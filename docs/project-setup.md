# Organização do GitHub Project

O backlog é acompanhado em um **GitHub Project** (Projects v2) ligado a este repositório,
com colunas de andamento no modelo Kanban.

## Colunas e o que significa estar em cada uma

| Coluna | Significado | Critério de saída |
| --- | --- | --- |
| **Backlog** | História escrita e priorizada, ainda não selecionada para a iteração | Foi escolhida no planejamento da iteração |
| **A fazer** | Selecionada para a iteração atual, com responsável definido | Alguém começou a implementar |
| **Em andamento** | Em desenvolvimento; existe branch aberto | Código pronto e Pull Request aberto |
| **Em revisão** | Pull Request aberto, em revisão pela equipe | PR aprovado e critérios de aceitação verificados |
| **Concluído** | Mesclado na branch principal e com todos os critérios de aceitação atendidos | — |

**Definição de pronto (DoD):** todos os critérios de aceitação da história verificados,
código revisado por outra pessoa da equipe, Pull Request mesclado e Issue fechada
referenciando o commit (`Closes #N`).

## Distribuição inicial sugerida

As dez histórias entram no Project em **Backlog**; as de prioridade alta são puxadas para
**A fazer** na primeira iteração:

- **Iteração 1 (prioridade alta):** US-01, US-02, US-03, US-04, US-07, US-09
- **Iteração 2 (prioridade média):** US-05, US-06, US-08, US-10

## Como criar o Project (passo a passo)

1. No repositório, abra a aba **Projects** → **Link a project** → **New project**.
2. Escolha o template **Board** e nomeie: `Backlog — Sistema Academia`.
3. Renomeie as colunas padrão (`Todo` / `In Progress` / `Done`) e acrescente as demais até
   ficar: **Backlog · A fazer · Em andamento · Em revisão · Concluído**.
4. Em **Add item** → **Add item from repository**, selecione as dez Issues `[US-01]`…`[US-10]`.
   (Atalho: digitar `#` no campo de item lista as Issues do repositório.)
5. Arraste as seis histórias de prioridade alta para **A fazer** e deixe as demais em **Backlog**.
6. Opcional — em **⋯ → Settings → Workflows**, ative:
   - *Item closed* → mover para **Concluído**;
   - *Pull request merged* → mover para **Concluído**.
7. Em **⋯ → Settings → Manage access**, marque o Project como **Public** para que o professor
   consiga abrir o link sem precisar de convite.

## Campos adicionais úteis (opcional)

Em **+ (nova coluna de campo)** dentro do Project:

- `Prioridade` (single select: Alta / Média / Baixa) — espelha a label da Issue;
- `Estimativa` (number) — pontos de história;
- `Iteração` (iteration) — para acompanhar o andamento por sprint.
