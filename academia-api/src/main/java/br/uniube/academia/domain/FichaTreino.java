package br.uniube.academia.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ficha de treino prescrita por um instrutor (US-03) e consultada pelo aluno (US-04).
 *
 * <p>A ficha e dona dos seus itens: composicao, como no diagrama de classes. Arquivar a
 * ficha e decisao dela propria, nao do service.
 */
@Entity
public class FichaTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;

    private String titulo;

    private LocalDate dataCriacao;

    private boolean vigente;

    @OneToMany(mappedBy = "ficha", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemTreino> itens = new ArrayList<>();

    protected FichaTreino() {
        // exigido pelo JPA
    }

    public FichaTreino(Aluno aluno, Instrutor instrutor, String titulo, LocalDate dataCriacao) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.titulo = titulo;
        this.dataCriacao = dataCriacao;
        this.vigente = true;
    }

    /** US-03, criterio 3: ficha sem exercicio nao e valida. */
    public void adicionarItem(ItemTreino item) {
        item.vincularA(this);
        itens.add(item);
    }

    /** US-03, criterio 4: a ficha anterior e arquivada, nunca apagada. */
    public void arquivar() {
        this.vigente = false;
    }

    public List<ItemTreino> itensDa(DivisaoTreino divisao) {
        return itens.stream()
                .filter(i -> i.getDivisao() == divisao)
                .sorted(Comparator.comparingInt(ItemTreino::getOrdem))
                .toList();
    }

    public List<DivisaoTreino> divisoes() {
        return itens.stream().map(ItemTreino::getDivisao).distinct().sorted().toList();
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Instrutor getInstrutor() {
        return instrutor;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public boolean isVigente() {
        return vigente;
    }

    public List<ItemTreino> getItens() {
        return List.copyOf(itens);
    }
}
