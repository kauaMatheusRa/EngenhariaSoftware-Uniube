package br.uniube.academia.exception;

/** Criterio 4 da US-01: aluno menor de 18 anos exige nome e CPF do responsavel. */
public class ResponsavelObrigatorioException extends RuntimeException {

    public ResponsavelObrigatorioException(String nomeDoAluno) {
        super("Aluno " + nomeDoAluno + " e menor de 18 anos: informe nome e CPF do responsavel");
    }
}
