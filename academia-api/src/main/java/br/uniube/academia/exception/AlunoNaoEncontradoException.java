package br.uniube.academia.exception;

public class AlunoNaoEncontradoException extends RuntimeException {

    public AlunoNaoEncontradoException(Long alunoId) {
        super("Aluno " + alunoId + " nao encontrado");
    }
}
