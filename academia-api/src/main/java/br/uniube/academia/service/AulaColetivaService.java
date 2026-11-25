package br.uniube.academia.service;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.AulaColetiva;
import br.uniube.academia.domain.Reserva;
import br.uniube.academia.domain.StatusReserva;
import br.uniube.academia.dto.AulaMapper;
import br.uniube.academia.dto.AulaResponse;
import br.uniube.academia.dto.NovaReservaRequest;
import br.uniube.academia.dto.ReservaResponse;
import br.uniube.academia.exception.AcessoBloqueadoException;
import br.uniube.academia.exception.AlunoNaoEncontradoException;
import br.uniube.academia.exception.CancelamentoForaDoPrazoException;
import br.uniube.academia.exception.RecursoNaoEncontradoException;
import br.uniube.academia.exception.ReservaDuplicadaException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.AulaColetivaRepository;
import br.uniube.academia.repository.ReservaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * US-10 — Agendamento de aula coletiva.
 *
 * <p>Corresponde as telas "07 - Aulas coletivas" e "08 - Reserva confirmada".
 */
@Service
public class AulaColetivaService {

    private final AulaColetivaRepository aulaRepository;
    private final ReservaRepository reservaRepository;
    private final AlunoRepository alunoRepository;
    private final AulaMapper mapper;
    private final Clock clock;

    public AulaColetivaService(
            AulaColetivaRepository aulaRepository,
            ReservaRepository reservaRepository,
            AlunoRepository alunoRepository,
            AulaMapper mapper,
            Clock clock) {
        this.aulaRepository = aulaRepository;
        this.reservaRepository = reservaRepository;
        this.alunoRepository = alunoRepository;
        this.mapper = mapper;
        this.clock = clock;
    }

    /** Grade do dia, com as vagas restantes de cada aula. */
    @Transactional(readOnly = true)
    public List<AulaResponse> gradeDoDia(LocalDate dia) {
        LocalDate alvo = dia != null ? dia : LocalDate.now(clock);
        return aulaRepository
                .findByDataHoraBetweenOrderByDataHora(alvo.atStartOfDay(), alvo.plusDays(1).atStartOfDay())
                .stream()
                .map(aula -> mapper.toResponse(aula, confirmadas(aula.getId())))
                .toList();
    }

    /**
     * US-10, criterios 1, 2 e 5: confirma se houver vaga, manda para a espera se estiver
     * lotada, e recusa aluno bloqueado por inadimplencia.
     */
    @Transactional
    public ReservaResponse reservar(Long aulaId, NovaReservaRequest dto) {
        Aluno aluno = alunoRepository
                .findById(dto.alunoId())
                .orElseThrow(() -> new AlunoNaoEncontradoException(dto.alunoId()));

        if (!aluno.estaAdimplente()) {
            throw new AcessoBloqueadoException(aluno.getNome());
        }

        AulaColetiva aula = buscarAula(aulaId);

        boolean jaTemReserva = reservaRepository.existsByAulaIdAndAlunoIdAndStatusIn(
                aulaId, aluno.getId(), List.of(StatusReserva.CONFIRMADA, StatusReserva.LISTA_DE_ESPERA));
        if (jaTemReserva) {
            throw new ReservaDuplicadaException(aula.getModalidade());
        }

        StatusReserva status =
                confirmadas(aulaId) < aula.getVagas() ? StatusReserva.CONFIRMADA : StatusReserva.LISTA_DE_ESPERA;

        Reserva reserva =
                reservaRepository.save(new Reserva(aluno, aula, LocalDateTime.now(clock), status));
        return mapper.toResponse(reserva, confirmadas(aulaId));
    }

    /** US-10, criterio 3: cancelar com 2 h de antecedencia libera a vaga e promove a espera. */
    @Transactional
    public ReservaResponse cancelar(Long reservaId) {
        Reserva reserva = reservaRepository
                .findById(reservaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva", reservaId));

        AulaColetiva aula = reserva.getAula();
        if (!aula.permiteCancelamentoEm(LocalDateTime.now(clock))) {
            throw new CancelamentoForaDoPrazoException(aula.limiteParaCancelar());
        }

        boolean liberaVaga = reserva.getStatus() == StatusReserva.CONFIRMADA;
        reserva.cancelar();
        reservaRepository.save(reserva);

        if (liberaVaga) {
            promoverPrimeiroDaEspera(aula.getId());
        }
        return mapper.toResponse(reserva, confirmadas(aula.getId()));
    }

    private void promoverPrimeiroDaEspera(Long aulaId) {
        reservaRepository
                .findFirstByAulaIdAndStatusOrderByDataHora(aulaId, StatusReserva.LISTA_DE_ESPERA)
                .ifPresent(proxima -> {
                    proxima.confirmar();
                    reservaRepository.save(proxima);
                });
    }

    private AulaColetiva buscarAula(Long aulaId) {
        return aulaRepository
                .findById(aulaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aula", aulaId));
    }

    private int confirmadas(Long aulaId) {
        return reservaRepository.countByAulaIdAndStatus(aulaId, StatusReserva.CONFIRMADA);
    }
}
