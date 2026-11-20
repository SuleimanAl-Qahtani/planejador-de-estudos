package dao;

import model.AtividadeAcademica;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Implementação em memória do AtividadeDAO para armazenar e filtrar tarefas e avaliações
public class MemoriaAtividadeDAO implements AtividadeDAO {
    private final List<AtividadeAcademica> atividades = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public void salvar(AtividadeAcademica atividade) {
        if (atividade == null) {
            throw new IllegalArgumentException("A atividade não pode ser nula.");
        }
        if (atividade.getId() <= 0) {
            atividade.setId(proximoId++);
        } else if (atividade.getId() >= proximoId) {
            proximoId = atividade.getId() + 1;
        }
        atividades.add(atividade);
    }

    @Override
    public void atualizar(AtividadeAcademica atividade) {
        if (atividade == null) {
            throw new IllegalArgumentException("A atividade não pode ser nula.");
        }
        for (int i = 0; i < atividades.size(); i++) {
            if (atividades.get(i).getId() == atividade.getId()) {
                atividades.set(i, atividade);
                return;
            }
        }
    }

    @Override
    public boolean removerPorId(int id) {
        return atividades.removeIf(a -> a.getId() == id);
    }

    @Override
    public AtividadeAcademica buscarPorId(int id) {
        for (AtividadeAcademica a : atividades) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<AtividadeAcademica> listarTodas() {
        return new ArrayList<>(atividades);
    }

    @Override
    public List<AtividadeAcademica> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<AtividadeAcademica> resultado = new ArrayList<>();
        if (dataInicio == null || dataFim == null) {
            return resultado;
        }
        for (AtividadeAcademica a : atividades) {
            LocalDate data = a.getData();
            if (!data.isBefore(dataInicio) && !data.isAfter(dataFim)) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    @Override
    public List<AtividadeAcademica> buscarPorDisciplina(int disciplinaId) {
        List<AtividadeAcademica> resultado = new ArrayList<>();
        for (AtividadeAcademica a : atividades) {
            if (a.getDisciplina() != null && a.getDisciplina().getId() == disciplinaId) {
                resultado.add(a);
            }
        }
        return resultado;
    }
}
