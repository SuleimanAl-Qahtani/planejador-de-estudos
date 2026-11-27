package teste;

import dao.AtividadeDAO;
import dao.ConexaoSQLite;
import dao.DisciplinaDAO;
import dao.SqlAtividadeDAO;
import dao.SqlDisciplinaDAO;
import model.AtividadeAcademica;
import model.Avaliacao;
import model.CorDisciplina;
import model.Disciplina;
import model.Prioridade;
import model.StatusAtividade;
import model.TarefaEstudo;
import service.CalendarioService;
import service.GerenciadorEstudos;
import service.RegraNegocioException;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

// Executa os 3 cenários obrigatórios do Tópico 15 do relatório (Normal, Alternativo e Excepcional) com SQLite
public class TesteAutomatizadoA3 {
    public static void main(String[] args) {
        File arquivoTeste = new File("teste_cenarios_a3.db");
        arquivoTeste.delete();

        ConexaoSQLite.setUrl("jdbc:sqlite:teste_cenarios_a3.db");
        ConexaoSQLite.inicializarBanco();

        DisciplinaDAO disciplinaDAO = new SqlDisciplinaDAO();
        AtividadeDAO atividadeDAO = new SqlAtividadeDAO();
        GerenciadorEstudos gerenciador = new GerenciadorEstudos(disciplinaDAO, atividadeDAO);
        CalendarioService calendarioService = new CalendarioService(gerenciador);

        System.out.println("================================================================================");
        System.out.println("   EXECUÇÃO DOS TESTES DO RELATÓRIO FINAL (TÓPICO 15) — SQLITE + JAVA POO");
        System.out.println("================================================================================\n");

        // TESTE 1: CENÁRIO NORMAL
        System.out.println("► TESTE 1 — CENÁRIO NORMAL: Cadastro de disciplinas/atividades e geração do calendário");
        Disciplina alg = new Disciplina("Algoritmo e Programação", "ALP2026", "Prof. Carlos", 6.0, CorDisciplina.AZUL);
        Disciplina bd = new Disciplina("Banco de Dados", "BDD2026", "Profa. Helena", 4.0, CorDisciplina.VERDE);
        gerenciador.cadastrarDisciplina(alg);
        gerenciador.cadastrarDisciplina(bd);

        LocalDate dataEstudo = LocalDate.of(2026, 11, 18);
        TarefaEstudo t1 = new TarefaEstudo(
                "Estudar Matrizes e Vetores", "Revisar exemplos da aula", alg,
                dataEstudo, LocalTime.of(14, 0), LocalTime.of(16, 0),
                Prioridade.ALTA, "Matrizes 6x7", true
        );
        Avaliacao av1 = new Avaliacao(
                "Entrega Relatório A3", "Apresentação em dupla", alg,
                dataEstudo, LocalTime.of(18, 30), LocalTime.of(20, 30),
                Prioridade.ALTA, "Projeto A3", 10.0
        );
        gerenciador.cadastrarAtividade(av1);
        gerenciador.cadastrarAtividade(t1);

        List<AtividadeAcademica> ordenadas = gerenciador.listarAtividadesOrdenadas();
        List<AtividadeAcademica>[][] gradeMensal = calendarioService.montarCalendarioMensal(2026, 11);

        boolean teste1Passou = ordenadas.size() == 2
                && ordenadas.get(0).getTitulo().equals("Estudar Matrizes e Vetores")
                && gradeMensal[3][2].size() == 2;

        System.out.println("  - Entrada: 2 disciplinas e 2 atividades no dia 18/11/2026 (cadastradas fora de ordem).");
        System.out.println("  - Resultado esperado: Persistir no SQLite, ordenar cronologicamente (14:00 antes de 18:30) e alocar na Matriz 6x7.");
        System.out.println("  - Resultado obtido: " + ordenadas.size() + " atividades salvas; 1ª da fila: '"
                + ordenadas.get(0).getTitulo() + "' às " + ordenadas.get(0).getHoraInicio() + ".");
        System.out.println("  - Situação: " + (teste1Passou ? "APROVADO" : "REPROVADO") + "\n");

        // TESTE 2: CENÁRIO ALTERNATIVO
        System.out.println("► TESTE 2 — CENÁRIO ALTERNATIVO: Conclusão de tarefa, lançamento de nota e cálculo de horas (HashMap)");
        gerenciador.concluirAtividade(t1.getId());
        gerenciador.registrarNotaAvaliacao(av1.getId(), 9.5);

        Map<Disciplina, Double> horasConcluidas = gerenciador.calcularHorasConcluidasPorDisciplina(null, null);
        double totalConcluidoAlg = horasConcluidas.getOrDefault(alg, 0.0);
        Avaliacao avAtualizada = (Avaliacao) gerenciador.buscarAtividadePorId(av1.getId());

        boolean teste2Passou = totalConcluidoAlg == 4.0
                && avAtualizada.getStatus() == StatusAtividade.CONCLUIDA
                && avAtualizada.getNotaObtida() == 9.5;

        System.out.println("  - Entrada: Conclusão da tarefa #" + t1.getId() + " (2.0h) e nota 9.5 na avaliação #" + av1.getId() + " (2.0h).");
        System.out.println("  - Resultado esperado: Atualizar status no banco para CONCLUIDA, salvar nota 9.5 e somar 4.0h concluídas no HashMap.");
        System.out.println("  - Resultado obtido: Nota salva = " + avAtualizada.getNotaObtida()
                + " | Horas concluídas em ALP2026 = " + totalConcluidoAlg + "h.");
        System.out.println("  - Situação: " + (teste2Passou ? "APROVADO" : "REPROVADO") + "\n");

        // TESTE 3: CENÁRIO EXCEPCIONAL
        System.out.println("► TESTE 3 — CENÁRIO EXCEPCIONAL: Bloqueio de conflito de horário e validação de intervalo inválido");
        boolean bloqueouConflito = false;
        String msgConflito = "";
        try {
            TarefaEstudo tarefaConflitante = new TarefaEstudo(
                    "Estudo de SQL em horário ocupado", "Deve ser bloqueada", bd,
                    dataEstudo, LocalTime.of(15, 0), LocalTime.of(17, 0),
                    Prioridade.BAIXA, null, false
            );
            gerenciador.cadastrarAtividade(tarefaConflitante);
        } catch (RegraNegocioException e) {
            bloqueouConflito = true;
            msgConflito = e.getMessage();
        }

        boolean bloqueouHorarioInvertido = false;
        try {
            new TarefaEstudo(
                    "Horário invertido", "Fim antes do início", bd,
                    dataEstudo, LocalTime.of(16, 0), LocalTime.of(14, 0),
                    Prioridade.BAIXA, "Teste", false
            );
        } catch (IllegalArgumentException e) {
            bloqueouHorarioInvertido = true;
        }

        boolean teste3Passou = bloqueouConflito && bloqueouHorarioInvertido;
        System.out.println("  - Entrada: Tentativa de agendar tarefa das 15:00 às 17:00 em 18/11 (conflitando com 14:00-16:00) e tarefa das 16:00 às 14:00.");
        System.out.println("  - Resultado esperado: Lançar RegraNegocioException e IllegalArgumentException impedindo gravação inválida no banco.");
        System.out.println("  - Resultado obtido: Bloqueio acionado com mensagem -> \"" + msgConflito + "\"");
        System.out.println("  - Situação: " + (teste3Passou ? "APROVADO" : "REPROVADO") + "\n");

        arquivoTeste.delete();
        System.out.println("================================================================================");
    }
}
