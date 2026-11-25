package br.uniube.academia.exception;

/** Sessao encerrada nao aceita novos registros. */
public class SessaoJaFinalizadaException extends RuntimeException {

    public SessaoJaFinalizadaException(Long sessaoId) {
        super("Sessao " + sessaoId + " ja foi finalizada");
    }
}
