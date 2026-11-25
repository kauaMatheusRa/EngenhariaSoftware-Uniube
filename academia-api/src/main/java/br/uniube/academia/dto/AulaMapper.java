package br.uniube.academia.dto;

import br.uniube.academia.domain.AulaColetiva;
import br.uniube.academia.domain.Reserva;
import org.springframework.stereotype.Component;

/** Traduz aulas e reservas para os contratos do app. */
@Component
public class AulaMapper {

    public AulaResponse toResponse(AulaColetiva aula, int confirmadas) {
        int restantes = Math.max(0, aula.getVagas() - confirmadas);
        return new AulaResponse(
                aula.getId(),
                aula.getModalidade(),
                aula.getDataHora(),
                aula.getInstrutor() == null ? null : aula.getInstrutor().getNome(),
                aula.getVagas(),
                restantes,
                restantes == 0);
    }

    public ReservaResponse toResponse(Reserva reserva, int confirmadas) {
        AulaColetiva aula = reserva.getAula();
        return new ReservaResponse(
                reserva.getId(),
                reserva.getStatus(),
                aula.getModalidade(),
                aula.getDataHora(),
                aula.getInstrutor() == null ? null : aula.getInstrutor().getNome(),
                Math.max(0, aula.getVagas() - confirmadas),
                aula.limiteParaCancelar());
    }
}
