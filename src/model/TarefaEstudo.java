package model;

import java.time.LocalDate;
import java.time.LocalTime;

// Representa uma tarefa comum de estudo ou revisão (herda de AtividadeAcademica)
public class TarefaEstudo extends AtividadeAcademica {
    private String topicoEstudo;
    private boolean revisaoNecessaria;

    public TarefaEstudo(int id, String titulo, String descricao, Disciplina disciplina,
                        LocalDate data, LocalTime horaInicio, LocalTime horaFim,
                        Prioridade prioridade, StatusAtividade status,
                        String topicoEstudo, boolean revisaoNecessaria) {
        super(id, titulo, descricao, disciplina, data, horaInicio, horaFim, prioridade, status);
        setTopicoEstudo(topicoEstudo);
        this.revisaoNecessaria = revisaoNecessaria;
    }

    public TarefaEstudo(String titulo, String descricao, Disciplina disciplina,
                        LocalDate data, LocalTime horaInicio, LocalTime horaFim,
                        Prioridade prioridade, String topicoEstudo, boolean revisaoNecessaria) {
        this(0, titulo, descricao, disciplina, data, horaInicio, horaFim,
                prioridade, StatusAtividade.PENDENTE, topicoEstudo, revisaoNecessaria);
    }

    public String getTopicoEstudo() {
        return topicoEstudo;
    }

    public void setTopicoEstudo(String topicoEstudo) {
        this.topicoEstudo = (topicoEstudo == null || topicoEstudo.trim().isEmpty())
                ? "Estudo Geral"
                : topicoEstudo.trim();
    }

    public boolean isRevisaoNecessaria() {
        return revisaoNecessaria;
    }

    public void setRevisaoNecessaria(boolean revisaoNecessaria) {
        this.revisaoNecessaria = revisaoNecessaria;
    }

    @Override
    public String getTipoRegistro() {
        return "TAREFA";
    }

    @Override
    public String exibirDetalhesEspecificos() {
        String flagRevisao = revisaoNecessaria ? "Sim (Revisar antes da prova)" : "Não";
        return String.format("Tópico: %s | Revisão necessária: %s", topicoEstudo, flagRevisao);
    }
}
