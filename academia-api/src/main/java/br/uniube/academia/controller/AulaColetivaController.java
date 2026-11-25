package br.uniube.academia.controller;

import br.uniube.academia.dto.AulaResponse;
import br.uniube.academia.dto.NovaReservaRequest;
import br.uniube.academia.dto.ReservaResponse;
import br.uniube.academia.service.AulaColetivaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** US-10 — grade de aulas e reserva de vaga. */
@RestController
public class AulaColetivaController {

    private final AulaColetivaService service;

    public AulaColetivaController(AulaColetivaService service) {
        this.service = service;
    }

    @GetMapping("/aulas")
    public ResponseEntity<List<AulaResponse>> grade(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        return ResponseEntity.ok(service.gradeDoDia(dia));
    }

    @PostMapping("/aulas/{aulaId}/reservas")
    public ResponseEntity<ReservaResponse> reservar(
            @PathVariable Long aulaId, @Valid @RequestBody NovaReservaRequest dto) {
        return ResponseEntity.ok(service.reservar(aulaId, dto));
    }

    @DeleteMapping("/reservas/{reservaId}")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long reservaId) {
        return ResponseEntity.ok(service.cancelar(reservaId));
    }
}
