package dao;

import model.Disciplina;
import java.util.ArrayList;
import java.util.List;

// Implementação em memória do DisciplinaDAO para uso e testes enquanto o banco SQL é construído
public class MemoriaDisciplinaDAO implements DisciplinaDAO {
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public void salvar(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("A disciplina não pode ser nula.");
        }
        if (disciplina.getId() <= 0) {
            disciplina.setId(proximoId++);
        } else if (disciplina.getId() >= proximoId) {
            proximoId = disciplina.getId() + 1;
        }
        disciplinas.add(disciplina);
    }

    @Override
    public void atualizar(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("A disciplina não pode ser nula.");
        }
        for (int i = 0; i < disciplinas.size(); i++) {
            if (disciplinas.get(i).getId() == disciplina.getId()) {
                disciplinas.set(i, disciplina);
                return;
            }
        }
    }

    @Override
    public boolean removerPorId(int id) {
        return disciplinas.removeIf(d -> d.getId() == id);
    }

    @Override
    public Disciplina buscarPorId(int id) {
        for (Disciplina d : disciplinas) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List<Disciplina> listarTodas() {
        return new ArrayList<>(disciplinas);
    }
}
