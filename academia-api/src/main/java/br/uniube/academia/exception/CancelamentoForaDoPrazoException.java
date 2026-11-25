package br.uniube.academia.exception;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/** US-10, criterio 3: cancelar exige 2 h de antecedencia. */
public class CancelamentoForaDoPrazoException extends RuntimeException {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public CancelamentoForaDoPrazoException(LocalDateTime limite) {
        super("Fora do prazo: o cancelamento era permitido ate as " + limite.format(HORA));
    }
}
