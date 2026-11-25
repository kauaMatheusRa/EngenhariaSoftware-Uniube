package br.uniube.academia.controller;

import br.uniube.academia.domain.DivisaoTreino;
import br.uniube.academia.dto.FichaResponse;
import br.uniube.academia.dto.ItemTreinoResponse;
import br.uniube.academia.service.FichaTreinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** US-04 — telas "Meu treino" e "Detalhe do exercicio". */
@RestController
public class FichaTreinoController {

    private final FichaTreinoService service;

    public FichaTreinoController(FichaTreinoService service) {
        this.service = service;
    }

    @GetMapping("/alunos/{alunoId}/ficha")
    public ResponseEntity<FichaResponse> ficha(
            @PathVariable Long alunoId, @RequestParam(required = false) DivisaoTreino divisao) {
        return ResponseEntity.ok(service.buscarFichaVigente(alunoId, divisao));
    }

    @GetMapping("/ficha/exercicios/{itemId}")
    public ResponseEntity<ItemTreinoResponse> exercicio(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.detalharExercicio(itemId));
    }
}
