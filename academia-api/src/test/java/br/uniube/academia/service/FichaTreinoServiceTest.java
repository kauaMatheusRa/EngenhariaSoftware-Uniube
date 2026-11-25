package br.uniube.academia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.DivisaoTreino;
import br.uniube.academia.domain.Exercicio;
import br.uniube.academia.domain.FichaTreino;
import br.uniube.academia.domain.Instrutor;
import br.uniube.academia.domain.ItemTreino;
import br.uniube.academia.dto.FichaResponse;
import br.uniube.academia.dto.TreinoMapper;
import br.uniube.academia.exception.FichaNaoEncontradaException;
import br.uniube.academia.exception.RecursoNaoEncontradoException;
import br.uniube.academia.repository.FichaTreinoRepository;
import br.uniube.academia.repository.ItemTreinoRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** US-04 — consulta da ficha pelo app do aluno (telas 03 e 04 do prototipo). */
@ExtendWith(MockitoExtension.class)
class FichaTreinoServiceTest {

    @Mock
    private FichaTreinoRepository fichaRepository;

    @Mock
    private ItemTreinoRepository itemRepository;

    private FichaTreinoService service;
    private FichaTreino ficha;

    @BeforeEach
    void preparar() {
        service = new FichaTreinoService(fichaRepository, itemRepository, new TreinoMapper());

        Aluno marina = new Aluno("202600001", "Marina Souza", "12345678901", LocalDate.of(1999, 3, 14), "34999990000");
        Instrutor rafael = new Instrutor("Rafael Antunes", "CREF 012345-G/MG");
        ficha = new FichaTreino(marina, rafael, "Treino A - Peito e triceps", LocalDate.of(2026, 9, 1));

        Exercicio supino = new Exercicio("Supino reto", "Peito", "Desca a barra ate o peito.", "video/supino");
        Exercicio triceps = new Exercicio("Triceps na polia", "Triceps", "Cotovelos colados.", "video/triceps");
        Exercicio puxada = new Exercicio("Puxada frontal", "Costas", "Junte as escapulas.", "video/puxada");

        ficha.adicionarItem(new ItemTreino(supino, DivisaoTreino.A, 1, 4, 12, 30, 60));
        ficha.adicionarItem(new ItemTreino(triceps, DivisaoTreino.A, 2, 3, 15, 25, 45));
        ficha.adicionarItem(new ItemTreino(puxada, DivisaoTreino.B, 1, 4, 12, 45, 60));
    }

    @Test
    @DisplayName("criterio 1: sem divisao informada, o app abre na primeira (Treino A)")
    void abreNaPrimeiraDivisao() {
        // Arrange
        when(fichaRepository.findByAlunoIdAndVigenteTrue(1L)).thenReturn(Optional.of(ficha));

        // Act
        FichaResponse resposta = service.buscarFichaVigente(1L, null);

        // Assert
        assertThat(resposta.divisaoSelecionada()).isEqualTo(DivisaoTreino.A);
        assertThat(resposta.divisoes()).containsExactly(DivisaoTreino.A, DivisaoTreino.B);
        assertThat(resposta.exercicios()).hasSize(2);
        assertThat(resposta.instrutor()).isEqualTo("Rafael Antunes");
    }

    @Test
    @DisplayName("criterio 1: a aba escolhida filtra os exercicios daquela divisao")
    void filtraPelaDivisao() {
        // Arrange
        when(fichaRepository.findByAlunoIdAndVigenteTrue(1L)).thenReturn(Optional.of(ficha));

        // Act
        FichaResponse resposta = service.buscarFichaVigente(1L, DivisaoTreino.B);

        // Assert
        assertThat(resposta.exercicios()).hasSize(1);
        assertThat(resposta.exercicios().get(0).exercicio()).isEqualTo("Puxada frontal");
    }

    @Test
    @DisplayName("criterio 3: sem ficha vigente, a mensagem orienta a procurar o instrutor")
    void semFichaVigente() {
        // Arrange
        when(fichaRepository.findByAlunoIdAndVigenteTrue(9L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.buscarFichaVigente(9L, null))
                .isInstanceOf(FichaNaoEncontradaException.class)
                .hasMessageContaining("Procure seu instrutor");
    }

    @Test
    @DisplayName("criterio 2: o detalhe traz orientacao de execucao e video")
    void detalheDoExercicio() {
        // Arrange
        ItemTreino item = ficha.itensDa(DivisaoTreino.A).get(0);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        // Act
        var detalhe = service.detalharExercicio(10L);

        // Assert
        assertThat(detalhe.orientacao()).isEqualTo("Desca a barra ate o peito.");
        assertThat(detalhe.videoUrl()).isEqualTo("video/supino");
        assertThat(detalhe.series()).isEqualTo(4);
        assertThat(detalhe.descansoSegundos()).isEqualTo(60);
    }

    @Test
    @DisplayName("exercicio inexistente devolve recurso nao encontrado")
    void exercicioInexistente() {
        // Arrange
        when(itemRepository.findById(404L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.detalharExercicio(404L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
