package br.uniube.academia.exception;

/** US-10, criterio 5: aluno bloqueado por inadimplencia nao reserva aula. */
public class AcessoBloqueadoException extends RuntimeException {

    public AcessoBloqueadoException(String nome) {
        super("Acesso de " + nome + " esta bloqueado: regularize a mensalidade para reservar aulas");
    }
}
