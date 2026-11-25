import java.util.Optional;

/** Exercita as cinco operacoes CRUD pedidas no enunciado, pela interface LivroDAO. */
public class TesteLivroDAO {

    public static void main(String[] args) {
        // A variavel e do tipo da INTERFACE, nao da implementacao: e isso que permite
        // trocar a persistencia sem mexer neste codigo.
        LivroDAO dao = new LivroDAOImpl();

        System.out.println("=== 1. Adicionar ===");
        dao.adicionar(new Livro(1, "O Nome do Vento", "Patrick Rothfuss", 2007));
        dao.adicionar(new Livro(2, "Dom Casmurro", "Machado de Assis", 1899));
        dao.adicionar(new Livro(3, "Clean Code", "Robert C. Martin", 2008));
        dao.listarTodos().forEach(l -> System.out.println("  + " + l));

        System.out.println("\n=== 2. Buscar por ID ===");
        System.out.println("  id 2 -> " + dao.buscarPorId(2).orElseThrow());
        Optional<Livro> inexistente = dao.buscarPorId(99);
        System.out.println("  id 99 -> " + (inexistente.isPresent() ? inexistente.get() : "nao encontrado"));

        System.out.println("\n=== 3. Listar todos ===");
        System.out.println("  total: " + dao.listarTodos().size());
        dao.listarTodos().forEach(l -> System.out.println("  - " + l));

        System.out.println("\n=== 4. Atualizar ===");
        boolean atualizado = dao.atualizar(new Livro(3, "Clean Code (2a edicao)", "Robert C. Martin", 2009));
        System.out.println("  atualizou o id 3? " + atualizado);
        System.out.println("  agora: " + dao.buscarPorId(3).orElseThrow());
        System.out.println("  atualizar id 99? " + dao.atualizar(new Livro(99, "x", "y", 2000)));

        System.out.println("\n=== 5. Excluir ===");
        System.out.println("  excluiu o id 1? " + dao.excluir(1));
        System.out.println("  excluir id 1 de novo? " + dao.excluir(1));
        System.out.println("  restam: " + dao.listarTodos().size());
        dao.listarTodos().forEach(l -> System.out.println("  - " + l));

        System.out.println("\n=== ID duplicado e recusado ===");
        try {
            dao.adicionar(new Livro(2, "Outro titulo", "Outro autor", 2020));
        } catch (IllegalArgumentException e) {
            System.out.println("  Recusado: " + e.getMessage());
        }
    }
}
