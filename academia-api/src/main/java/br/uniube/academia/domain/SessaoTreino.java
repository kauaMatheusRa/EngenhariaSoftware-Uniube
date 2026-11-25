package br.uniube.academia.domain;

import br.uniube.academia.exception.SessaoJaFinalizadaException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma sessao de treino do aluno (US-05).
 *
 * <p>O calculo de progresso e a regra de nao mexer em sessao finalizada moram aqui: sao
 * decisoes que dependem so dos dados da propria sessao.
 */
@Entity
public class SessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ficha_id")
    private FichaTreino ficha;

    private DivisaoTreino divisao;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ExecucaoExercicio> execucoes = new ArrayList<>();

    protected SessaoTreino() {
        // exigido pelo JPA
    }

    public SessaoTreino(Aluno aluno, FichaTreino ficha, DivisaoTreino divisao, LocalDateTime inicio) {
        this.aluno = aluno;
        this.ficha = ficha;
        this.divisao = divisao;
        this.inicio = inicio;
    }

    /** US-05, criterio 1: marcar exercicio como concluido atualiza o percentual. */
    public void registrarExecucao(ItemTreino item, double cargaReal) {
        garantirQueEstaAberta();
        if (jaExecutou(item)) {
            return;
        }
        ExecucaoExercicio execucao = new ExecucaoExercicio(item, cargaReal);
        execucao.vincularA(this);
        execucoes.add(execucao);
    }

    /** US-05, criterio 3: finalizar grava data, duracao e exercicios executados. */
    public void finalizar(LocalDateTime agora) {
        garantirQueEstaAberta();
        this.fim = agora;
    }

    public boolean estaFinalizada() {
        return fim != null;
    }

    public int totalPrescrito() {
        return ficha.itensDa(divisao).size();
    }

    public int totalConcluido() {
        return execucoes.size();
    }

    public int percentualConcluido() {
        int total = totalPrescrito();
        return total == 0 ? 0 : (int) Math.round(totalConcluido() * 100.0 / total);
    }

    public Long duracaoMinutos() {
        return fim == null ? null : Duration.between(inicio, fim).toMinutes();
    }

    private boolean jaExecutou(ItemTreino item) {
        return execucoes.stream().anyMatch(e -> e.getItem().getId().equals(item.getId()));
    }

    private void garantirQueEstaAberta() {
        if (estaFinalizada()) {
            throw new SessaoJaFinalizadaException(id);
        }
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public FichaTreino getFicha() {
        return ficha;
    }

    public DivisaoTreino getDivisao() {
        return divisao;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public List<ExecucaoExercicio> getExecucoes() {
        return List.copyOf(execucoes);
    }
}
