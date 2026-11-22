package br.uniube.academia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * Contrato de entrada do cadastro.
 *
 * <p>Criterio 3 da US-01: campo obrigatorio em branco impede a gravacao. A checagem de
 * formato fica no DTO; as regras de negocio ficam no dominio e no service.
 */
public record NovoAlunoRequest(
        @NotBlank(message = "nome e obrigatorio") String nome,
        @NotBlank(message = "cpf e obrigatorio")
                @Pattern(regexp = "\\d{11}", message = "cpf deve ter 11 digitos, apenas numeros")
                String cpf,
        @NotNull(message = "dataNascimento e obrigatoria") @Past(message = "dataNascimento deve ser no passado")
                LocalDate dataNascimento,
        String telefone,
        @NotNull(message = "planoId e obrigatorio") Long planoId,
        String responsavelNome,
        String responsavelCpf) {
}
