package br.uniube.academia.exception;

/** Recurso generico que nao existe (aula, sessao, item de treino). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " " + id + " nao encontrado");
    }
}
