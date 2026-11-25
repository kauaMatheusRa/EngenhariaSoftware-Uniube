package br.uniube.academia.dto;

import java.time.LocalDateTime;

/** Card de aula da tela "07 - Aulas coletivas". */
public record AulaResponse(
        Long id,
        String modalidade,
        LocalDateTime dataHora,
        String instrutor,
        int vagas,
        int vagasRestantes,
        boolean lotada) {
}
