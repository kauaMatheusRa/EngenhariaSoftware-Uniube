package br.uniube.academia.repository;

import br.uniube.academia.domain.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
}
