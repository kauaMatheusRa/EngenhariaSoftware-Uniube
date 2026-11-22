package br.uniube.academia.service;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.Matricula;
import br.uniube.academia.domain.Plano;
import br.uniube.academia.dto.AlunoMapper;
import br.uniube.academia.dto.AlunoResponse;
import br.uniube.academia.dto.NovoAlunoRequest;
import br.uniube.academia.exception.AlunoNaoEncontradoException;
import br.uniube.academia.exception.CpfDuplicadoException;
import br.uniube.academia.exception.PlanoNaoEncontradoException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.MatriculaRepository;
import br.uniube.academia.repository.PlanoRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Year;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso do aluno (US-01 — Cadastrar aluno com plano).
 *
 * <p>Esta e a camada que orquestra: valida a regra que depende de outros dados, pede ao
 * dominio que aplique as suas proprias regras e manda o repositorio persistir. Nenhuma
 * dessas decisoes aparece no Controller.
 */
@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaRepository matriculaRepository;
    private final AlunoMapper mapper;

    /** Injetado para que as datas fiquem sob controle no teste. */
    private final Clock clock;

    public AlunoService(
            AlunoRepository alunoRepository,
            PlanoRepository planoRepository,
            MatriculaRepository matriculaRepository,
            AlunoMapper mapper,
            Clock clock) {
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.matriculaRepository = matriculaRepository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public AlunoResponse cadastrar(NovoAlunoRequest dto) {
        if (alunoRepository.existsByCpf(dto.cpf())) {
            throw new CpfDuplicadoException(dto.cpf());
        }

        Plano plano = planoRepository
                .findById(dto.planoId())
                .orElseThrow(() -> new PlanoNaoEncontradoException(dto.planoId()));

        LocalDate hoje = LocalDate.now(clock);

        Aluno aluno = mapper.toEntity(dto, gerarMatricula());
        aluno.informarResponsavel(dto.responsavelNome(), dto.responsavelCpf(), hoje);

        Aluno salvo = alunoRepository.save(aluno);
        Matricula matricula = matriculaRepository.save(new Matricula(salvo, plano, hoje));

        return mapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarFicha(Long alunoId) {
        return matriculaRepository
                .findFirstByAlunoIdOrderByDataInicioDesc(alunoId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AlunoNaoEncontradoException(alunoId));
    }

    /** Criterio 1 da US-01: cada aluno recebe um numero de matricula unico. */
    private String gerarMatricula() {
        return "%d%05d".formatted(Year.now(clock).getValue(), alunoRepository.count() + 1);
    }
}
