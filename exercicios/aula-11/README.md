# Exercícios da Aula 11 — Design Patterns

Dois exercícios propostos nos slides da aula: classe abstrata (slide 26) e padrão DAO
(slide 27).

📄 **[`exercicios-aula-11.pdf`](exercicios-aula-11.pdf)** — documento de 11 páginas com
enunciado, resolução comentada, classe de teste e saída de execução dos dois exercícios. São exercícios independentes do projeto da academia, por isso ficam em pasta
separada e sem framework — Java puro, compilável com `javac`.

> **Sobre os nomes dos métodos:** o enunciado escreve `exibir_info()` e `calcular_custo()`
> em snake_case. Mantive exatamente como pedido, embora a convenção Java seja camelCase
> (`exibirInfo`, `calcularCusto`).

---

## Exercício 1 — Classe abstrata `Veiculo`

### O que o enunciado pede

1. Classe abstrata `Veiculo` com os métodos abstratos `exibir_info()` e `calcular_custo()`
2. Subclasses `Carro` (com atributo de combustível) e `Bicicleta` (sem atributos extras)
3. `exibir_info()` mostra o tipo de veículo e, no carro, o combustível
4. `calcular_custo()`: carro = 0,2 (gasolina) ou 0,3 (dísel); bicicleta = 0,0
5. Classe `Teste` para testar

### Arquivos

```
abstract/
├── Veiculo.java      classe abstrata — define o contrato
├── Carro.java        subclasse com tipo de combustível
├── Bicicleta.java    subclasse sem atributos adicionais
└── Teste.java        classe de teste
```

### Rodar

```bash
cd exercicios/aula-11/abstract
javac *.java && java Teste
```

### Saída

```
=== Frota ===
Tipo de veiculo: Carro | Combustivel: gasolina custo: 0.20
Tipo de veiculo: Carro | Combustivel: disel    custo: 0.30
Tipo de veiculo: Bicicleta                     custo: 0.00

=== Custo total da frota ===
Total: 0.50

=== Combustivel nao previsto ===
Recusado: Combustivel nao previsto: etanol
```

### O ponto do exercício

Está no laço de `Teste`: o código percorre uma `List<Veiculo>` **sem saber** se cada item é
`Carro` ou `Bicicleta`, e cada objeto responde do seu jeito. É polimorfismo sustentado pela
classe abstrata — adicionar uma `Moto` amanhã não exige mexer no laço.

---

## Exercício 2 — Padrão DAO (biblioteca)

### O que o enunciado pede

Sistema simples de gerenciamento de livros com as cinco operações CRUD: adicionar, buscar
por ID, listar todos, atualizar e excluir. Com entidade `Livro`, interface `LivroDAO`,
implementação `LivroDAOImpl` (lista ou JDBC) e classe de teste.

### Arquivos

```
dao/
├── Livro.java          entidade — só guarda dados, não sabe como é persistida
├── LivroDAO.java       interface com as 5 operações CRUD
├── LivroDAOImpl.java   implementação com lista em memória
└── TesteLivroDAO.java  classe de teste exercitando o CRUD
```

### Rodar

```bash
cd exercicios/aula-11/dao
javac *.java && java TesteLivroDAO
```

### Saída

```
=== 1. Adicionar ===
  + [1] O Nome do Vento - Patrick Rothfuss (2007)
  + [2] Dom Casmurro - Machado de Assis (1899)
  + [3] Clean Code - Robert C. Martin (2008)

=== 2. Buscar por ID ===
  id 2 -> [2] Dom Casmurro - Machado de Assis (1899)
  id 99 -> nao encontrado

=== 3. Listar todos ===
  total: 3

=== 4. Atualizar ===
  atualizou o id 3? true
  agora: [3] Clean Code (2a edicao) - Robert C. Martin (2009)
  atualizar id 99? false

=== 5. Excluir ===
  excluiu o id 1? true
  excluir id 1 de novo? false
  restam: 2

=== ID duplicado e recusado ===
  Recusado: Ja existe livro com o ID 2
```

### O ponto do exercício

Na classe de teste, a variável é declarada como **`LivroDAO dao = new LivroDAOImpl();`** —
do tipo da interface, não da implementação. Se a biblioteca trocar a lista por JDBC, basta
criar um `LivroDAOJdbc implements LivroDAO`: a classe de teste e qualquer outro cliente
continuam iguais, sem uma linha alterada.

É a mesma ideia que já aparece no `academia-api` deste repositório, onde `AlunoService`
depende da interface `AlunoRepository` e não de uma implementação concreta.

### Casos de borda cobertos

Além do caminho feliz, a classe de teste mostra o comportamento quando a operação não faz
sentido: buscar ID inexistente devolve vazio, atualizar ID inexistente devolve `false`,
excluir duas vezes devolve `false` na segunda, e adicionar ID repetido é recusado.
