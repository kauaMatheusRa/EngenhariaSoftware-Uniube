package br.uniube.academia.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.uniube.academia.domain.StatusAluno;
import br.uniube.academia.dto.AlunoResponse;
import br.uniube.academia.exception.CpfDuplicadoException;
import br.uniube.academia.service.AlunoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** O Controller so traduz HTTP: entrada invalida nem chega no service. */
@WebMvcTest(AlunoController.class)
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlunoService service;

    private static final String CORPO_VALIDO =
            """
            {"nome":"Marina Souza","cpf":"12345678901","dataNascimento":"1999-03-14",
             "telefone":"34999990000","planoId":1}
            """;

    @Test
    @DisplayName("criterio 1: cadastro valido devolve 201 com a ficha do aluno")
    void cadastraComSucesso() throws Exception {
        when(service.cadastrar(any()))
                .thenReturn(new AlunoResponse(
                        1L,
                        "202600008",
                        "Marina Souza",
                        "12345678901",
                        StatusAluno.ATIVO,
                        LocalDate.now(),
                        "Mensal",
                        new BigDecimal("119.90"),
                        LocalDate.now().getDayOfMonth()));

        mockMvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("202600008"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("criterio 3: campo obrigatorio em branco devolve 400 e nao chama o service")
    void recusaCamposObrigatorios() throws Exception {
        String semNome =
                """
                {"nome":"","cpf":"12345678901","dataNascimento":"1999-03-14","planoId":1}
                """;

        mockMvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(semNome))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[0].campo").value("nome"));

        verify(service, never()).cadastrar(any());
    }

    @Test
    @DisplayName("criterio 2: CPF duplicado vira 409, traduzido pelo handler")
    void traduzCpfDuplicado() throws Exception {
        when(service.cadastrar(any())).thenThrow(new CpfDuplicadoException("12345678901"));

        mockMvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(CORPO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("CPF duplicado"));
    }
}
