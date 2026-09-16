#!/usr/bin/env bash
# Cria as labels e as 10 Issues do backlog neste repositório.
#
# Pré-requisitos:
#   1. GitHub CLI instalado  -> https://cli.github.com
#   2. Autenticado           -> gh auth login
#   3. Executado de dentro do repositório clonado
#
# Uso:  bash scripts/criar-issues.sh
#
# ATENÇÃO: as 10 Issues deste backlog JÁ FORAM CRIADAS (#1 a #10). Este script existe
# como registro do processo e para recriar o backlog em outro repositório (um fork da
# equipe, por exemplo). Rodá-lo neste repositório criaria Issues duplicadas.
# Para apenas corrigir as cores das labels, use scripts/aplicar-cores-labels.sh.

set -euo pipefail

cd "$(dirname "$0")/.."

command -v gh >/dev/null || { echo "gh não encontrado. Instale: https://cli.github.com"; exit 1; }
command -v jq >/dev/null || { echo "jq não encontrado. Instale: https://jqlang.github.io/jq/"; exit 1; }

echo "==> Criando labels"
criar_label() {
  gh label create "$1" --color "$2" --description "$3" 2>/dev/null \
    && echo "    + $1" \
    || echo "    = $1 (já existe)"
}

criar_label "user-story"          "0E8A16" "História de usuário do backlog"
criar_label "prioridade: alta"    "B60205" "Must have — sem isso a academia não opera"
criar_label "prioridade: média"   "FBCA04" "Should have — entra na segunda iteração"
criar_label "prioridade: baixa"   "C2E0C6" "Could have — desejável"
criar_label "perfil: aluno"       "1D76DB" "História do perfil Aluno"
criar_label "perfil: instrutor"   "5319E7" "História do perfil Instrutor"
criar_label "perfil: recepção"    "006B75" "História do perfil Recepcionista"
criar_label "perfil: gerente"     "BF5B17" "História do perfil Gerente/Proprietário"
criar_label "módulo: cadastro"    "D4C5F9" "Cadastro de alunos e planos"
criar_label "módulo: acesso"      "D4C5F9" "Check-in e controle de entrada"
criar_label "módulo: treino"      "D4C5F9" "Fichas de treino e execução"
criar_label "módulo: avaliação"   "D4C5F9" "Avaliações físicas"
criar_label "módulo: financeiro"  "D4C5F9" "Mensalidades, pagamentos e inadimplência"
criar_label "módulo: agendamento" "D4C5F9" "Aulas coletivas e reservas"
criar_label "módulo: relatórios"  "D4C5F9" "Relatórios gerenciais"

echo "==> Criando as Issues"
jq -c '.[]' docs/issues/manifest.json | while read -r item; do
  code=$(jq -r '.code'  <<<"$item")
  title=$(jq -r '.title' <<<"$item")
  mapfile -t labels < <(jq -r '.labels[]' <<<"$item")

  args=()
  for l in "${labels[@]}"; do args+=(--label "$l"); done

  url=$(gh issue create --title "$title" --body-file "docs/issues/${code}.md" "${args[@]}")
  echo "    $code -> $url"
done

echo
echo "Pronto. Agora crie o Project seguindo docs/project-setup.md"
