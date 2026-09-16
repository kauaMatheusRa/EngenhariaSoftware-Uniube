# User Stories e critérios de aceitação

Dez histórias, cada uma com no mínimo três critérios de aceitação escritos no formato
**Dado / Quando / Então**. Cada história corresponde a uma Issue do repositório
(ver `backlog.md` para o mapeamento).

Legenda de prioridade (MoSCoW): **Alta** = *Must have* · **Média** = *Should have* · **Baixa** = *Could have*

---

## US-01 — Cadastro de aluno com plano

> **Como** recepcionista, **quero** cadastrar um novo aluno com seus dados e plano
> contratado, **para que** ele possa treinar e ser cobrado corretamente desde o primeiro dia.

**Perfil:** recepção · **Módulo:** cadastro · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que estou na tela de novo cadastro, **quando** informo nome, CPF, data de
   nascimento, telefone e plano e confirmo, **então** o aluno é salvo com status *ativo* e
   recebe um número de matrícula único.
2. **Dado** que informei um CPF já cadastrado, **quando** tento salvar, **então** o sistema
   bloqueia a gravação e exibe "CPF já cadastrado para o aluno <nome>", sem criar duplicata.
3. **Dado** que deixei em branco um campo obrigatório (nome, CPF ou plano), **quando** tento
   salvar, **então** o campo é destacado com a mensagem do erro e nada é gravado.
4. **Dado** que o aluno é menor de 18 anos, **quando** salvo o cadastro, **então** o sistema
   exige nome e CPF do responsável antes de concluir.
5. **Dado** que o cadastro foi concluído, **quando** abro a ficha do aluno, **então** vejo a
   data de início, o plano, o valor da mensalidade e o dia de vencimento.

---

## US-02 — Check-in na entrada

> **Como** aluno, **quero** fazer check-in na entrada usando o QR Code do meu app ou minha
> matrícula, **para que** meu acesso seja liberado sem depender de atendimento na recepção.

**Perfil:** aluno · **Módulo:** acesso · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que sou um aluno ativo e adimplente, **quando** apresento meu QR Code ao leitor,
   **então** a catraca libera em até 2 segundos e o check-in é registrado com data e hora.
2. **Dado** que meu plano está vencido ou minha mensalidade está em atraso, **quando** tento
   o check-in, **então** o acesso é negado e a tela da recepção mostra o motivo do bloqueio.
3. **Dado** que meu QR Code não é lido, **quando** digito meu número de matrícula no teclado
   da catraca, **então** o acesso segue as mesmas regras do QR Code.
4. **Dado** que já fiz check-in há menos de 5 minutos, **quando** apresento o código de novo,
   **então** o sistema não registra um segundo check-in no mesmo dia/sessão.

---

## US-03 — Montagem da ficha de treino

> **Como** instrutor, **quero** montar a ficha de treino de um aluno a partir de um catálogo
> de exercícios, **para que** ele treine conforme a prescrição e eu tenha o registro do que foi
> prescrito.

**Perfil:** instrutor · **Módulo:** treino · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que busquei um aluno ativo, **quando** crio uma ficha e adiciono exercícios
   informando séries, repetições, carga e descanso, **então** a ficha é salva vinculada ao
   aluno, a mim e à data de criação.
2. **Dado** que estou montando a ficha, **quando** busco um exercício pelo nome ou grupo
   muscular, **então** o catálogo retorna os resultados correspondentes para inclusão.
3. **Dado** que a ficha não tem nenhum exercício, **quando** tento salvar, **então** o sistema
   impede e informa que a ficha precisa de ao menos um exercício.
4. **Dado** que o aluno já tem uma ficha vigente, **quando** salvo uma nova, **então** a
   anterior é arquivada no histórico (não apagada) e a nova passa a ser a vigente.
5. **Dado** que a ficha foi salva, **quando** o aluno abre o app, **então** ela aparece como
   treino atual sem necessidade de nova ação minha.

---

## US-04 — Consulta da ficha no app

> **Como** aluno, **quero** consultar minha ficha de treino no celular, **para que** eu não
> dependa da ficha impressa para treinar.

**Perfil:** aluno · **Módulo:** treino · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que estou autenticado no app e tenho ficha vigente, **quando** abro "Meu treino",
   **então** vejo os exercícios do dia agrupados por divisão (A, B, C) com séries,
   repetições, carga e descanso.
2. **Dado** que toco em um exercício, **quando** a tela de detalhe abre, **então** vejo a
   orientação de execução e a imagem/vídeo demonstrativo cadastrados no catálogo.
3. **Dado** que não tenho ficha vigente, **quando** abro "Meu treino", **então** vejo a
   mensagem "Procure seu instrutor para montar sua ficha" em vez de uma tela vazia.
4. **Dado** que estou sem conexão, **quando** abro o app, **então** a última ficha
   sincronizada é exibida com o aviso de que está offline.

---

## US-05 — Registro de execução do treino

> **Como** aluno, **quero** marcar os exercícios que executei e a carga usada, **para que** eu
> e meu instrutor acompanhemos minha evolução.

**Perfil:** aluno · **Módulo:** treino · **Prioridade:** Média

### Critérios de aceitação

1. **Dado** que estou na tela do treino do dia, **quando** marco um exercício como concluído,
   **então** ele fica sinalizado como feito e o percentual de conclusão do treino é atualizado.
2. **Dado** que usei carga diferente da prescrita, **quando** informo a carga real,
   **então** o valor é salvo no registro daquele dia sem alterar a prescrição da ficha.
3. **Dado** que concluí todos os exercícios, **quando** finalizo o treino, **então** a sessão
   é gravada com data, duração e exercícios executados, e passa a constar no meu histórico.
