package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Aula coletiva com numero limitado de vagas (US-10).
 *
 * <p>As duas regras que dependem so da aula moram aqui: quanto tempo falta para comecar e
 * qual o prazo de cancelamento. O prototipo mostra as duas na tela de confirmacao.
 */
@Entity
public class AulaColetiva {

    /** US-10, criterio 3: cancelamento com mais de 2 h de antecedencia libera a vaga. */
    public static final int HORAS_LIMITE_CANCELAMENTO = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modalidade;

    private LocalDateTime dataHora;

    private int vagas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;

    protected AulaColetiva() {
        // exigido pelo JPA
    }

    public AulaColetiva(String modalidade, LocalDateTime dataHora, int vagas, Instrutor instrutor) {
        this.modalidade = modalidade;
        this.dataHora = dataHora;
        this.vagas = vagas;
        this.instrutor = instrutor;
    }

    public boolean permiteCancelamentoEm(LocalDateTime agora) {
        return Duration.between(agora, dataHora).toMinutes() >= HORAS_LIMITE_CANCELAMENTO * 60L;
    }

    public LocalDateTime limiteParaCancelar() {
        return dataHora.minusHours(HORAS_LIMITE_CANCELAMENTO);
    }

    public Long getId() {
        return id;
    }

    public String getModalidade() {
        return modalidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public int getVagas() {
        return vagas;
    }

    public Instrutor getInstrutor() {
        return instrutor;
    }
}
