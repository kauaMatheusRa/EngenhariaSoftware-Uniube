package br.uniube.academia.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.uniube.academia.exception.ResponsavelObrigatorioException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Teste da regra de idade direto no dominio: sem mock, sem Spring, sem banco.
 *
 * <p>E o teste mais barato possivel — so a classe e a data de referencia. Depois da
 * refatoracao, a fronteira dos 18 anos pode ser verificada dia a dia.
 */
class AlunoTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 3, 15);

    private Aluno alunoNascidoEm(LocalDate nascimento) {
        return new Aluno("202600001", "Pedro Lima", "55566677788", nascimento, "34988880000");
    }

    @ParameterizedTest(name = "nascido em {0} e menor de idade? {1}")
    @CsvSource({
        "2008-03-14, false", // fez 18 ontem
        "2008-03-15, false", // faz 18 exatamente hoje
        "2008-03-16, true", // faz 18 amanha
        "2011-03-15, true", // 15 anos
        "1999-03-14, false" // adulto
    })
    @DisplayName("a fronteira dos 18 anos cai no dia do aniversario")
    void fronteiraDaMaioridade(LocalDate nascimento, boolean esperadoMenor) {
        // Arrange
        Aluno aluno = alunoNascidoEm(nascimento);

        // Act
        boolean menor = aluno.isMenorDeIdade(HOJE);

        // Assert
        assertThat(menor).isEqualTo(esperadoMenor);
    }

    @Test
    @DisplayName("adulto nao precisa de responsavel")
    void adultoDispensaResponsavel() {
        // Arrange
        Aluno aluno = alunoNascidoEm(LocalDate.of(1999, 3, 14));

        // Act + Assert
        assertThatCode(() -> aluno.informarResponsavel(null, null, HOJE)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("menor sem responsavel e recusado pelo proprio dominio")
    void menorExigeResponsavel() {
        // Arrange
        Aluno aluno = alunoNascidoEm(LocalDate.of(2011, 3, 15));

        // Act + Assert
        assertThatThrownBy(() -> aluno.informarResponsavel(null, null, HOJE))
                .isInstanceOf(ResponsavelObrigatorioException.class)
                .hasMessageContaining("Pedro Lima");
    }

    @Test
    @DisplayName("menor com responsavel completo guarda os dados do responsavel")
    void menorComResponsavel() {
        // Arrange
        Aluno aluno = alunoNascidoEm(LocalDate.of(2011, 3, 15));

        // Act
        aluno.informarResponsavel("Ana Lima", "99988877766", HOJE);

        // Assert
        assertThat(aluno.getResponsavelNome()).isEqualTo("Ana Lima");
        assertThat(aluno.getResponsavelCpf()).isEqualTo("99988877766");
    }

    @Test
    @DisplayName("todo aluno nasce ATIVO")
    void alunoNasceAtivo() {
        // Arrange + Act
        Aluno aluno = alunoNascidoEm(LocalDate.of(1999, 3, 14));

        // Assert
        assertThat(aluno.getStatus()).isEqualTo(StatusAluno.ATIVO);
    }
}
