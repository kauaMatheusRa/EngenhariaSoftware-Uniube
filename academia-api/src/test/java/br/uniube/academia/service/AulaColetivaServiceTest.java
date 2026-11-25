package br.uniube.academia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.AulaColetiva;
import br.uniube.academia.domain.Instrutor;
import br.uniube.academia.domain.Reserva;
import br.uniube.academia.domain.StatusReserva;
import br.uniube.academia.dto.AulaMapper;
import br.uniube.academia.dto.NovaReservaRequest;
import br.uniube.academia.dto.ReservaResponse;
import br.uniube.academia.exception.AcessoBloqueadoException;
import br.uniube.academia.exception.CancelamentoForaDoPrazoException;
import br.uniube.academia.exception.ReservaDuplicadaException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.AulaColetivaRepository;
import br.uniube.academia.repository.ReservaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** US-10 — agendamento de aula coletiva (telas 07 e 08 do prototipo). */
@ExtendWith(MockitoExtension.class)
class AulaColetivaServiceTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 3, 15, 14, 0);
    private static final Clock RELOGIO =
            Clock.fixed(AGORA.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    @Mock
    private AulaColetivaRepository aulaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    private AulaColetivaService service;
    private Aluno marina;
    private AulaColetiva spinning;

    @BeforeEach
    void preparar() {
        service = new AulaColetivaService(
                aulaRepository, reservaRepository, alunoRepository, new AulaMapper(), RELOGIO);
        marina = new Aluno("202600001", "Marina Souza", "12345678901", LocalDate.of(1999, 3, 14), "34999990000");
        // aula as 19:00; agora sao 14:00
        spinning = new AulaColetiva("Spinning", AGORA.withHour(19).withMinute(0), 2, new Instrutor("Rafael", "CREF 1"));
    }

    @Test
    @DisplayName("criterio 1: havendo vaga, a reserva e confirmada e sobra uma vaga")
    void confirmaQuandoHaVaga() {
        // Arrange
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(marina));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(spinning));
        when(reservaRepository.countByAulaIdAndStatus(any(), eq(StatusReserva.CONFIRMADA)))
                .thenReturn(0, 1);
        when(reservaRepository.existsByAulaIdAndAlunoIdAndStatusIn(any(), any(), any()))
                .thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReservaResponse resposta = service.reservar(1L, new NovaReservaRequest(1L));

        // Assert
        assertThat(resposta.status()).isEqualTo(StatusReserva.CONFIRMADA);
        assertThat(resposta.vagasRestantes()).isEqualTo(1);
        assertThat(resposta.podeCancelarAte()).isEqualTo(AGORA.withHour(17).withMinute(0));
    }

    @Test
    @DisplayName("criterio 2: turma lotada manda o aluno para a lista de espera")
    void turmaLotadaVaiParaEspera() {
        // Arrange — as 2 vagas ja estao ocupadas
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(marina));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(spinning));
        when(reservaRepository.countByAulaIdAndStatus(any(), eq(StatusReserva.CONFIRMADA)))
                .thenReturn(2);
        when(reservaRepository.existsByAulaIdAndAlunoIdAndStatusIn(any(), any(), any()))
                .thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReservaResponse resposta = service.reservar(1L, new NovaReservaRequest(1L));

        // Assert
        assertThat(resposta.status()).isEqualTo(StatusReserva.LISTA_DE_ESPERA);
        assertThat(resposta.vagasRestantes()).isZero();
    }

    @Test
    @DisplayName("criterio 5: aluno bloqueado por inadimplencia nao reserva")
    void inadimplenteNaoReserva() {
        // Arrange
        marina.bloquearAcesso();
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(marina));

        // Act + Assert
        assertThatThrownBy(() -> service.reservar(1L, new NovaReservaRequest(1L)))
                .isInstanceOf(AcessoBloqueadoException.class)
                .hasMessageContaining("Marina Souza");
        verify(reservaRepository, never()).save(any());
        verify(aulaRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("o mesmo aluno nao reserva a mesma aula duas vezes")
    void reservaDuplicada() {
        // Arrange
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(marina));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(spinning));
        when(reservaRepository.existsByAulaIdAndAlunoIdAndStatusIn(any(), any(), any()))
                .thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> service.reservar(1L, new NovaReservaRequest(1L)))
                .isInstanceOf(ReservaDuplicadaException.class);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("criterio 3: cancelar dentro do prazo promove o primeiro da lista de espera")
    void cancelamentoPromoveAEspera() {
        // Arrange
        Reserva confirmada = new Reserva(marina, spinning, AGORA.minusDays(1), StatusReserva.CONFIRMADA);
        Aluno outro = new Aluno("202600002", "Bruno", "98765432100", LocalDate.of(1995, 7, 22), "34988880000");
        Reserva naEspera = new Reserva(outro, spinning, AGORA.minusHours(2), StatusReserva.LISTA_DE_ESPERA);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(confirmada));
        when(reservaRepository.findFirstByAulaIdAndStatusOrderByDataHora(any(), eq(StatusReserva.LISTA_DE_ESPERA)))
                .thenReturn(Optional.of(naEspera));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReservaResponse resposta = service.cancelar(1L);

        // Assert
        assertThat(resposta.status()).isEqualTo(StatusReserva.CANCELADA);
        assertThat(naEspera.getStatus()).isEqualTo(StatusReserva.CONFIRMADA);
    }

    @Test
    @DisplayName("criterio 3: faltando menos de 2 h, o cancelamento e recusado")
    void cancelamentoForaDoPrazo() {
        // Arrange — aula as 15:00, agora sao 14:00: falta 1 hora
        AulaColetiva emUmaHora =
                new AulaColetiva("Funcional", AGORA.plusHours(1), 10, new Instrutor("Camila", "CREF 2"));
        Reserva reserva = new Reserva(marina, emUmaHora, AGORA.minusDays(1), StatusReserva.CONFIRMADA);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act + Assert
        assertThatThrownBy(() -> service.cancelar(1L))
                .isInstanceOf(CancelamentoForaDoPrazoException.class)
                .hasMessageContaining("13:00");
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("a grade do dia mostra as vagas restantes de cada aula")
    void gradeDoDia() {
        // Arrange
        when(aulaRepository.findByDataHoraBetweenOrderByDataHora(any(), any()))
                .thenReturn(List.of(spinning));
        when(reservaRepository.countByAulaIdAndStatus(any(), eq(StatusReserva.CONFIRMADA)))
                .thenReturn(1);

        // Act
        var grade = service.gradeDoDia(AGORA.toLocalDate());

        // Assert
        assertThat(grade).hasSize(1);
        assertThat(grade.get(0).modalidade()).isEqualTo("Spinning");
        assertThat(grade.get(0).vagasRestantes()).isEqualTo(1);
        assertThat(grade.get(0).lotada()).isFalse();
    }
}
