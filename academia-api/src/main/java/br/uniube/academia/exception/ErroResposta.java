package br.uniube.academia.exception;

import java.time.OffsetDateTime;
import java.util.List;

/** Corpo padrao de erro devolvido pela API. */
public record ErroResposta(
        OffsetDateTime momento,
        int status,
        String erro,
        String mensagem,
        List<CampoInvalido> campos) {

    public record CampoInvalido(String campo, String mensagem) {
    }

    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(OffsetDateTime.now(), status, erro, mensagem, List.of());
    }
}
