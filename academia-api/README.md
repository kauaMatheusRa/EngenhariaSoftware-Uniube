# academia-api — cadastro de aluno em camadas

Prática guiada da Aula 9 (Arquitetura em Camadas e MVC) — Engenharia de Software, Uniube.

**Objetivo:** organizar uma funcionalidade do backlog separando responsabilidades, sem
regra de negócio no Controller.

**User Stories implementadas:** US-01 (cadastro), US-04 (consultar ficha), US-05 (registrar
execução do treino) e US-10 (reservar aula coletiva) — as três últimas são exatamente as
telas do [protótipo](../docs/prototipo/README.md).

A arquitetura em camadas descrita abaixo nasceu com a [US-01](https://github.com/kauaMatheusRa/EngenhariaSoftware-Uniube/issues/1).
Foi a escolhida por ser a de maior prioridade do backlog e por ter cinco critérios de
aceitação de naturezas diferentes — formato, unicidade, regra de idade, geração de dado e
composição de resposta —, o que obriga a distribuir a lógica entre as camadas em vez de
empilhar tudo em um lugar só.

---

## Diagrama da arquitetura

![Arquitetura em camadas](../docs/uml/arquitetura-camadas.png)

```
Cliente HTTP  →  Controller  →  Service  →  Repository  →  Banco (H2)
                     ↑                                        │
                     └──────────  AlunoResponse (DTO)  ───────┘
```

A seta só aponta para a direita: o Controller não enxerga o Repository e o domínio não
conhece HTTP.

| Camada | Faz | Não faz |
| --- | --- | --- |
| `controller/` | Recebe a requisição e devolve a resposta | Regra de negócio, acesso a banco |
| `service/` | Orquestra o caso de uso e as regras que dependem de outros dados | Detalhes de HTTP ou SQL |
| `domain/` | Representa os conceitos e aplica as regras próprias de cada objeto | Conhecer a UI ou o banco |
| `repository/` | Acesso a dados | Regra de negócio |
| `dto/` | Contratos de entrada e saída, e a tradução para o domínio | Guardar estado do sistema |
| `exception/` | Erros de negócio e sua tradução para HTTP | Decidir a regra em si |

---

## Onde cada critério de aceitação da US-01 foi parar

Essa é a parte que importa na prática: a mesma funcionalidade, com cada regra na camada
que lhe cabe.

| Critério da US-01 | Onde vive | Resultado |
| --- | --- | --- |
| 3 — campo obrigatório em branco | `dto/NovoAlunoRequest` (`@NotBlank`, `@Pattern`) | `400` com a lista de campos |
| 2 — CPF já cadastrado | `service/AlunoService` (precisa consultar o repositório) | `409` |
| 4 — menor de 18 sem responsável | `domain/Aluno.informarResponsavel()` (depende só do próprio aluno) | `422` |
| 1 — matrícula única e status `ATIVO` | `service/` gera a matrícula, `domain/Aluno` nasce `ATIVO` | `201` |
| 5 — ficha com plano, valor e vencimento | `domain/Matricula` → `dto/AlunoResponse` | `200` |

O critério de decisão foi simples: **regra que depende só do próprio objeto fica no
domínio; regra que precisa consultar outros dados fica no service; formato de entrada fica
no DTO.** O Controller não decide nada.

---

## Estrutura de pastas

```
src/main/java/br/uniube/academia/
├── AcademiaApiApplication.java
├── controller/   AlunoController
├── service/      AlunoService
├── domain/       Aluno · Matricula · Plano · StatusAluno
├── repository/   AlunoRepository · PlanoRepository · MatriculaRepository
├── dto/          NovoAlunoRequest · AlunoResponse · AlunoMapper
└── exception/    CpfDuplicadoException · ResponsavelObrigatorioException
                  PlanoNaoEncontradoException · AlunoNaoEncontradoException
                  ApiExceptionHandler · ErroResposta
```

---

## Como rodar

Requisitos: **JDK 21** e Maven.

```bash
cd academia-api
mvn test          # 8 testes
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080` com banco H2 em memória, já com três planos
cadastrados (Mensal, Trimestral, Anual). Console do banco em `/h2-console`.

## Endpoints

| Método | Rota | História | Tela do protótipo |
| --- | --- | --- | --- |
| `POST` | `/alunos` | US-01 | — (recepção) |
| `GET` | `/alunos/{id}` | US-01 | — (recepção) |
| `GET` | `/alunos/{id}/ficha` | US-04 | 03 · Meu treino |
| `GET` | `/ficha/exercicios/{id}` | US-04 | 04 · Detalhe do exercício |
| `POST` | `/sessoes` | US-05 | 05 · Treino em andamento |
| `POST` | `/sessoes/{id}/execucoes` | US-05 | 05 · marcar exercício |
| `POST` | `/sessoes/{id}/finalizacao` | US-05 | 06 · Treino concluído |
| `GET` | `/sessoes/{id}` | US-05 | 05 · progresso |
| `GET` | `/aulas?dia=AAAA-MM-DD` | US-10 | 07 · Aulas coletivas |
| `POST` | `/aulas/{id}/reservas` | US-10 | 08 · Reserva confirmada |
| `DELETE` | `/reservas/{id}` | US-10 | cancelamento |

### Exemplos verificados

Cadastro válido:

```bash
curl -X POST http://localhost:8080/alunos -H 'Content-Type: application/json' \
  -d '{"nome":"Marina Souza","cpf":"12345678901","dataNascimento":"1999-03-14",
       "telefone":"34999990000","planoId":1}'
```

```json
HTTP 201
{"id":1,"matricula":"202600001","nome":"Marina Souza","cpf":"12345678901",
 "status":"ATIVO","dataInicio":"2026-11-22","plano":"Mensal",
 "valorMensalidade":119.90,"diaVencimento":22}
```

CPF repetido (critério 2):

```json
HTTP 409
{"status":409,"erro":"CPF duplicado",
 "mensagem":"CPF 12345678901 ja cadastrado para outro aluno"}
```

Nome em branco (critério 3):

```json
HTTP 400
{"status":400,"erro":"Campos invalidos",
 "campos":[{"campo":"nome","mensagem":"nome e obrigatorio"}]}
```

Menor de 18 sem responsável (critério 4):

```json
HTTP 422
{"status":422,"erro":"Responsavel obrigatorio",
 "mensagem":"Aluno Pedro Lima e menor de 18 anos: informe nome e CPF do responsavel"}
```

---

## Testes

```
AlunoTest ..................... 9  regra de idade no domínio, sem mock e sem Spring
AulaColetivaTest .............. 5  fronteira das 2 h do cancelamento, sem mock
AlunoServiceTest .............. 11 cadastro: feliz, fronteiras e erros (Clock fixo)
FichaTreinoServiceTest ........ 5  consulta da ficha por divisão (US-04)
SessaoTreinoServiceTest ....... 6  progresso, carga real e sessão encerrada (US-05)
AulaColetivaServiceTest ....... 7  vaga, lista de espera, bloqueio e prazo (US-10)
AlunoControllerTest ...........  3 contrato HTTP, com o service mockado
AcademiaApiApplicationTests ...  1 as camadas se conectam de verdade
                                47 testes, 0 falhas
```

```bash
cd academia-api && mvn test
```

Os cenários da US-01 foram listados **antes** de escrever os testes, em
[`docs/cenarios-de-teste.md`](../docs/cenarios-de-teste.md), e cada teste segue
Arrange-Act-Assert explícito.

### Testes que protegem a arquitetura

`AlunoControllerTest` verifica com `verify(service, never())` que entrada inválida **não
chega ao service**. `AlunoServiceTest.cpfDuplicado` verifica que, com CPF repetido, nem o
plano é consultado. `AulaColetivaServiceTest.inadimplenteNaoReserva` verifica que a aula
nem é buscada quando o aluno está bloqueado. Se alguém mover uma validação de camada, os
testes quebram.

### Uma refatoração que veio dos testes

Os cenários de fronteira ("faz 18 anos exatamente hoje", "faz 18 anos amanhã") não eram
testáveis de forma repetível: `Aluno.isMenorDeIdade()` lia `LocalDate.now()` por dentro, e
o resultado dependeria do dia em que a suíte rodasse.

| Antes | Depois |
| --- | --- |
| `isMenorDeIdade()` | `isMenorDeIdade(LocalDate hoje)` |
| `AlunoService` chamava `LocalDate.now()` | recebe um `java.time.Clock` injetado |

Em produção o Spring injeta `Clock.systemDefaultZone()`; no teste, um `Clock.fixed` deixa as
datas sob controle. Os services de sessão e de aulas já nasceram com `Clock` injetado por
causa dessa lição — é o que torna possível testar a fronteira das 2 h do cancelamento.

## Limitações conhecidas do MVP

| Limitação | Observação |
| --- | --- |
| Sem autenticação | A tela 01 do protótipo existe, mas o `alunoId` é informado na requisição |
| Banco em memória | H2 com `create-drop`: os dados somem ao reiniciar |
| Sem interface gráfica | O MVP é a API; o protótipo representa a interface pretendida |
| US-02, 03, 06, 07, 08, 09 | Permanecem no backlog, priorizadas para as próximas iterações |
