/** Bicicleta: nao tem atributo adicional e o custo de operacao e fixo. */
public class Bicicleta extends Veiculo {

    private static final double CUSTO_FIXO = 0.0;

    public Bicicleta() {
        super("Bicicleta");
    }

    @Override
    public String exibir_info() {
        return "Tipo de veiculo: %s".formatted(getDescricao());
    }

    @Override
    public double calcular_custo() {
        return CUSTO_FIXO;
    }
}
