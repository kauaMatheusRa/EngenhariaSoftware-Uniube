import java.util.List;

/**
 * Classe de teste do exercicio de classe abstrata.
 *
 * <p>O ponto do exercicio aparece no laco final: o codigo percorre uma lista de Veiculo
 * sem saber se cada item e Carro ou Bicicleta. Cada objeto responde do seu jeito — e
 * polimorfismo sustentado pela classe abstrata.
 */
public class Teste {

    public static void main(String[] args) {
        Carro carroGasolina = new Carro("gasolina");
        Carro carroDisel = new Carro("disel");
        Bicicleta bicicleta = new Bicicleta();

        List<Veiculo> frota = List.of(carroGasolina, carroDisel, bicicleta);

        System.out.println("=== Frota ===");
        for (Veiculo veiculo : frota) {
            System.out.printf("%-45s custo: %.2f%n", veiculo.exibir_info(), veiculo.calcular_custo());
        }

        System.out.println();
        System.out.println("=== Custo total da frota ===");
        double total = frota.stream().mapToDouble(Veiculo::calcular_custo).sum();
        System.out.printf("Total: %.2f%n", total);

        System.out.println();
        System.out.println("=== Combustivel nao previsto ===");
        try {
            new Carro("etanol").calcular_custo();
        } catch (IllegalArgumentException e) {
            System.out.println("Recusado: " + e.getMessage());
        }
    }
}
