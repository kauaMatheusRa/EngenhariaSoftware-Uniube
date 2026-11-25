package br.uniube.academia.dto;

/** Card de exercicio do prototipo, e tambem a tela "04 - Detalhe do exercicio". */
public record ItemTreinoResponse(
        Long id,
        String exercicio,
        String grupoMuscular,
        int series,
        int repeticoes,
        double carga,
        int descansoSegundos,
        String orientacao,
        String videoUrl) {
}
