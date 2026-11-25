package br.uniube.academia.controller;

import br.uniube.academia.dto.IniciarSessaoRequest;
import br.uniube.academia.dto.RegistrarExecucaoRequest;
import br.uniube.academia.dto.SessaoResponse;
import br.uniube.academia.service.SessaoTreinoService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** US-05 — execucao do treino: iniciar, marcar exercicio e finalizar. */
@RestController
@RequestMapping("/sessoes")
public class SessaoTreinoController {

    private final SessaoTreinoService service;

    public SessaoTreinoController(SessaoTreinoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessaoResponse> iniciar(@Valid @RequestBody IniciarSessaoRequest dto) {
        SessaoResponse criada = service.iniciar(dto);
        return ResponseEntity.created(URI.create("/sessoes/" + criada.id())).body(criada);
    }

    @PostMapping("/{sessaoId}/execucoes")
    public ResponseEntity<SessaoResponse> registrar(
            @PathVariable Long sessaoId, @Valid @RequestBody RegistrarExecucaoRequest dto) {
        return ResponseEntity.ok(service.registrarExecucao(sessaoId, dto));
    }

    @PostMapping("/{sessaoId}/finalizacao")
    public ResponseEntity<SessaoResponse> finalizar(@PathVariable Long sessaoId) {
        return ResponseEntity.ok(service.finalizar(sessaoId));
    }

    @GetMapping("/{sessaoId}")
    public ResponseEntity<SessaoResponse> consultar(@PathVariable Long sessaoId) {
        return ResponseEntity.ok(service.consultar(sessaoId));
    }
}
