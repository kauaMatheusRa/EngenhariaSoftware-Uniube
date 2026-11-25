package br.uniube.academia.service;

import br.uniube.academia.domain.DivisaoTreino;
import br.uniube.academia.domain.FichaTreino;
import br.uniube.academia.dto.FichaResponse;
import br.uniube.academia.dto.ItemTreinoResponse;
import br.uniube.academia.dto.TreinoMapper;
import br.uniube.academia.exception.FichaNaoEncontradaException;
import br.uniube.academia.exception.RecursoNaoEncontradoException;
import br.uniube.academia.repository.FichaTreinoRepository;
import br.uniube.academia.repository.ItemTreinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * US-04 — Consulta da ficha de treino pelo aluno.
 *
 * <p>Corresponde as telas "03 - Meu treino" e "04 - Detalhe do exercicio" do prototipo.
 */
@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaRepository;
    private final ItemTreinoRepository itemRepository;
    private final TreinoMapper mapper;

    public FichaTreinoService(
            FichaTreinoRepository fichaRepository, ItemTreinoRepository itemRepository, TreinoMapper mapper) {
        this.fichaRepository = fichaRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    /** US-04, criterios 1 e 3: ficha vigente agrupada por divisao, ou orientacao clara. */
    @Transactional(readOnly = true)
    public FichaResponse buscarFichaVigente(Long alunoId, DivisaoTreino divisao) {
        FichaTreino ficha = fichaRepository
                .findByAlunoIdAndVigenteTrue(alunoId)
                .orElseThrow(() -> new FichaNaoEncontradaException(alunoId));

        DivisaoTreino alvo = divisao != null ? divisao : primeiraDivisao(ficha);
        return mapper.toResponse(ficha, alvo);
    }

    /** US-04, criterio 2: detalhe com orientacao de execucao e video. */
    @Transactional(readOnly = true)
    public ItemTreinoResponse detalharExercicio(Long itemId) {
        return itemRepository
                .findById(itemId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercicio da ficha", itemId));
    }

    private DivisaoTreino primeiraDivisao(FichaTreino ficha) {
        return ficha.divisoes().stream().findFirst().orElse(DivisaoTreino.A);
    }
}
