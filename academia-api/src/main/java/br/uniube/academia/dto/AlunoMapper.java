package br.uniube.academia.dto;

import br.uniube.academia.domain.Aluno;
import br.uniube.academia.domain.Matricula;
import org.springframework.stereotype.Component;

/** Traduz entre o dominio e os contratos expostos pela API. */
@Component
public class AlunoMapper {

    public Aluno toEntity(NovoAlunoRequest dto, String matricula) {
        return new Aluno(matricula, dto.nome(), dto.cpf(), dto.dataNascimento(), dto.telefone());
    }

    public AlunoResponse toResponse(Matricula matricula) {
        Aluno aluno = matricula.getAluno();
        return new AlunoResponse(
                aluno.getId(),
                aluno.getMatricula(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getStatus(),
                matricula.getDataInicio(),
                matricula.getPlano().getNome(),
                matricula.getPlano().getValorMensal(),
                matricula.getDiaVencimento());
    }
}
