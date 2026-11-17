package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Classe base abstrata com os dados comuns de qualquer atividade do calendário
public abstract class AtividadeAcademica {
    private int id;
    private String titulo;
    private String descricao;
    private Disciplina disciplina;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Prioridade prioridade;
    private StatusAtividade status;

    public AtividadeAcademica(int id, String titulo, String descricao, Disciplina disciplina,
                              LocalDate data, LocalTime horaInicio, LocalTime horaFim,
                              Prioridade prioridade, StatusAtividade status) {
        this.id = id;
        setTitulo(titulo);
        setDescricao(descricao);
        setDisciplina(disciplina);
        setData(data);
        setHorario(horaInicio, horaFim);
        setPrioridade(prioridade);
        setStatus(status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da atividade não pode ser vazio.");
        }
        this.titulo = titulo.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = (descricao == null) ? "" : descricao.trim();
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("A atividade deve estar vinculada a uma disciplina.");
        }
        this.disciplina = disciplina;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data da atividade deve ser informada.");
        }
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHorario(LocalTime horaInicio, LocalTime horaFim) {
        if (horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Os horários de início e fim devem ser informados.");
        }
        if (!horaFim.isAfter(horaInicio)) {
            throw new IllegalArgumentException("O horário de término deve ser posterior ao horário de início.");
        }
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        if (prioridade == null) {
            throw new IllegalArgumentException("A prioridade deve ser informada.");
        }
        this.prioridade = prioridade;
    }

    public StatusAtividade getStatus() {
        return status;
    }

    public void setStatus(StatusAtividade status) {
        if (status == null) {
            throw new IllegalArgumentException("O status deve ser informado.");
        }
        this.status = status;
    }

    public long getDuracaoMinutos() {
        return Duration.between(horaInicio, horaFim).toMinutes();
    }

    public double getDuracaoHoras() {
        return getDuracaoMinutos() / 60.0;
    }

    public boolean isAtrasada() {
        if (status == StatusAtividade.CONCLUIDA) {
            return false;
        }
        LocalDateTime fimDaAtividade = LocalDateTime.of(data, horaFim);
        return fimDaAtividade.isBefore(LocalDateTime.now());
    }

    public boolean conflitaCom(AtividadeAcademica outra) {
        if (outra == null || !this.data.equals(outra.data)) {
            return false;
        }
        if (this.id > 0 && this.id == outra.id) {
            return false;
        }
        return this.horaInicio.isBefore(outra.horaFim) && outra.horaInicio.isBefore(this.horaFim);
    }

    public abstract String getTipoRegistro();

    public abstract String exibirDetalhesEspecificos();
}
