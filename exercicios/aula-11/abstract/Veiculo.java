/**
 * Exercicio 1 da Aula 11 (Design Patterns) — classe abstrata.
 *
 * <p>Veiculo define o contrato comum e deixa para as subclasses o que varia. Nenhum
 * objeto Veiculo pode ser criado diretamente: so Carro ou Bicicleta.
 */
public abstract class Veiculo {

    private final String descricao;

    protected Veiculo(String descricao) {
        this.descricao = descricao;
    }

    /** Exibe as informacoes especificas do veiculo. */
    public abstract String exibir_info();

    /** Calcula o custo de operacao do veiculo. */
    public abstract double calcular_custo();

    protected String getDescricao() {
        return descricao;
    }
}
