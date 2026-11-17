package model;

// Define o nível de prioridade das atividades (Baixa, Média ou Alta)
public enum Prioridade {
    BAIXA(1, "Baixa"),
    MEDIA(2, "Média"),
    ALTA(3, "Alta");

    private final int nivel;
    private final String descricao;

    Prioridade(int nivel, String descricao) {
        this.nivel = nivel;
        this.descricao = descricao;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescricao() {
        return descricao;
    }
}
