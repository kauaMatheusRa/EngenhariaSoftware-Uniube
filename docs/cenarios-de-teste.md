# Cenários de teste — US-01 (Cadastro de aluno)

Prática guiada da Aula 12 (Testes Unitários).
**Passo 2 do enunciado: listar os cenários antes de escrever qualquer código de teste.**

Unidade escolhida: **`AlunoService.cadastrar()`** e a regra de idade em **`domain/Aluno`**.
Motivo: é a classe que concentra as regras da história — unicidade de CPF, existência do
plano, geração de matrícula e a delegação da regra de menoridade ao domínio.

## Os cenários, antes do código

| # | Cenário | Tipo | Resultado esperado |
| --- | --- | --- | --- |
| 1 | Adulto com todos os dados válidos | caminho feliz | Cadastrado `ATIVO`, com matrícula e ficha completa |
| 2 | CPF já existente na base | erro | `CpfDuplicadoException`, nada é salvo |
| 3 | Plano informado não existe | erro | `PlanoNaoEncontradoException`, nada é salvo |
| 4 | Aluno faz 18 anos exatamente hoje | **fronteira** | Cadastra sem exigir responsável |
| 5 | Aluno faz 18 anos amanhã | **fronteira** | Exige responsável (ainda é menor) |
| 6 | Menor com nome e CPF do responsável | caminho feliz | Cadastrado normalmente |
| 7 | Menor com responsável pela metade (só o nome) | erro | `ResponsavelObrigatorioException` |
| 8 | Primeiro aluno da base (zero cadastros) | **fronteira** | Matrícula termina em `00001` |
| 9 | Base com 7 alunos | sequência | Matrícula termina em `00008` |
| 10 | Plano trimestral | regra | `dataFim` = início + 3 meses |
| 11 | Ficha de aluno inexistente | erro | `AlunoNaoEncontradoException` |

Onze cenários — o enunciado pede no mínimo cinco, e os três tipos exigidos
(caminho feliz, fronteira e erro) estão cobertos.

## O que a lista revelou antes de escrever o primeiro teste

Os cenários **4 e 5** não eram testáveis com o código como estava.

`Aluno.isMenorDeIdade()` chamava `LocalDate.now()` por dentro. Para testar "faz 18 anos
exatamente hoje" seria preciso calcular a data de nascimento a partir do relógio real — o
teste passaria hoje e poderia falhar em outro dia, violando a propriedade **repetível**
discutida na aula.

Isso é o passo 6 do enunciado acontecendo na prática: *"refatore a implementação se os
testes revelarem design difícil de testar"*. A correção está descrita abaixo.

## Refatoração feita por causa dos testes

| Antes | Depois |
| --- | --- |
| `Aluno.isMenorDeIdade()` lia `LocalDate.now()` | `Aluno.isMenorDeIdade(LocalDate hoje)` recebe a data de referência |
| `AlunoService` chamava `LocalDate.now()` | `AlunoService` recebe um `java.time.Clock` injetado |

Em produção o Spring injeta `Clock.systemDefaultZone()`, então o comportamento é idêntico.
No teste, injeta-se um `Clock.fixed(...)` e as datas ficam sob controle — o cenário de
fronteira passa a ser determinístico.

Ganho secundário: o domínio deixou de depender do relógio do sistema, que é um recurso
externo. É a mesma ideia de não depender de banco ou rede dentro de um teste unitário.
