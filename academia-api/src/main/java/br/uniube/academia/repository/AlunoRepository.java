package br.uniube.academia.repository;

import br.uniube.academia.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso a dados de Aluno.
 *
 * <p>Quem chama este repositorio nao precisa conhecer SQL nem detalhes de conexao.
 */
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsByCpf(String cpf);
}
