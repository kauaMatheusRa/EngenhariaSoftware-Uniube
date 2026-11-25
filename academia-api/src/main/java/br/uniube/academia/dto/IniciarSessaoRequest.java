package br.uniube.academia.dto;

import br.uniube.academia.domain.DivisaoTreino;
import jakarta.validation.constraints.NotNull;

public record IniciarSessaoRequest(
        @NotNull(message = "alunoId e obrigatorio") Long alunoId,
        @NotNull(message = "divisao e obrigatoria") DivisaoTreino divisao) {
}
