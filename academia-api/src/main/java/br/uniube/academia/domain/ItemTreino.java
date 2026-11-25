package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** Um exercicio prescrito dentro de uma ficha, com series, repeticoes, carga e descanso. */
@Entity
public class ItemTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_id")
    private FichaTreino ficha;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exercicio_id")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    private DivisaoTreino divisao;

    private int ordem;

    private int series;

    private int repeticoes;

    private double carga;

    private int descansoSegundos;

    protected ItemTreino() {
        // exigido pelo JPA
    }

    public ItemTreino(
            Exercicio exercicio,
            DivisaoTreino divisao,
            int ordem,
            int series,
            int repeticoes,
            double carga,
            int descansoSegundos) {
        this.exercicio = exercicio;
        this.divisao = divisao;
        this.ordem = ordem;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.descansoSegundos = descansoSegundos;
    }

    void vincularA(FichaTreino ficha) {
        this.ficha = ficha;
    }

    public Long getId() {
        return id;
    }

    public FichaTreino getFicha() {
        return ficha;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public DivisaoTreino getDivisao() {
        return divisao;
    }

    public int getOrdem() {
        return ordem;
    }

    public int getSeries() {
        return series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public double getCarga() {
        return carga;
    }

    public int getDescansoSegundos() {
        return descansoSegundos;
    }
}
