package br.uniube.academia.repository;

import br.uniube.academia.domain.Reserva;
import br.uniube.academia.domain.StatusReserva;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    int countByAulaIdAndStatus(Long aulaId, StatusReserva status);

    Optional<Reserva> findFirstByAulaIdAndStatusOrderByDataHora(Long aulaId, StatusReserva status);

    boolean existsByAulaIdAndAlunoIdAndStatusIn(Long aulaId, Long alunoId, List<StatusReserva> status);

    List<Reserva> findByAulaIdAndStatus(Long aulaId, StatusReserva status);
}
