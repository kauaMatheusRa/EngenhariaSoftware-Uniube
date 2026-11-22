package br.uniube.academia.exception;

public class PlanoNaoEncontradoException extends RuntimeException {

    public PlanoNaoEncontradoException(Long planoId) {
        super("Plano " + planoId + " nao encontrado");
    }
}
