# Planejador de Estudos (A3 — Algoritmo e Programação)

Sistema computacional em **Java** voltado para a organização de atividades acadêmicas, permitindo cadastrar disciplinas personalizadas por cores, agendar tarefas de estudo e avaliações, e visualizar o cronograma em um **calendário semanal e mensal** ordenado cronologicamente.

## Estrutura de Pacotes (`src/`)

- `model/` — Entidades de domínio e Programação Orientada a Objetos (`Disciplina`, `AtividadeAcademica`, `TarefaEstudo`, `Avaliacao` e Enums).
- `dao/` — Contratos (`Interfaces`) e persistência de dados em banco de dados / memória.
- `service/` — Regras de negócio, validações de conflito de horário e estruturas de dados do calendário (Matriz `6x7`, Vetor semanal `[7]`, `ArrayList` e `HashMap`).
- `view/` — Interface interativa colorida no terminal (preparada para expansão em interface gráfica).
