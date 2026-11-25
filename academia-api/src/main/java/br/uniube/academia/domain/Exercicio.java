package br.uniube.academia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Exercicio do catalogo, reaproveitado por varias fichas. */
@Entity
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String grupoMuscular;

    /** Texto exibido na tela "Detalhe do exercicio" (US-04, criterio 2). */
    @Column(length = 500)
    private String orientacao;

    private String videoUrl;

    protected Exercicio() {
        // exigido pelo JPA
    }

    public Exercicio(String nome, String grupoMuscular, String orientacao, String videoUrl) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
        this.orientacao = orientacao;
        this.videoUrl = videoUrl;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public String getOrientacao() {
        return orientacao;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
