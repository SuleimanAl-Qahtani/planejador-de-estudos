package model;

import java.util.Objects;

// Representa uma disciplina cadastrada pelo aluno com sua cor e meta semanal de estudo
public class Disciplina {
    private int id;
    private String nome;
    private String codigo;
    private String professor;
    private double metaHorasSemanais;
    private CorDisciplina cor;

    public Disciplina(int id, String nome, String codigo, String professor, double metaHorasSemanais, CorDisciplina cor) {
        this.id = id;
        setNome(nome);
        setCodigo(codigo);
        setProfessor(professor);
        setMetaHorasSemanais(metaHorasSemanais);
        setCor(cor);
    }

    public Disciplina(String nome, String codigo, String professor, double metaHorasSemanais, CorDisciplina cor) {
        this(0, nome, codigo, professor, metaHorasSemanais, cor);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da disciplina não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da disciplina não pode ser vazio.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = (professor == null || professor.trim().isEmpty()) ? "Não informado" : professor.trim();
    }

    public double getMetaHorasSemanais() {
        return metaHorasSemanais;
    }

    public void setMetaHorasSemanais(double metaHorasSemanais) {
        if (metaHorasSemanais <= 0) {
            throw new IllegalArgumentException("A meta de horas semanais deve ser maior que zero.");
        }
        this.metaHorasSemanais = metaHorasSemanais;
    }

    public CorDisciplina getCor() {
        return cor;
    }

    public void setCor(CorDisciplina cor) {
        if (cor == null) {
            throw new IllegalArgumentException("A cor da disciplina deve ser informada.");
        }
        this.cor = cor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disciplina that = (Disciplina) o;
        if (this.id > 0 && that.id > 0) {
            return this.id == that.id;
        }
        return Objects.equals(this.codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return (id > 0) ? Objects.hash(id) : Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Prof. %s | Meta: %.1fh/sem | Cor: %s)",
                codigo, nome, professor, metaHorasSemanais, cor.getNomeExibicao());
    }
}
