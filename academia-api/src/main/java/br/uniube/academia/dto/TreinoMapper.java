package br.uniube.academia.dto;

import br.uniube.academia.domain.DivisaoTreino;
import br.uniube.academia.domain.FichaTreino;
import br.uniube.academia.domain.ItemTreino;
import br.uniube.academia.domain.SessaoTreino;
import java.util.List;
import org.springframework.stereotype.Component;

/** Traduz o dominio do treino para os contratos que o app consome. */
@Component
public class TreinoMapper {

    public FichaResponse toResponse(FichaTreino ficha, DivisaoTreino divisao) {
        List<ItemTreinoResponse> itens =
                ficha.itensDa(divisao).stream().map(this::toResponse).toList();
        return new FichaResponse(
                ficha.getId(),
                ficha.getTitulo(),
                ficha.getDataCriacao(),
                ficha.getInstrutor() == null ? null : ficha.getInstrutor().getNome(),
                ficha.divisoes(),
                divisao,
                itens);
    }

    public ItemTreinoResponse toResponse(ItemTreino item) {
        return new ItemTreinoResponse(
                item.getId(),
                item.getExercicio().getNome(),
                item.getExercicio().getGrupoMuscular(),
                item.getSeries(),
                item.getRepeticoes(),
                item.getCarga(),
                item.getDescansoSegundos(),
                item.getExercicio().getOrientacao(),
                item.getExercicio().getVideoUrl());
    }

    public SessaoResponse toResponse(SessaoTreino sessao) {
        return new SessaoResponse(
                sessao.getId(),
                sessao.getDivisao(),
                sessao.getInicio(),
                sessao.getFim(),
                sessao.duracaoMinutos(),
                sessao.totalConcluido(),
                sessao.totalPrescrito(),
                sessao.percentualConcluido(),
                sessao.estaFinalizada());
    }
}
