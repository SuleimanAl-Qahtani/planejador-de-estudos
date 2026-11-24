# Planejador de Estudos (A3 — Algoritmo e Programação)

Sistema computacional em **Java** voltado para a organização de atividades acadêmicas, permitindo cadastrar disciplinas personalizadas por cores, agendar tarefas de estudo e avaliações, e visualizar o cronograma em um **calendário semanal e mensal** ordenado cronologicamente.

## Estrutura de Pacotes (`src/`)

- `model/` — Entidades de domínio e Programação Orientada a Objetos (`Disciplina`, `AtividadeAcademica`, `TarefaEstudo`, `Avaliacao` e Enums).
- `dao/` — Contratos (`Interfaces`) e persistência de dados em banco de dados / memória.
- `service/` — Regras de negócio, validações de conflito de horário e estruturas de dados do calendário (Matriz `6x7`, Vetor semanal `[7]`, `ArrayList` e `HashMap`).
- `view/` — Interface interativa colorida no terminal (preparada para expansão em interface gráfica).

## Banco de Dados (SQLite)

A persistência usa **SQLite**: os dados ficam no arquivo `planejador_estudos.db`, criado automaticamente na pasta do projeto, sem precisar instalar servidor.

- `schema_banco.sql` — script de criação das tabelas `disciplinas` e `atividades` (relação 1:N pela chave estrangeira `disciplina_id`).
- `dao/ConexaoSQLite` — abre a conexão e cria as tabelas na primeira execução (`ConexaoSQLite.inicializarBanco()`).
- `dao/SqlDisciplinaDAO` e `dao/SqlAtividadeDAO` — implementam `DisciplinaDAO` e `AtividadeDAO` com SQL (`INSERT`, `SELECT ... JOIN`, `UPDATE`, `DELETE`). Tarefas e avaliações ficam na mesma tabela, diferenciadas pela coluna `tipo_atividade`.

Para usar o banco no sistema, basta trocar os DAOs em memória pelos de SQL:

```java
ConexaoSQLite.inicializarBanco();
GerenciadorEstudos gerenciador = new GerenciadorEstudos(new SqlDisciplinaDAO(), new SqlAtividadeDAO());
```

### Como testar
1. Baixe o driver do SQLite para a pasta `lib/` (veja `lib/LEIA-ME.md`).
2. Compile e rode o teste:
   - Windows: `javac -d out -cp "lib/*" -sourcepath src src/teste/TesteBanco.java` e depois `java -cp "out;lib/*" teste.TesteBanco`
   - Mac/Linux: igual, trocando `;` por `:`.
3. O resultado esperado é **"18 passaram, 0 falharam"**.
