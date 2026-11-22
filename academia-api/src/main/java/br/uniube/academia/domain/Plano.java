package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/** Plano contratado pelo aluno. */
@Entity
public class Plano {

    @Id
    private Long id;

    private String nome;

    private BigDecimal valorMensal;

    private int duracaoMeses;

    protected Plano() {
        // exigido pelo JPA
    }

    public Plano(Long id, String nome, BigDecimal valorMensal, int duracaoMeses) {
        this.id = id;
        this.nome = nome;
        this.valorMensal = valorMensal;
        this.duracaoMeses = duracaoMeses;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getValorMensal() {
        return valorMensal;
    }

    public int getDuracaoMeses() {
        return duracaoMeses;
    }
}
