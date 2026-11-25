package br.uniube.academia.repository;

import br.uniube.academia.domain.FichaTreino;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaTreinoRepository extends JpaRepository<FichaTreino, Long> {

    Optional<FichaTreino> findByAlunoIdAndVigenteTrue(Long alunoId);
}
