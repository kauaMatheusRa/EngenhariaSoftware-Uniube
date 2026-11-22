package br.uniube.academia.domain;

import br.uniube.academia.exception.ResponsavelObrigatorioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.Period;

/**
 * Aluno da academia.
 *
 * <p>As regras que dependem apenas do proprio aluno moram aqui, e nao no Service nem no
 * Controller: saber se e menor de idade e exigir responsavel sao decisoes do dominio.
 */
@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluno status;

    private String responsavelNome;

    private String responsavelCpf;

    protected Aluno() {
        // exigido pelo JPA
    }

    public Aluno(String matricula, String nome, String cpf, LocalDate dataNascimento, String telefone) {
        this.matricula = matricula;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.status = StatusAluno.ATIVO;
    }

    /** Criterio 4 da US-01: menor de 18 anos so e cadastrado com responsavel informado. */
    public void informarResponsavel(String responsavelNome, String responsavelCpf) {
        if (!isMenorDeIdade()) {
            this.responsavelNome = responsavelNome;
            this.responsavelCpf = responsavelCpf;
            return;
        }
        if (vazio(responsavelNome) || vazio(responsavelCpf)) {
            throw new ResponsavelObrigatorioException(nome);
        }
        this.responsavelNome = responsavelNome;
        this.responsavelCpf = responsavelCpf;
    }

    public boolean isMenorDeIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears() < 18;
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    public Long getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public StatusAluno getStatus() {
        return status;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public String getResponsavelCpf() {
        return responsavelCpf;
    }
}
