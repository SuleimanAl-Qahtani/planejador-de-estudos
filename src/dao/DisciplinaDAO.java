package dao;

import model.Disciplina;
import java.util.List;

// Interface que define as operações de armazenamento e busca para as disciplinas
public interface DisciplinaDAO {
    void salvar(Disciplina disciplina);

    void atualizar(Disciplina disciplina);

    boolean removerPorId(int id);

    Disciplina buscarPorId(int id);

    List<Disciplina> listarTodas();
}
