package br.uniube.academia.repository;

import br.uniube.academia.domain.SessaoTreino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoTreinoRepository extends JpaRepository<SessaoTreino, Long> {

    List<SessaoTreino> findByAlunoIdOrderByInicioDesc(Long alunoId);
}
