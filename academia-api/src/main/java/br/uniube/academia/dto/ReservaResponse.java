package br.uniube.academia.dto;

import br.uniube.academia.domain.StatusReserva;
import java.time.LocalDateTime;

/** Tela "08 - Reserva confirmada" do prototipo. */
public record ReservaResponse(
        Long id,
        StatusReserva status,
        String modalidade,
        LocalDateTime dataHoraAula,
        String instrutor,
        int vagasRestantes,
        LocalDateTime podeCancelarAte) {
}
