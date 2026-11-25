package br.uniube.academia.dto;

import jakarta.validation.constraints.NotNull;

public record NovaReservaRequest(@NotNull(message = "alunoId e obrigatorio") Long alunoId) {
}
