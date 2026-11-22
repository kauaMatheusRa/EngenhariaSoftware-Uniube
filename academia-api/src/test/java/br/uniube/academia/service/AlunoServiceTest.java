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
import br.uniube.academia.exception.CpfDuplicadoException;
import br.uniube.academia.exception.PlanoNaoEncontradoException;
import br.uniube.academia.exception.ResponsavelObrigatorioException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.MatriculaRepository;
import br.uniube.academia.repository.PlanoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Regras da US-01 verificadas sem subir servidor nem banco. */
@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PlanoRepository planoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoMapper mapperMock;

    private final AlunoMapper mapperReal = new AlunoMapper();

    @InjectMocks
    private AlunoService service;

    private static final Plano PLANO_MENSAL = new Plano(1L, "Mensal", new BigDecimal("119.90"), 1);

    private NovoAlunoRequest adulto() {
        return new NovoAlunoRequest(
                "Marina Souza", "12345678901", LocalDate.of(1999, 3, 14), "34999990000", 1L, null, null);
    }

    private NovoAlunoRequest menorSemResponsavel() {
        return new NovoAlunoRequest(
                "Pedro Lima", "98765432100", LocalDate.now().minusYears(15), "34988880000", 1L, null, null);
    }

    @Test
    @DisplayName("criterio 2: CPF ja cadastrado impede a gravacao")
    void recusaCpfDuplicado() {
        when(alunoRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(adulto()))
                .isInstanceOf(CpfDuplicadoException.class)
                .hasMessageContaining("12345678901");

        verify(alunoRepository, never()).save(any());
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criterio 4: menor de 18 anos sem responsavel nao e cadastrado")
    void recusaMenorSemResponsavel() {
        NovoAlunoRequest dto = menorSemResponsavel();
        when(alunoRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(planoRepository.findById(1L)).thenReturn(Optional.of(PLANO_MENSAL));
        when(mapperMock.toEntity(any(), any()))
                .thenAnswer(inv -> mapperReal.toEntity(inv.getArgument(0), inv.getArgument(1)));

        assertThatThrownBy(() -> service.cadastrar(dto))
                .isInstanceOf(ResponsavelObrigatorioException.class)
                .hasMessageContaining("Pedro Lima");

        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("plano inexistente interrompe o cadastro")
    void recusaPlanoInexistente() {
        when(alunoRepository.existsByCpf(any())).thenReturn(false);
        when(planoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(adulto())).isInstanceOf(PlanoNaoEncontradoException.class);

        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("criterios 1 e 5: aluno nasce ATIVO, com matricula unica e ficha completa")
    void cadastraAlunoAtivoComFicha() {
        NovoAlunoRequest dto = adulto();
        when(alunoRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(alunoRepository.count()).thenReturn(7L);
        when(planoRepository.findById(1L)).thenReturn(Optional.of(PLANO_MENSAL));
        when(mapperMock.toEntity(any(), any()))
                .thenAnswer(inv -> mapperReal.toEntity(inv.getArgument(0), inv.getArgument(1)));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapperMock.toResponse(any(Matricula.class)))
                .thenAnswer(inv -> mapperReal.toResponse(inv.getArgument(0)));

        AlunoResponse resposta = service.cadastrar(dto);

        assertThat(resposta.status()).isEqualTo(StatusAluno.ATIVO);
        assertThat(resposta.matricula()).endsWith("00008");
        assertThat(resposta.plano()).isEqualTo("Mensal");
        assertThat(resposta.valorMensalidade()).isEqualByComparingTo("119.90");
        assertThat(resposta.dataInicio()).isEqualTo(LocalDate.now());
        assertThat(resposta.diaVencimento()).isEqualTo(LocalDate.now().getDayOfMonth());
    }
}
