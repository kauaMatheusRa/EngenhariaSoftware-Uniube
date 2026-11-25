package br.uniube.academia.repository;

import br.uniube.academia.domain.AulaColetiva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaColetivaRepository extends JpaRepository<AulaColetiva, Long> {

    List<AulaColetiva> findByDataHoraBetweenOrderByDataHora(LocalDateTime inicio, LocalDateTime fim);
}
