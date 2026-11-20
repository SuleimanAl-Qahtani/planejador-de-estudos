package dao;

import model.AtividadeAcademica;
import java.time.LocalDate;
import java.util.List;

// Interface que define as operações de armazenamento e busca para as atividades acadêmicas
public interface AtividadeDAO {
    void salvar(AtividadeAcademica atividade);

    void atualizar(AtividadeAcademica atividade);

    boolean removerPorId(int id);

    AtividadeAcademica buscarPorId(int id);

    List<AtividadeAcademica> listarTodas();

    List<AtividadeAcademica> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    List<AtividadeAcademica> buscarPorDisciplina(int disciplinaId);
}
