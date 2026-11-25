-- Planos iniciais, para que o cadastro de aluno tenha o que referenciar.
INSERT INTO plano (id, nome, valor_mensal, duracao_meses) VALUES (1, 'Mensal', 119.90, 1);
INSERT INTO plano (id, nome, valor_mensal, duracao_meses) VALUES (2, 'Trimestral', 99.90, 3);
INSERT INTO plano (id, nome, valor_mensal, duracao_meses) VALUES (3, 'Anual', 79.90, 12);

-- Instrutores
INSERT INTO instrutor (id, nome, cref) VALUES (1, 'Rafael Antunes', 'CREF 012345-G/MG');
INSERT INTO instrutor (id, nome, cref) VALUES (2, 'Camila Prado', 'CREF 067890-G/MG');

-- Catalogo de exercicios (US-04, criterio 2: orientacao e video)
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (1, 'Supino reto com barra', 'Peito',
  'Deite no banco com os pes no chao. Desca a barra ate a linha do peito e empurre ate estender os bracos, sem travar os cotovelos.',
  'https://videos.academia.local/supino-reto');
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (2, 'Supino inclinado com halteres', 'Peito',
  'Banco a 30 graus. Desca os halteres na linha da clavicula e suba sem bater um no outro.',
  'https://videos.academia.local/supino-inclinado');
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (3, 'Triceps na polia', 'Triceps',
  'Cotovelos colados ao tronco. Estenda ate o fim do movimento e volte controlando a carga.',
  'https://videos.academia.local/triceps-polia');
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (4, 'Triceps testa', 'Triceps',
  'Desca a barra ate a testa mantendo os cotovelos apontados para cima.',
  'https://videos.academia.local/triceps-testa');
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (5, 'Puxada frontal', 'Costas',
  'Puxe a barra ate a altura do queixo, juntando as escapulas.',
  'https://videos.academia.local/puxada-frontal');
INSERT INTO exercicio (id, nome, grupo_muscular, orientacao, video_url) VALUES
 (6, 'Remada curvada', 'Costas',
  'Tronco inclinado a 45 graus, costas retas. Puxe a barra ate o umbigo.',
  'https://videos.academia.local/remada-curvada');

-- Aluna de demonstracao: a mesma Marina do prototipo
INSERT INTO aluno (id, matricula, nome, cpf, data_nascimento, telefone, status)
 VALUES (1, '202600001', 'Marina Souza', '12345678901', DATE '1999-03-14', '34999990000', 'ATIVO');
INSERT INTO matricula (id, aluno_id, plano_id, data_inicio, data_fim, dia_vencimento)
 VALUES (1, 1, 1, DATE '2026-09-01', DATE '2026-10-01', 1);

-- Aluno inadimplente, para demonstrar o bloqueio da US-10
INSERT INTO aluno (id, matricula, nome, cpf, data_nascimento, telefone, status)
 VALUES (2, '202600002', 'Bruno Carvalho', '98765432100', DATE '1995-07-22', '34988887777', 'INADIMPLENTE');

-- Ficha vigente da Marina: Treino A (peito e triceps) e Treino B (costas)
INSERT INTO ficha_treino (id, aluno_id, instrutor_id, titulo, data_criacao, vigente)
 VALUES (1, 1, 1, 'Treino A - Peito e triceps', DATE '2026-09-01', TRUE);
INSERT INTO item_treino (id, ficha_id, exercicio_id, divisao, ordem, series, repeticoes, carga, descanso_segundos) VALUES
 (1, 1, 1, 'A', 1, 4, 12, 30.0, 60),
 (2, 1, 2, 'A', 2, 3, 12, 14.0, 60),
 (3, 1, 3, 'A', 3, 3, 15, 25.0, 45),
 (4, 1, 4, 'A', 4, 3, 12, 20.0, 45),
 (5, 1, 5, 'B', 1, 4, 12, 45.0, 60),
 (6, 1, 6, 'B', 2, 4, 10, 30.0, 60);

-- Grade de aulas coletivas: Spinning com 2 vagas, para demonstrar lotacao e lista de espera
INSERT INTO aula_coletiva (id, modalidade, data_hora, vagas, instrutor_id) VALUES
 (1, 'Spinning', TIMESTAMP '2026-12-30 19:00:00', 2, 1),
 (2, 'Funcional', TIMESTAMP '2026-12-30 07:00:00', 12, 2),
 (3, 'Pilates', TIMESTAMP '2026-12-30 18:00:00', 8, 2);
