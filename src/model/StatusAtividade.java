package model;

// Indica se a atividade ainda está pendente ou se já foi concluída
public enum StatusAtividade {
    PENDENTE("Pendente"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
