package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Instrutor que prescreve fichas e aplica avaliacoes. */
@Entity
public class Instrutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cref;

    protected Instrutor() {
        // exigido pelo JPA
    }

    public Instrutor(String nome, String cref) {
        this.nome = nome;
        this.cref = cref;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCref() {
        return cref;
    }
}
