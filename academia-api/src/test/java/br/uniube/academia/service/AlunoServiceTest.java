package br.uniube.academia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.Matricula;
import br.uniube.academia.domain.Plano;
import br.uniube.academia.domain.StatusAluno;
import br.uniube.academia.dto.AlunoMapper;
import br.uniube.academia.dto.AlunoResponse;
import br.uniube.academia.dto.NovoAlunoRequest;
import br.uniube.academia.exception.AlunoNaoEncontradoException;
import br.uniube.academia.exception.CpfDuplicadoException;
import br.uniube.academia.exception.PlanoNaoEncontradoException;
import br.uniube.academia.exception.ResponsavelObrigatorioException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.MatriculaRepository;
import br.uniube.academia.repository.PlanoRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Suite unitaria do cadastro de aluno (US-01).
 *
 * <p>Os cenarios foram listados antes do codigo, em docs/cenarios-de-teste.md. Cada teste
 * segue Arrange-Act-Assert e nao depende de banco, rede nem do relogio real: o Clock e
 * fixo, entao os cenarios de fronteira de idade dao sempre o mesmo resultado.
 */
@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    /** Data de referencia de todos os testes. Nada aqui depende do dia em que a suite roda. */
    private static final LocalDate HOJE = LocalDate.of(2026, 3, 15);

    private static final Clock RELOGIO_FIXO =
            Clock.fixed(HOJE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private static final Plano MENSAL = new Plano(1L, "Mensal", new BigDecimal("119.90"), 1);
    private static final Plano TRIMESTRAL = new Plano(2L, "Trimestral", new BigDecimal("99.90"), 3);

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    private final AlunoMapper mapper = new AlunoMapper();

    private AlunoService service() {
        return new AlunoService(alunoRepository, planoRepository, matriculaRepository, mapper, RELOGIO_FIXO);
    }

    private NovoAlunoRequest pedido(LocalDate nascimento, Long planoId, String respNome, String respCpf) {
        return new NovoAlunoRequest(
                "Marina Souza", "12345678901", nascimento, "34999990000", planoId, respNome, respCpf);
    }

    /** Faz o repositorio devolver a entidade que recebeu, como um save real faria. */
    private void repositoriosDevolvemOqueRecebem() {
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ----------------------------------------------------------------- caminho feliz

    @Nested
    @DisplayName("caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("cenario 1: adulto com dados validos nasce ATIVO e com ficha completa")
        void cadastraAdulto() {
            // Arrange
            NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 1L, null, null);
            when(alunoRepository.existsByCpf("12345678901")).thenReturn(false);
            when(alunoRepository.count()).thenReturn(7L);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));
            repositoriosDevolvemOqueRecebem();

            // Act
            AlunoResponse resposta = service().cadastrar(dto);

            // Assert
            assertThat(resposta.status()).isEqualTo(StatusAluno.ATIVO);
            assertThat(resposta.nome()).isEqualTo("Marina Souza");
            assertThat(resposta.plano()).isEqualTo("Mensal");
            assertThat(resposta.valorMensalidade()).isEqualByComparingTo("119.90");
            assertThat(resposta.dataInicio()).isEqualTo(HOJE);
            assertThat(resposta.diaVencimento()).isEqualTo(15);
        }

        @Test
        @DisplayName("cenario 6: menor com nome e CPF do responsavel e cadastrado")
        void cadastraMenorComResponsavel() {
            // Arrange
            NovoAlunoRequest dto = pedido(HOJE.minusYears(15), 1L, "Ana Lima", "99988877766");
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));
            repositoriosDevolvemOqueRecebem();

            // Act
            AlunoResponse resposta = service().cadastrar(dto);

            // Assert
            assertThat(resposta.status()).isEqualTo(StatusAluno.ATIVO);
            verify(alunoRepository).save(any(Aluno.class));
        }
    }

    // -------------------------------------------------------------------- fronteiras

    @Nested
    @DisplayName("fronteiras")
    class Fronteiras {

        @Test
        @DisplayName("cenario 4: faz 18 anos exatamente hoje — nao exige responsavel")
        void dezoitoAnosHoje() {
            // Arrange
            NovoAlunoRequest dto = pedido(HOJE.minusYears(18), 1L, null, null);
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));
            repositoriosDevolvemOqueRecebem();

            // Act
            AlunoResponse resposta = service().cadastrar(dto);

            // Assert
            assertThat(resposta.status()).isEqualTo(StatusAluno.ATIVO);
        }

        @Test
        @DisplayName("cenario 5: faz 18 anos amanha — ainda exige responsavel")
        void dezoitoAnosAmanha() {
            // Arrange
            NovoAlunoRequest dto = pedido(HOJE.minusYears(18).plusDays(1), 1L, null, null);
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));

            // Act + Assert
            assertThatThrownBy(() -> service().cadastrar(dto))
                    .isInstanceOf(ResponsavelObrigatorioException.class);
            verify(alunoRepository, never()).save(any());
        }

        @Test
        @DisplayName("cenario 8: primeiro aluno da base recebe matricula terminada em 00001")
        void primeiraMatricula() {
            // Arrange
            NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 1L, null, null);
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(alunoRepository.count()).thenReturn(0L);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));
            repositoriosDevolvemOqueRecebem();

            // Act
            AlunoResponse resposta = service().cadastrar(dto);

            // Assert
            assertThat(resposta.matricula()).isEqualTo("202600001");
        }

        @Test
        @DisplayName("cenario 9: com 7 alunos na base, o proximo e 00008")
        void matriculaSegueASequencia() {
            // Arrange
            NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 1L, null, null);
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(alunoRepository.count()).thenReturn(7L);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));
            repositoriosDevolvemOqueRecebem();

            // Act
            AlunoResponse resposta = service().cadastrar(dto);

            // Assert
            assertThat(resposta.matricula()).isEqualTo("202600008");
        }
    }

    // ------------------------------------------------------------------------ erros

    @Nested
    @DisplayName("erros")
    class Erros {

        @Test
        @DisplayName("cenario 2: CPF ja cadastrado interrompe antes de qualquer gravacao")
        void cpfDuplicado() {
            // Arrange
            NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 1L, null, null);
            when(alunoRepository.existsByCpf("12345678901")).thenReturn(true);

            // Act + Assert
            assertThatThrownBy(() -> service().cadastrar(dto))
                    .isInstanceOf(CpfDuplicadoException.class)
                    .hasMessageContaining("12345678901");
            verify(alunoRepository, never()).save(any());
            verify(matriculaRepository, never()).save(any());
            verify(planoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("cenario 3: plano inexistente interrompe o cadastro")
        void planoInexistente() {
            // Arrange
            NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 9L, null, null);
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(planoRepository.findById(9L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service().cadastrar(dto))
                    .isInstanceOf(PlanoNaoEncontradoException.class)
                    .hasMessageContaining("9");
            verify(alunoRepository, never()).save(any());
        }

        @Test
        @DisplayName("cenario 7: menor com responsavel pela metade e recusado")
        void responsavelIncompleto() {
            // Arrange — informou o nome, esqueceu o CPF
            NovoAlunoRequest dto = pedido(HOJE.minusYears(15), 1L, "Ana Lima", "   ");
            when(alunoRepository.existsByCpf(any())).thenReturn(false);
            when(planoRepository.findById(1L)).thenReturn(Optional.of(MENSAL));

            // Act + Assert
            assertThatThrownBy(() -> service().cadastrar(dto))
                    .isInstanceOf(ResponsavelObrigatorioException.class);
            verify(alunoRepository, never()).save(any());
        }

        @Test
        @DisplayName("cenario 11: ficha de aluno inexistente")
        void fichaDeAlunoInexistente() {
            // Arrange
            when(matriculaRepository.findFirstByAlunoIdOrderByDataInicioDesc(404L))
                    .thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service().buscarFicha(404L))
                    .isInstanceOf(AlunoNaoEncontradoException.class)
                    .hasMessageContaining("404");
        }
    }

    // ------------------------------------------------------------------- regra do plano

    @Test
    @DisplayName("cenario 10: plano trimestral encerra a matricula tres meses depois")
    void planoTrimestralDefineDataFim() {
        // Arrange
        NovoAlunoRequest dto = pedido(LocalDate.of(1999, 3, 14), 2L, null, null);
        when(alunoRepository.existsByCpf(any())).thenReturn(false);
        when(planoRepository.findById(2L)).thenReturn(Optional.of(TRIMESTRAL));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service().cadastrar(dto);

        // Assert — a matricula salva termina em 15/06/2026
        org.mockito.ArgumentCaptor<Matricula> capturada =
                org.mockito.ArgumentCaptor.forClass(Matricula.class);
        verify(matriculaRepository).save(capturada.capture());
        assertThat(capturada.getValue().getDataFim()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(capturada.getValue().getDiaVencimento()).isEqualTo(15);
    }
}
