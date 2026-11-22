package br.uniube.academia.dto;

import br.uniube.academia.domain.StatusAluno;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Contrato de saida: a ficha do aluno conforme o criterio 5 da US-01. */
public record AlunoResponse(
        Long id,
        String matricula,
        String nome,
        String cpf,
        StatusAluno status,
        LocalDate dataInicio,
        String plano,
        BigDecimal valorMensalidade,
        int diaVencimento) {
}
