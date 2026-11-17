package model;

// Define as cores disponíveis para diferenciar as disciplinas no calendário
public enum CorDisciplina {
    AZUL("Azul", "\u001B[34m", "#2563EB"),
    VERDE("Verde", "\u001B[32m", "#16A34A"),
    AMARELO("Amarelo", "\u001B[33m", "#D97706"),
    VERMELHO("Vermelho", "\u001B[31m", "#DC2626"),
    ROXO("Roxo", "\u001B[35m", "#9333EA"),
    CIANO("Ciano", "\u001B[36m", "#0891B2");

    public static final String ANSI_RESET = "\u001B[0m";

    private final String nomeExibicao;
    private final String codigoAnsi;
    private final String codigoHex;

    CorDisciplina(String nomeExibicao, String codigoAnsi, String codigoHex) {
        this.nomeExibicao = nomeExibicao;
        this.codigoAnsi = codigoAnsi;
        this.codigoHex = codigoHex;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public String getCodigoAnsi() {
        return codigoAnsi;
    }

    public String getCodigoHex() {
        return codigoHex;
    }

    public String colorirTexto(String texto) {
        return codigoAnsi + texto + ANSI_RESET;
    }
}
