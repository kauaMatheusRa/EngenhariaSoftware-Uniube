/** Carro: alem do tipo de veiculo, guarda o combustivel usado. */
public class Carro extends Veiculo {

    /** Custo ficticio por tipo de combustivel, conforme o enunciado. */
    private static final double CUSTO_GASOLINA = 0.2;
    private static final double CUSTO_DISEL = 0.3;

    private final String tipoCombustivel;

    public Carro(String tipoCombustivel) {
        super("Carro");
        this.tipoCombustivel = tipoCombustivel;
    }

    @Override
    public String exibir_info() {
        return "Tipo de veiculo: %s | Combustivel: %s".formatted(getDescricao(), tipoCombustivel);
    }

    @Override
    public double calcular_custo() {
        return switch (tipoCombustivel.toLowerCase()) {
            case "gasolina" -> CUSTO_GASOLINA;
            case "disel", "diesel", "dísel" -> CUSTO_DISEL;
            default -> throw new IllegalArgumentException(
                    "Combustivel nao previsto: " + tipoCombustivel);
        };
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }
}
