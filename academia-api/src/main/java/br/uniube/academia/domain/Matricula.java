package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * Vinculo entre o aluno e o plano contratado.
 *
 * <p>Criterio 5 da US-01: a ficha do aluno mostra data de inicio, plano, valor da
 * mensalidade e dia de vencimento — todos vem daqui.
 */
@Entity
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plano_id")
    private Plano plano;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private int diaVencimento;

    protected Matricula() {
        // exigido pelo JPA
    }

    public Matricula(Aluno aluno, Plano plano, LocalDate dataInicio) {
        this.aluno = aluno;
        this.plano = plano;
        this.dataInicio = dataInicio;
        this.dataFim = dataInicio.plusMonths(plano.getDuracaoMeses());
        this.diaVencimento = dataInicio.getDayOfMonth();
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Plano getPlano() {
        return plano;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }
}
