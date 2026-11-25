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
import java.time.LocalDateTime;

/** Reserva de vaga em uma aula coletiva (US-10). */
@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "aula_id")
    private AulaColetiva aula;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    protected Reserva() {
        // exigido pelo JPA
    }

    public Reserva(Aluno aluno, AulaColetiva aula, LocalDateTime dataHora, StatusReserva status) {
        this.aluno = aluno;
        this.aula = aula;
        this.dataHora = dataHora;
        this.status = status;
    }

    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    /** Quando uma vaga abre, o primeiro da espera e promovido. */
    public void confirmar() {
        this.status = StatusReserva.CONFIRMADA;
    }

    public boolean estaAtiva() {
        return status == StatusReserva.CONFIRMADA || status == StatusReserva.LISTA_DE_ESPERA;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public AulaColetiva getAula() {
        return aula;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusReserva getStatus() {
        return status;
    }
}
