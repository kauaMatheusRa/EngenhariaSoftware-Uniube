package br.uniube.academia.dto;

import br.uniube.academia.domain.DivisaoTreino;
import java.time.LocalDateTime;

/** Telas "05 - Meu treino (em andamento)" e "06 - Treino concluido". */
public record SessaoResponse(
        Long id,
        DivisaoTreino divisao,
        LocalDateTime inicio,
        LocalDateTime fim,
        Long duracaoMinutos,
        int exerciciosConcluidos,
        int exerciciosPrescritos,
        int percentual,
        boolean finalizada) {
}
