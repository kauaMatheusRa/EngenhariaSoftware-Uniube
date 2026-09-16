# Backlog — matriz de rastreabilidade

Uma linha por User Story, ligando história → Issue → labels → prioridade → iteração.
Os números de Issue abaixo (`#1`…`#10`) correspondem à ordem de criação pelo script
[`scripts/criar-issues.sh`](../scripts/criar-issues.sh) em um repositório sem Issues anteriores.

| ID | História (resumo) | Issue | Perfil | Módulo | Prioridade | Iteração |
| --- | --- | --- | --- | --- | --- | --- |
| US-01 | Cadastro de aluno com plano | [#1](../../issues/1) | recepção | cadastro | Alta | 1 |
| US-02 | Check-in na entrada | [#2](../../issues/2) | aluno | acesso | Alta | 1 |
| US-03 | Montagem da ficha de treino | [#3](../../issues/3) | instrutor | treino | Alta | 1 |
| US-04 | Consulta da ficha no app | [#4](../../issues/4) | aluno | treino | Alta | 1 |
| US-05 | Registro de execução do treino | [#5](../../issues/5) | aluno | treino | Média | 2 |
| US-06 | Avaliação física periódica | [#6](../../issues/6) | instrutor | avaliação | Média | 2 |
| US-07 | Renovação de plano e pagamento | [#7](../../issues/7) | recepção | financeiro | Alta | 1 |
| US-08 | Relatório de frequência e ocupação | [#8](../../issues/8) | gerente | relatórios | Média | 2 |
| US-09 | Inadimplência e bloqueio de acesso | [#9](../../issues/9) | gerente | financeiro | Alta | 1 |
| US-10 | Agendamento de aula coletiva | [#10](../../issues/10) | aluno | agendamento | Média | 2 |

## Cobertura por perfil

| Perfil | Histórias | Total |
| --- | --- | --- |
| Aluno | US-02, US-04, US-05, US-10 | 4 |
| Instrutor | US-03, US-06 | 2 |
| Recepcionista | US-01, US-07 | 2 |
| Gerente | US-08, US-09 | 2 |

## Cobertura por prioridade

| Prioridade | Histórias | Total |
| --- | --- | --- |
| Alta (*Must*) | US-01, US-02, US-03, US-04, US-07, US-09 | 6 |
| Média (*Should*) | US-05, US-06, US-08, US-10 | 4 |

## Dependências entre histórias

| História | Depende de | Por quê |
| --- | --- | --- |
| US-02 | US-01 | Só há check-in depois que existe aluno cadastrado |
| US-04 | US-03 | O app exibe a ficha que o instrutor montou |
| US-05 | US-04 | O registro de execução acontece sobre a ficha exibida |
| US-09 | US-01, US-07 | O bloqueio depende do plano e do registro de pagamento |
| US-10 | US-01, US-09 | A reserva valida cadastro ativo e situação financeira |

Isso define a ordem natural da iteração 1: **US-01 → US-07 → US-02 → US-09**, com
**US-03 → US-04** em paralelo.

## Como manter a rastreabilidade

1. Ao começar uma história, crie o branch `us-01-cadastro-aluno` (ID + resumo).
2. Nos commits, comece a mensagem pelo ID: `US-01: valida CPF duplicado no cadastro`.
3. No Pull Request, escreva `Closes #1` — a Issue fecha ao mesclar e o Project move o
   cartão para **Concluído**.
4. Se um critério de aceitação mudar, edite `docs/user-stories.md` **e** o corpo da Issue,
   mantendo os dois idênticos.
