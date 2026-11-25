package br.uniube.academia.service;

import br.uniube.academia.domain.FichaTreino;
import br.uniube.academia.domain.ItemTreino;
import br.uniube.academia.domain.SessaoTreino;
import br.uniube.academia.dto.IniciarSessaoRequest;
import br.uniube.academia.dto.RegistrarExecucaoRequest;
import br.uniube.academia.dto.SessaoResponse;
import br.uniube.academia.dto.TreinoMapper;
import br.uniube.academia.exception.AlunoNaoEncontradoException;
import br.uniube.academia.exception.FichaNaoEncontradaException;
import br.uniube.academia.exception.RecursoNaoEncontradoException;
import br.uniube.academia.repository.AlunoRepository;
import br.uniube.academia.repository.FichaTreinoRepository;
import br.uniube.academia.repository.ItemTreinoRepository;
import br.uniube.academia.repository.SessaoTreinoRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * US-05 — Registro da execucao do treino.
 *
 * <p>Corresponde as telas "05 - Meu treino (em andamento)" e "06 - Treino concluido".
 * O service orquestra; o calculo de progresso e a regra de sessao encerrada ficam no
 * dominio, em SessaoTreino.
 */
@Service
public class SessaoTreinoService {

    private final SessaoTreinoRepository sessaoRepository;
    private final FichaTreinoRepository fichaRepository;
    private final ItemTreinoRepository itemRepository;
    private final AlunoRepository alunoRepository;
    private final TreinoMapper mapper;
    private final Clock clock;

    public SessaoTreinoService(
            SessaoTreinoRepository sessaoRepository,
            FichaTreinoRepository fichaRepository,
            ItemTreinoRepository itemRepository,
            AlunoRepository alunoRepository,
            TreinoMapper mapper,
            Clock clock) {
        this.sessaoRepository = sessaoRepository;
        this.fichaRepository = fichaRepository;
        this.itemRepository = itemRepository;
        this.alunoRepository = alunoRepository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public SessaoResponse iniciar(IniciarSessaoRequest dto) {
        var aluno = alunoRepository
                .findById(dto.alunoId())
                .orElseThrow(() -> new AlunoNaoEncontradoException(dto.alunoId()));

        FichaTreino ficha = fichaRepository
                .findByAlunoIdAndVigenteTrue(dto.alunoId())
                .orElseThrow(() -> new FichaNaoEncontradaException(dto.alunoId()));

        SessaoTreino sessao = new SessaoTreino(aluno, ficha, dto.divisao(), LocalDateTime.now(clock));
        return mapper.toResponse(sessaoRepository.save(sessao));
    }

    /** US-05, criterios 1 e 2: marca o exercicio e guarda a carga real do dia. */
    @Transactional
    public SessaoResponse registrarExecucao(Long sessaoId, RegistrarExecucaoRequest dto) {
        SessaoTreino sessao = buscar(sessaoId);
        ItemTreino item = itemRepository
                .findById(dto.itemTreinoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercicio da ficha", dto.itemTreinoId()));

        double carga = dto.cargaReal() != null ? dto.cargaReal() : item.getCarga();
        sessao.registrarExecucao(item, carga);

        return mapper.toResponse(sessaoRepository.save(sessao));
    }

    /** US-05, criterio 3: grava data, duracao e exercicios executados. */
    @Transactional
    public SessaoResponse finalizar(Long sessaoId) {
        SessaoTreino sessao = buscar(sessaoId);
        sessao.finalizar(LocalDateTime.now(clock));
        return mapper.toResponse(sessaoRepository.save(sessao));
    }

    @Transactional(readOnly = true)
    public SessaoResponse consultar(Long sessaoId) {
        return mapper.toResponse(buscar(sessaoId));
    }

    private SessaoTreino buscar(Long sessaoId) {
        return sessaoRepository
                .findById(sessaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessao", sessaoId));
    }
}