4. **Dado** que abro o histórico, **quando** seleciono um exercício, **então** vejo a evolução
   de carga dele nas últimas 8 semanas.

---

## US-06 — Avaliação física periódica

> **Como** instrutor, **quero** registrar a avaliação física do aluno, **para que** a evolução
> fique documentada e eu ajuste o treino com base em dados.

**Perfil:** instrutor · **Módulo:** avaliação · **Prioridade:** Média

### Critérios de aceitação

1. **Dado** que selecionei um aluno, **quando** registro peso, altura, percentual de gordura e
   circunferências, **então** a avaliação é salva com a data e o IMC é calculado automaticamente.
2. **Dado** que o aluno tem duas ou mais avaliações, **quando** abro a comparação,
   **então** vejo a variação de cada indicador entre a primeira, a anterior e a atual.
3. **Dado** que informo um valor fora da faixa plausível (ex.: peso 0 kg ou 600 kg),
   **quando** tento salvar, **então** o sistema recusa o valor e explica a faixa aceita.
4. **Dado** que a avaliação foi salva, **quando** o aluno abre o app, **então** ele vê o
   resultado e a data sugerida da próxima avaliação (3 meses depois).

---

## US-07 — Renovação de plano e registro de pagamento

> **Como** recepcionista, **quero** renovar ou alterar o plano do aluno e registrar o
> pagamento, **para que** a situação financeira e o acesso dele fiquem corretos na hora.

**Perfil:** recepção · **Módulo:** financeiro · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que abri a ficha de um aluno, **quando** seleciono um novo plano e confirmo,
   **então** o sistema registra a troca com data de início, novo valor e mantém o histórico
   do plano anterior.
2. **Dado** que registro um pagamento informando valor, forma e data, **quando** confirmo,
   **então** a mensalidade correspondente muda para *paga* e o acesso do aluno é liberado
   imediatamente.
3. **Dado** que o valor informado é menor que o da mensalidade, **quando** confirmo,
   **então** o sistema registra o pagamento parcial e mantém o saldo devedor visível na ficha.
4. **Dado** que um pagamento foi lançado por engano, **quando** o estorno é feito pela
   recepção, **então** a operação exige justificativa e fica registrada com usuário e horário.

---

## US-08 — Relatório de frequência e ocupação

> **Como** gerente, **quero** ver a frequência dos alunos e a ocupação por faixa de horário,
> **para que** eu ajuste a escala de instrutores e a grade de aulas.

**Perfil:** gerente · **Módulo:** relatórios · **Prioridade:** Média

### Critérios de aceitação

1. **Dado** que escolhi um período (semana ou mês), **quando** abro o relatório de ocupação,
   **então** vejo o total de check-ins por faixa de horário e por dia da semana.
2. **Dado** que o relatório está aberto, **quando** aplico filtro por plano ou por instrutor,
   **então** os números são recalculados apenas para o recorte selecionado.
3. **Dado** que quero levar o dado para a reunião, **quando** clico em exportar,
   **então** recebo o relatório em CSV ou PDF com o período no cabeçalho.
4. **Dado** que não houve check-in no período filtrado, **quando** o relatório carrega,
   **então** vejo "Sem registros no período" em vez de um gráfico vazio.

---

## US-09 — Painel de inadimplência e bloqueio de acesso

> **Como** gerente, **quero** acompanhar os alunos inadimplentes e ter o bloqueio de acesso
> aplicado automaticamente, **para que** a cobrança não dependa de conferência manual.

**Perfil:** gerente · **Módulo:** financeiro · **Prioridade:** Alta

### Critérios de aceitação

1. **Dado** que a mensalidade de um aluno venceu há mais dias que o prazo de tolerância
   configurado, **quando** o processo diário roda, **então** o aluno passa a *inadimplente* e
   seu acesso é bloqueado na catraca.
2. **Dado** que abro o painel, **quando** ele carrega, **então** vejo a lista de inadimplentes
   com nome, dias em atraso, valor devido e telefone, ordenada por dias em atraso.
3. **Dado** que o pagamento em atraso é registrado (US-07), **quando** a confirmação ocorre,
   **então** o aluno sai do painel e o acesso é desbloqueado na mesma hora.
4. **Dado** que um aluno negociou o débito, **quando** a recepção registra a liberação
   temporária com prazo, **então** o acesso é liberado até a data acordada e o caso continua
   visível no painel, sinalizado como negociado.
5. **Dado** que o prazo de tolerância é alterado nas configurações, **quando** salvo,
   **então** a nova regra vale a partir do próximo processamento diário.

---

## US-10 — Agendamento de aula coletiva

> **Como** aluno, **quero** reservar minha vaga nas aulas coletivas, **para que** eu não
> chegue à academia e encontre a turma lotada.

**Perfil:** aluno · **Módulo:** agendamento · **Prioridade:** Média

### Critérios de aceitação

1. **Dado** que estou na grade de aulas, **quando** escolho uma aula com vaga disponível,
   **então** minha reserva é confirmada e o número de vagas restantes diminui em um.
2. **Dado** que a aula já atingiu o limite de vagas, **quando** tento reservar, **então** entro
   em uma lista de espera e sou avisado se uma vaga for liberada.
3. **Dado** que tenho uma reserva, **quando** cancelo com mais de 2 horas de antecedência,
   **então** a vaga volta a ficar disponível e a primeira pessoa da lista de espera é notificada.
4. **Dado** que faltei sem cancelar três vezes no mesmo mês, **quando** tento uma nova reserva,
   **então** o sistema bloqueia a reserva antecipada por 7 dias e explica o motivo.
5. **Dado** que meu acesso está bloqueado por inadimplência, **quando** tento reservar,
   **então** a reserva é recusada com a mesma mensagem do bloqueio de acesso.
