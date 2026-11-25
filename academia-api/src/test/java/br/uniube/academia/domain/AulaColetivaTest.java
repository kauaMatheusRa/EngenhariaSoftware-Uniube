package br.uniube.academia.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** A regra das 2 horas (US-10, criterio 3) testada direto no dominio, sem mock. */
class AulaColetivaTest {

    private static final LocalDateTime AULA = LocalDateTime.of(2026, 3, 15, 19, 0);

    private AulaColetiva spinning() {
        return new AulaColetiva("Spinning", AULA, 10, new Instrutor("Rafael", "CREF 1"));
    }

    @ParameterizedTest(name = "as {0} o cancelamento e permitido? {1}")
    @CsvSource({
        "2026-03-15T16:59, true", // falta 2h01: permite
        "2026-03-15T17:00, true", // exatamente 2h: permite (fronteira)
        "2026-03-15T17:01, false", // falta 1h59: recusa
        "2026-03-15T19:30, false" // aula ja comecou
    })
    @DisplayName("a fronteira do cancelamento cai exatamente 2 horas antes da aula")
    void fronteiraDoCancelamento(LocalDateTime agora, boolean permitido) {
        // Arrange
        AulaColetiva aula = spinning();

        // Act
        boolean resultado = aula.permiteCancelamentoEm(agora);

        // Assert
        assertThat(resultado).isEqualTo(permitido);
    }

    @Test
    @DisplayName("o limite exibido ao aluno e o horario da aula menos 2 horas")
    void limiteExibido() {
        // Arrange + Act
        LocalDateTime limite = spinning().limiteParaCancelar();

        // Assert
        assertThat(limite).isEqualTo(LocalDateTime.of(2026, 3, 15, 17, 0));
    }
}
