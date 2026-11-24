package teste;

import dao.*;
import model.*;
import service.GerenciadorEstudos;
import service.RegraNegocioException;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Teste da camada de banco de dados (SQLite).
 * Usa um arquivo separado (teste_planejador.db) para não mexer no banco de verdade.
 */
public class TesteBanco {

    private static int ok = 0, falhas = 0;

    public static void main(String[] args) {
        new File("teste_planejador.db").delete();
        ConexaoSQLite.setUrl("jdbc:sqlite:teste_planejador.db");
        ConexaoSQLite.inicializarBanco();
        System.out.println("Tabelas criadas.\n");

        DisciplinaDAO disciplinaDAO = new SqlDisciplinaDAO();
        AtividadeDAO atividadeDAO = new SqlAtividadeDAO();

        // ---------- DISCIPLINAS ----------
        Disciplina alg = new Disciplina("Algoritmo e Programação", "ALP2026", "Prof. Carlos", 4.5, CorDisciplina.AZUL);
        Disciplina bd = new Disciplina("Banco de Dados", "BD2026", "Prof. Ana", 3.0, CorDisciplina.VERDE);
        disciplinaDAO.salvar(alg);
        disciplinaDAO.salvar(bd);
        verificar("Salvar disciplina gera id", alg.getId() > 0 && bd.getId() > 0);
        verificar("Listar disciplinas traz 2", disciplinaDAO.listarTodas().size() == 2);

        bd.setProfessor("Prof. Marcos");
        disciplinaDAO.atualizar(bd);
        verificar("Atualizar disciplina", "Prof. Marcos".equals(disciplinaDAO.buscarPorId(bd.getId()).getProfessor()));
        verificar("Buscar id inexistente retorna null", disciplinaDAO.buscarPorId(999) == null);

        // ---------- ATIVIDADES ----------
        TarefaEstudo tarefa = new TarefaEstudo("Estudar Matrizes", "Ler capítulo 4", alg,
                LocalDate.of(2026, 11, 18), LocalTime.of(14, 0), LocalTime.of(16, 0),
                Prioridade.ALTA, "Matrizes e ArrayList", true);
        Avaliacao prova = new Avaliacao("Apresentação A3", null, alg,
                LocalDate.of(2026, 11, 30), LocalTime.of(19, 0), LocalTime.of(20, 0),
                Prioridade.ALTA, "Projeto A3", 10.0);
        TarefaEstudo leitura = new TarefaEstudo("Revisar SQL", null, bd,
                LocalDate.of(2026, 11, 17), LocalTime.of(9, 0), LocalTime.of(10, 30),
                Prioridade.MEDIA, "JOIN e chaves estrangeiras", false);

        atividadeDAO.salvar(tarefa);
        atividadeDAO.salvar(prova);
        atividadeDAO.salvar(leitura);
        verificar("Salvar atividades gera id", tarefa.getId() > 0 && prova.getId() > 0);

        List<AtividadeAcademica> todas = atividadeDAO.listarTodas();
        verificar("Listar atividades traz 3", todas.size() == 3);
        verificar("Lista vem em ordem cronológica", todas.get(0).getTitulo().equals("Revisar SQL")
                && todas.get(2).getTitulo().equals("Apresentação A3"));

        AtividadeAcademica lida = atividadeDAO.buscarPorId(tarefa.getId());
        verificar("Tarefa volta como TarefaEstudo", lida instanceof TarefaEstudo);
        verificar("Campos da tarefa voltam certos", lida instanceof TarefaEstudo t
                && t.isRevisaoNecessaria() && t.getTopicoEstudo().equals("Matrizes e ArrayList")
                && t.getHoraInicio().equals(LocalTime.of(14, 0))
                && t.getDisciplina().getCor() == CorDisciplina.AZUL);
        verificar("Avaliação volta como Avaliacao", atividadeDAO.buscarPorId(prova.getId()) instanceof Avaliacao);

        List<AtividadeAcademica> semana = atividadeDAO.buscarPorPeriodo(
                LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 22));
        verificar("Busca por período (semana 16-22/11) traz 2", semana.size() == 2);
        verificar("Busca por disciplina (Banco de Dados) traz 1", atividadeDAO.buscarPorDisciplina(bd.getId()).size() == 1);

        tarefa.setStatus(StatusAtividade.CONCLUIDA);
        atividadeDAO.atualizar(tarefa);
        verificar("Marcar como concluída", atividadeDAO.buscarPorId(tarefa.getId()).getStatus() == StatusAtividade.CONCLUIDA);

        prova.setNotaObtida(9.5);
        atividadeDAO.atualizar(prova);
        verificar("Registrar nota da avaliação", ((Avaliacao) atividadeDAO.buscarPorId(prova.getId())).getNotaObtida() == 9.5);

        verificar("Remover atividade", atividadeDAO.removerPorId(leitura.getId())
                && atividadeDAO.buscarPorId(leitura.getId()) == null);

        // ---------- INTEGRAÇÃO COM O SISTEMA (GerenciadorEstudos do Suleiman) ----------
        GerenciadorEstudos gerenciador = new GerenciadorEstudos(disciplinaDAO, atividadeDAO);
        boolean conflitoDetectado = false;
        try {
            gerenciador.cadastrarAtividade(new TarefaEstudo("Choque de horário", null, bd,
                    LocalDate.of(2026, 11, 18), LocalTime.of(15, 0), LocalTime.of(17, 0),
                    Prioridade.BAIXA, null, false));
        } catch (RegraNegocioException e) {
            conflitoDetectado = true;
        }
        verificar("Sistema detecta conflito de horário lendo do banco", conflitoDetectado);

        // Apagar a disciplina apaga as atividades dela (ON DELETE CASCADE)
        disciplinaDAO.removerPorId(alg.getId());
        verificar("Remover disciplina apaga as atividades dela (CASCADE)", atividadeDAO.listarTodas().isEmpty());

        // Os dados continuam lá depois de "fechar o programa" (nova instância do DAO)
        verificar("Dados persistem no arquivo .db", new SqlDisciplinaDAO().buscarPorId(bd.getId()) != null);

        System.out.println("\nResultado: " + ok + " passaram, " + falhas + " falharam.");
    }

    private static void verificar(String descricao, boolean passou) {
        if (passou) ok++; else falhas++;
        System.out.println((passou ? "[OK]    " : "[FALHA] ") + descricao);
    }
}
