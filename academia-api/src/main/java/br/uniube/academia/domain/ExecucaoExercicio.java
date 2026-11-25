package br.uniube.academia.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Registro de um exercicio executado dentro de uma sessao.
 *
 * <p>US-05, criterio 2: a carga real fica aqui, no registro do dia. A prescricao da ficha
 * nao muda — e exatamente o que o prototipo avisa na tela de detalhe do exercicio.
 */
@Entity
public class ExecucaoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id")
    private SessaoTreino sessao;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "item_id")
    private ItemTreino item;

    private double cargaReal;

    protected ExecucaoExercicio() {
        // exigido pelo JPA
    }

    public ExecucaoExercicio(ItemTreino item, double cargaReal) {
        this.item = item;
        this.cargaReal = cargaReal;
    }

    void vincularA(SessaoTreino sessao) {
        this.sessao = sessao;
    }

    public Long getId() {
        return id;
    }

    public ItemTreino getItem() {
        return item;
    }

    public double getCargaReal() {
        return cargaReal;
    }
}
