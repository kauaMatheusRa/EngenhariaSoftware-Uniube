package br.uniube.academia.exception;

/** US-04, criterio 3: sem ficha vigente, o app mostra orientacao em vez de tela vazia. */
public class FichaNaoEncontradaException extends RuntimeException {

    public FichaNaoEncontradaException(Long alunoId) {
        super("Aluno " + alunoId + " nao tem ficha vigente. Procure seu instrutor para montar sua ficha");
    }
}
