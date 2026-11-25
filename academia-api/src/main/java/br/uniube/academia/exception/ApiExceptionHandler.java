package br.uniube.academia.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz excecoes de negocio em respostas HTTP.
 *
 * <p>Fica aqui, e nao no Controller, para que nenhum endpoint precise repetir try/catch.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> camposInvalidos(MethodArgumentNotValidException ex) {
        List<ErroResposta.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErroResposta.CampoInvalido(e.getField(), e.getDefaultMessage()))
                .toList();
        ErroResposta corpo = new ErroResposta(
                java.time.OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Campos invalidos",
                "Corrija os campos destacados e envie novamente",
                campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ErroResposta> cpfDuplicado(CpfDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResposta.de(HttpStatus.CONFLICT.value(), "CPF duplicado", ex.getMessage()));
    }

    @ExceptionHandler(ResponsavelObrigatorioException.class)
    public ResponseEntity<ErroResposta> responsavelObrigatorio(ResponsavelObrigatorioException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(ErroResposta.de(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(), "Responsavel obrigatorio", ex.getMessage()));
    }

    @ExceptionHandler({
        PlanoNaoEncontradoException.class,
        AlunoNaoEncontradoException.class,
        RecursoNaoEncontradoException.class
    })
    public ResponseEntity<ErroResposta> naoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResposta.de(HttpStatus.NOT_FOUND.value(), "Nao encontrado", ex.getMessage()));
    }

    /** US-04, criterio 3: o app mostra orientacao em vez de tela vazia. */
    @ExceptionHandler(FichaNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> semFicha(FichaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResposta.de(HttpStatus.NOT_FOUND.value(), "Sem ficha vigente", ex.getMessage()));
    }

    /** US-10, criterio 5: inadimplente nao reserva aula. */
    @ExceptionHandler(AcessoBloqueadoException.class)
    public ResponseEntity<ErroResposta> acessoBloqueado(AcessoBloqueadoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErroResposta.de(HttpStatus.FORBIDDEN.value(), "Acesso bloqueado", ex.getMessage()));
    }

    /** US-10, criterio 3: cancelamento exige 2 h de antecedencia. */
    @ExceptionHandler(CancelamentoForaDoPrazoException.class)
    public ResponseEntity<ErroResposta> foraDoPrazo(CancelamentoForaDoPrazoException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(ErroResposta.de(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(), "Fora do prazo", ex.getMessage()));
    }

    @ExceptionHandler({ReservaDuplicadaException.class, SessaoJaFinalizadaException.class})
    public ResponseEntity<ErroResposta> conflito(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResposta.de(HttpStatus.CONFLICT.value(), "Operacao invalida", ex.getMessage()));
    }
}
