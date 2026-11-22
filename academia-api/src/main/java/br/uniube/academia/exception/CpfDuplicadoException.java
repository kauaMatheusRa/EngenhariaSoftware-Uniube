package br.uniube.academia.exception;

/** Criterio 2 da US-01: o CPF nao pode se repetir entre alunos. */
public class CpfDuplicadoException extends RuntimeException {

    public CpfDuplicadoException(String cpf) {
        super("CPF " + cpf + " ja cadastrado para outro aluno");
    }
}
