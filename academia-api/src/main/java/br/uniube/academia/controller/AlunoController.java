package br.uniube.academia.controller;

import br.uniube.academia.dto.AlunoResponse;
import br.uniube.academia.dto.NovoAlunoRequest;
import br.uniube.academia.service.AlunoService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entrada HTTP do cadastro de aluno.
 *
 * <p>O Controller traduz HTTP para uma chamada da aplicacao e devolve a resposta. Nao ha
 * regra de negocio aqui: nem consulta de CPF, nem calculo de vencimento, nem acesso a banco.
 */
@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody NovoAlunoRequest dto) {
        AlunoResponse criado = service.cadastrar(dto);
        return ResponseEntity.created(URI.create("/alunos/" + criado.id())).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> ficha(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarFicha(id));
    }
}
