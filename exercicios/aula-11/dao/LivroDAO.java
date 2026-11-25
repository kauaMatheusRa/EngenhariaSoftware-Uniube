import java.util.List;
import java.util.Optional;

/**
 * Contrato de acesso a dados de Livro (padrao DAO).
 *
 * <p>Quem usa esta interface nao sabe se os livros estao em uma lista na memoria, em um
 * banco via JDBC ou em um arquivo. Trocar a implementacao nao afeta quem chama.
 */
public interface LivroDAO {

    /** Adiciona um novo livro. */
    void adicionar(Livro livro);

    /** Busca um livro pelo ID; vazio se nao existir. */
    Optional<Livro> buscarPorId(int id);

    /** Lista todos os livros disponiveis. */
    List<Livro> listarTodos();

    /** Atualiza os dados de um livro existente. Devolve false se o ID nao existir. */
    boolean atualizar(Livro livro);

    /** Exclui um livro pelo ID. Devolve false se o ID nao existir. */
    boolean excluir(int id);
}
