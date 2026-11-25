package br.uniube.academia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.DivisaoTreino;
import br.uniube.academia.domain.Exercicio;
import br.uniube.academia.domain.FichaTreino;
import br.uniube.academia.domain.Instrutor;
import br.uniube.academia.domain.ItemTreino;
import br.uniube.academia.domain.SessaoTreino;
import br.uniube.academia.dto.IniciarSessaoRequest;
import br.uniube.academia.dto.RegistrarExecucaoRequest;
import br.uniube.academia.dto.SessaoResponse;
import br.uniube.academia.dto.TreinoMapper;
import br.uniube.academia.exception.SessaoJaFinalizadaException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.FichaTreinoRepository;
import br.uniube.academia.repository.ItemTreinoRepository;
import br.uniube.academia.repository.SessaoTreinoRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** US-05 — execucao do treino (telas 05 e 06 do prototipo). */
@ExtendWith(MockitoExtension.class)
class SessaoTreinoServiceTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 3, 15, 6, 12);
    private static final Clock RELOGIO =
            Clock.fixed(AGORA.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    @Mock
    private SessaoTreinoRepository sessaoRepository;

    @Mock
    private FichaTreinoRepository fichaRepository;

    @Mock
    private ItemTreinoRepository itemRepository;

    @Mock
    private AlunoRepository alunoRepository;

    private SessaoTreinoService service;
    private Aluno marina;
    private FichaTreino ficha;

    @BeforeEach
    void preparar() {
        service = new SessaoTreinoService(
                sessaoRepository, fichaRepository, itemRepository, alunoRepository, new TreinoMapper(), RELOGIO);

        marina = new Aluno("202600001", "Marina Souza", "12345678901", LocalDate.of(1999, 3, 14), "34999990000");
        ficha = new FichaTreino(marina, new Instrutor("Rafael", "CREF 1"), "Treino A", LocalDate.of(2026, 9, 1));
        for (int i = 1; i <= 4; i++) {
            ficha.adicionarItem(new ItemTreino(
                    new Exercicio("Exercicio " + i, "Peito", "orientacao", "video"),
                    DivisaoTreino.A,
                    i,
                    4,
                    12,
                    30,
                    60));
        }
    }

    private SessaoTreino sessaoAberta() {
        return new SessaoTreino(marina, ficha, DivisaoTreino.A, AGORA);
    }

    private void repositorioDevolveOqueRecebe() {
        when(sessaoRepository.save(any(SessaoTreino.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("iniciar: a sessao nasce com 0 de 4 exercicios (0%)")
    void iniciaVazia() {
        // Arrange
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(marina));
        when(fichaRepository.findByAlunoIdAndVigenteTrue(1L)).thenReturn(Optional.of(ficha));
        repositorioDevolveOqueRecebe();

        // Act
        SessaoResponse resposta = service.iniciar(new IniciarSessaoRequest(1L, DivisaoTreino.A));

        // Assert
        assertThat(resposta.exerciciosPrescritos()).isEqualTo(4);
        assertThat(resposta.exerciciosConcluidos()).isZero();
        assertThat(resposta.percentual()).isZero();
        assertThat(resposta.finalizada()).isFalse();
    }

    @Test
    @DisplayName("criterio 1: marcar um de quatro exercicios leva o progresso a 25%")
    void progressoDe25PorCento() {
        // Arrange
        SessaoTreino sessao = sessaoAberta();
        ItemTreino primeiro = ficha.itensDa(DivisaoTreino.A).get(0);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(itemRepository.findById(any())).thenReturn(Optional.of(primeiro));
        repositorioDevolveOqueRecebe();

        // Act
        SessaoResponse resposta = service.registrarExecucao(1L, new RegistrarExecucaoRequest(1L, 32.0));

        // Assert
        assertThat(resposta.exerciciosConcluidos()).isEqualTo(1);
        assertThat(resposta.percentual()).isEqualTo(25);
    }

    @Test
    @DisplayName("criterio 2: a carga real do dia nao altera a prescricao da ficha")
    void cargaRealNaoMudaAFicha() {
        // Arrange
        SessaoTreino sessao = sessaoAberta();
        ItemTreino primeiro = ficha.itensDa(DivisaoTreino.A).get(0);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(itemRepository.findById(any())).thenReturn(Optional.of(primeiro));
        repositorioDevolveOqueRecebe();

        // Act — o aluno usou 32 kg, a ficha prescreve 30
        service.registrarExecucao(1L, new RegistrarExecucaoRequest(1L, 32.0));

        // Assert
        assertThat(primeiro.getCarga()).isEqualTo(30.0);
        assertThat(sessao.getExecucoes().get(0).getCargaReal()).isEqualTo(32.0);
    }

    @Test
    @DisplayName("sem carga informada, vale a prescrita na ficha")
    void semCargaUsaAPrescrita() {
        // Arrange
        SessaoTreino sessao = sessaoAberta();
        ItemTreino primeiro = ficha.itensDa(DivisaoTreino.A).get(0);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(itemRepository.findById(any())).thenReturn(Optional.of(primeiro));
        repositorioDevolveOqueRecebe();

        // Act
        service.registrarExecucao(1L, new RegistrarExecucaoRequest(1L, null));

        // Assert
        assertThat(sessao.getExecucoes().get(0).getCargaReal()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("criterio 3: finalizar grava o fim e a duracao da sessao")
    void finalizaComDuracao() {
        // Arrange — sessao comecou 52 minutos atras
        SessaoTreino sessao = new SessaoTreino(marina, ficha, DivisaoTreino.A, AGORA.minusMinutes(52));
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        repositorioDevolveOqueRecebe();

        // Act
        SessaoResponse resposta = service.finalizar(1L);

        // Assert
        assertThat(resposta.finalizada()).isTrue();
        assertThat(resposta.duracaoMinutos()).isEqualTo(52);
        assertThat(resposta.fim()).isEqualTo(AGORA);
    }

    @Test
    @DisplayName("sessao finalizada nao aceita novo registro")
    void sessaoFinalizadaRecusaRegistro() {
        // Arrange
        SessaoTreino sessao = sessaoAberta();
        sessao.finalizar(AGORA);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(itemRepository.findById(any())).thenReturn(Optional.of(ficha.itensDa(DivisaoTreino.A).get(0)));

        // Act + Assert
        assertThatThrownBy(() -> service.registrarExecucao(1L, new RegistrarExecucaoRequest(1L, 30.0)))
                .isInstanceOf(SessaoJaFinalizadaException.class);
        verify(sessaoRepository, never()).save(any());
    }
}
