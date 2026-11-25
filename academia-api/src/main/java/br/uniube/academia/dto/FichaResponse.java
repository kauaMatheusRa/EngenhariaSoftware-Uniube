package br.uniube.academia.dto;

import br.uniube.academia.domain.DivisaoTreino;
import java.time.LocalDate;
import java.util.List;

/** Tela "03 - Meu treino" do prototipo. */
public record FichaResponse(
        Long id,
        String titulo,
        LocalDate vigenteDesde,
        String instrutor,
        List<DivisaoTreino> divisoes,
        DivisaoTreino divisaoSelecionada,
        List<ItemTreinoResponse> exercicios) {
}
