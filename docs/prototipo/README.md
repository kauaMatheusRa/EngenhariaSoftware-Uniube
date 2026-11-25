# Protótipo de interface

Protótipo das telas do app do aluno, feito no **Figma**.

🔗 **[Abrir no Figma](https://www.figma.com/design/y5bZomj9VPFv1OgNHj9PMJ/Prot%C3%B3tipo-Sistema-Academia-%E2%80%93-Kau%C3%A3-Azambuja)**

---

## Biblioteca de componentes

O protótipo não é um conjunto de telas soltas: tem uma biblioteca de componentes
reutilizados em todas elas, o que mantém a consistência visual e aproxima o desenho de um
design system de verdade.

| Componente | Onde aparece |
| --- | --- |
| `Botão/Primário` · `Botão/Secundário` | Ações principais e secundárias de cada tela |
| `Campo` | Login e entrada de carga no detalhe do exercício |
| `Cabeçalho` | Telas internas com navegação de volta |
| `Card/Exercício` | Lista de exercícios da ficha |
| `Card/Aula` | Grade de aulas coletivas |
| `Tab bar` (4 estados: Início, Treino, Aulas, Perfil) | Navegação principal do app |

---

## As telas, e o que cada uma virou no código

Esta tabela é a ponte entre o que foi desenhado, o requisito que originou o desenho e o
endpoint que o implementa.

| # | Tela | História | Endpoint |
| --- | --- | --- | --- |
| 01 | Login | entrada do fluxo | — (fora do escopo do MVP) |
| 02 | Início | navegação | — |
| 03 | Meu treino | **US-04** | `GET /alunos/{id}/ficha` |
| 04 | Detalhe do exercício | **US-04** · **US-05** | `GET /ficha/exercicios/{id}` |
| 05 | Meu treino (em andamento) | **US-05** | `POST /sessoes` · `POST /sessoes/{id}/execucoes` |
| 06 | Treino concluído | **US-05** | `POST /sessoes/{id}/finalizacao` |
| 07 | Aulas coletivas | **US-10** | `GET /aulas?dia=AAAA-MM-DD` |
| 08 | Reserva confirmada | **US-10** | `POST /aulas/{id}/reservas` |

---

## Decisões de regra que nasceram no protótipo

Três textos escritos nas telas viraram regra implementada e testada — vale apontar isso na
apresentação, porque mostra o protótipo cumprindo seu papel de alinhar entendimento antes
do código:

| Texto na tela | Onde virou código |
| --- | --- |
| *"A carga informada fica só no registro de hoje; a ficha não muda"* (tela 04) | `SessaoTreino.registrarExecucao()` grava a carga na execução; `ItemTreino` fica intacto. Teste: `cargaRealNaoMudaAFicha` |
| *"Cancele até 2 h antes do início para liberar a vaga"* (tela 07) | `AulaColetiva.permiteCancelamentoEm()`. Teste: `fronteiraDoCancelamento`, com os 4 casos de borda |
| *"1 de 4 exercícios concluídos (25%)"* (tela 05) | `SessaoTreino.percentualConcluido()`. Teste: `progressoDe25PorCento` |

E a tela 08 mostra "Vagas restantes: 2" junto do horário-limite de cancelamento — os dois
campos existem no `ReservaResponse` exatamente por causa disso.

---

## O que o protótipo cobre e o código ainda não

| Tela | Situação |
| --- | --- |
| 01 · Login | Autenticação ficou fora do MVP: o `alunoId` é informado direto na requisição |
| 02 · Início | É composição das outras telas; não tem endpoint próprio |

Essas duas são as limitações conhecidas do MVP, e estão registradas como tal no
[README do módulo](../../academia-api/README.md).
