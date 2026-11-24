import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementacao do LivroDAO usando uma lista em memoria.
 *
 * <p>Se amanha a biblioteca passar a usar JDBC, basta criar LivroDAOJdbc implementando a
 * mesma interface: a classe de teste e qualquer outro cliente continuam iguais.
 */
public class LivroDAOImpl implements LivroDAO {

    private final List<Livro> livros = new ArrayList<>();

    @Override
    public void adicionar(Livro livro) {
        if (buscarPorId(livro.getId()).isPresent()) {
            throw new IllegalArgumentException("Ja existe livro com o ID " + livro.getId());
        }
        livros.add(livro);
    }

    @Override
    public Optional<Livro> buscarPorId(int id) {
        return livros.stream().filter(l -> l.getId() == id).findFirst();
    }

    @Override
    public List<Livro> listarTodos() {
        return Collections.unmodifiableList(livros);
    }

    @Override
    public boolean atualizar(Livro livro) {
        Optional<Livro> existente = buscarPorId(livro.getId());
        if (existente.isEmpty()) {
            return false;
        }
        Livro alvo = existente.get();
        alvo.setTitulo(livro.getTitulo());
        alvo.setAutor(livro.getAutor());
        alvo.setAno(livro.getAno());
        return true;
    }

    @Override
    public boolean excluir(int id) {
        return livros.removeIf(l -> l.getId() == id);
    }
}
