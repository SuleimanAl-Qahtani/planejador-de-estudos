package model;

import java.time.LocalDate;
import java.time.LocalTime;

// Representa uma prova ou trabalho que vale nota (herda de AtividadeAcademica)
public class Avaliacao extends AtividadeAcademica {
    private String tipoAvaliacao;
    private double pesoOuNotaMaxima;
    private double notaObtida;

    public Avaliacao(int id, String titulo, String descricao, Disciplina disciplina,
                     LocalDate data, LocalTime horaInicio, LocalTime horaFim,
                     Prioridade prioridade, StatusAtividade status,
                     String tipoAvaliacao, double pesoOuNotaMaxima, double notaObtida) {
        super(id, titulo, descricao, disciplina, data, horaInicio, horaFim, prioridade, status);
        setTipoAvaliacao(tipoAvaliacao);
        setPesoOuNotaMaxima(pesoOuNotaMaxima);
        setNotaObtida(notaObtida);
    }

    public Avaliacao(String titulo, String descricao, Disciplina disciplina,
                     LocalDate data, LocalTime horaInicio, LocalTime horaFim,
                     Prioridade prioridade, String tipoAvaliacao, double pesoOuNotaMaxima) {
        this(0, titulo, descricao, disciplina, data, horaInicio, horaFim,
                prioridade, StatusAtividade.PENDENTE, tipoAvaliacao, pesoOuNotaMaxima, 0.0);
    }

    public String getTipoAvaliacao() {
        return tipoAvaliacao;
    }

    public void setTipoAvaliacao(String tipoAvaliacao) {
        this.tipoAvaliacao = (tipoAvaliacao == null || tipoAvaliacao.trim().isEmpty())
                ? "Prova/Trabalho"
                : tipoAvaliacao.trim();
    }

    public double getPesoOuNotaMaxima() {
        return pesoOuNotaMaxima;
    }

    public void setPesoOuNotaMaxima(double pesoOuNotaMaxima) {
        if (pesoOuNotaMaxima <= 0) {
            throw new IllegalArgumentException("O valor/peso máximo da avaliação deve ser maior que zero.");
        }
        this.pesoOuNotaMaxima = pesoOuNotaMaxima;
    }

    public double getNotaObtida() {
        return notaObtida;
    }

    public void setNotaObtida(double notaObtida) {
        if (notaObtida < 0 || (pesoOuNotaMaxima > 0 && notaObtida > pesoOuNotaMaxima)) {
            throw new IllegalArgumentException("A nota obtida deve estar entre 0 e " + pesoOuNotaMaxima);
        }
        this.notaObtida = notaObtida;
    }

    @Override
    public String getTipoRegistro() {
        return "AVALIACAO";
    }

    @Override
    public String exibirDetalhesEspecificos() {
        if (getStatus() == StatusAtividade.CONCLUIDA) {
            return String.format("Tipo: %s | Nota obtida: %.1f / %.1f", tipoAvaliacao, notaObtida, pesoOuNotaMaxima);
        }
        return String.format("Tipo: %s | Valor máximo: %.1f pts", tipoAvaliacao, pesoOuNotaMaxima);
    }
}
