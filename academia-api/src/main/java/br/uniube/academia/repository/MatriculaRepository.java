package br.uniube.academia.repository;

import br.uniube.academia.domain.Matricula;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findFirstByAlunoIdOrderByDataInicioDesc(Long alunoId);
}
