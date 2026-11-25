package br.uniube.academia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** US-05, criterio 2: a carga real e opcional; sem ela, vale a prescrita. */
public record RegistrarExecucaoRequest(
        @NotNull(message = "itemTreinoId e obrigatorio") Long itemTreinoId,
        @PositiveOrZero(message = "cargaReal nao pode ser negativa") Double cargaReal) {
}
