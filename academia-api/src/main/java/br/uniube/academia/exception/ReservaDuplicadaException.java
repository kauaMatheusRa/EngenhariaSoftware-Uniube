package br.uniube.academia.exception;

/** O aluno ja tem vaga ou lugar na espera desta aula. */
public class ReservaDuplicadaException extends RuntimeException {

    public ReservaDuplicadaException(String modalidade) {
        super("Voce ja tem reserva para a aula de " + modalidade);
    }
}
