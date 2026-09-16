#!/usr/bin/env bash
# Aplica as cores padronizadas às labels do backlog.
#
# As labels já existem (foram criadas junto com as Issues), mas com a cor
# aleatória que o GitHub atribui. Este script corrige as cores e descrições.
# É idempotente: pode rodar quantas vezes quiser.
#
# Pré-requisitos: gh CLI autenticado (gh auth login)
# Uso: bash scripts/aplicar-cores-labels.sh

set -euo pipefail
cd "$(dirname "$0")/.."

command -v gh >/dev/null || { echo "gh não encontrado. Instale: https://cli.github.com"; exit 1; }

aplicar() {
  gh label create "$1" --color "$2" --description "$3" --force >/dev/null
  echo "    $1"
}

echo "==> Aplicando cores às labels"
aplicar "user-story"          "0E8A16" "História de usuário do backlog"
aplicar "prioridade: alta"    "B60205" "Must have — sem isso a academia não opera"
aplicar "prioridade: média"   "FBCA04" "Should have — entra na segunda iteração"
aplicar "prioridade: baixa"   "C2E0C6" "Could have — desejável"
aplicar "perfil: aluno"       "1D76DB" "História do perfil Aluno"
aplicar "perfil: instrutor"   "5319E7" "História do perfil Instrutor"
aplicar "perfil: recepção"    "006B75" "História do perfil Recepcionista"
aplicar "perfil: gerente"     "BF5B17" "História do perfil Gerente/Proprietário"
aplicar "módulo: cadastro"    "D4C5F9" "Cadastro de alunos e planos"
aplicar "módulo: acesso"      "D4C5F9" "Check-in e controle de entrada"
aplicar "módulo: treino"      "D4C5F9" "Fichas de treino e execução"
aplicar "módulo: avaliação"   "D4C5F9" "Avaliações físicas"
aplicar "módulo: financeiro"  "D4C5F9" "Mensalidades, pagamentos e inadimplência"
aplicar "módulo: agendamento" "D4C5F9" "Aulas coletivas e reservas"
aplicar "módulo: relatórios"  "D4C5F9" "Relatórios gerenciais"
echo "Pronto."
