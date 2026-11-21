package view;

import model.AtividadeAcademica;
import model.Avaliacao;
import model.CorDisciplina;
import model.Disciplina;
import model.Prioridade;
import model.TarefaEstudo;
import service.CalendarioService;
import service.GerenciadorEstudos;
import service.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

// Menu interativo principal no terminal que conecta as ações do usuário aos serviços do sistema
public class MenuPrincipalView {
    private static final String NEGRITO = "\u001B[1m";
    private static final String VERDE = "\u001B[32m";
    private static final String CIANO = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private final GerenciadorEstudos gerenciador;
    private final CalendarioService calendarioService;
    private final LeitorConsole leitor;
    private final RenderizadorCalendario renderizador;

    public MenuPrincipalView(GerenciadorEstudos gerenciador, CalendarioService calendarioService) {
        this.gerenciador = gerenciador;
        this.calendarioService = calendarioService;
        this.leitor = new LeitorConsole();
        this.renderizador = new RenderizadorCalendario();
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirCabecalhoMenu();
            int opcao = leitor.lerInteiro("Escolha uma opção: ", 0, 8);

            try {
                switch (opcao) {
                    case 1:
                        menuDisciplinas();
                        break;
                    case 2:
                        menuCadastrarAtividade();
                        break;
                    case 3:
                        menuGerenciarAtividades();
                        break;
                    case 4:
                        menuCalendarioSemanal();
                        break;
                    case 5:
                        menuCalendarioMensal();
                        break;
                    case 6:
                        menuPainelEstatisticas();
                        break;
                    case 7:
                        menuPesquisarEFiltrar();
                        break;
                    case 8:
                        carregarDadosDemonstracao();
                        break;
                    case 0:
                        System.out.println("\nEncerrando o Planejador de Estudos. Bons estudos!");
                        executando = false;
                        break;
                }
            } catch (RegraNegocioException | IllegalArgumentException e) {
                leitor.exibirErro(e.getMessage());
                leitor.pausar();
            }
        }
    }

    private void exibirCabecalhoMenu() {
        System.out.println("\n" + CIANO + "╔══════════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CIANO + "║" + RESET + NEGRITO + "                   PLANEJADOR DE ESTUDOS ACADÊMICOS                   " + RESET + CIANO + "║" + RESET);
        System.out.println(CIANO + "╚══════════════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println("  1. Gerenciar Disciplinas (Cadastrar, Listar, Excluir)");
        System.out.println("  2. Agendar Nova Atividade (Tarefa de Estudo ou Avaliação)");
        System.out.println("  3. Concluir Atividade / Registrar Nota / Excluir Atividade");
        System.out.println("  4. Visualizar Calendário Semanal Colorido");
        System.out.println("  5. Visualizar Calendário Mensal Colorido (Grade 6x7)");
        System.out.println("  6. Painel de Metas e Horas de Estudo por Disciplina");
        System.out.println("  7. Pesquisar e Filtrar Atividades");
        System.out.println("  8. Carregar Dados de Demonstração (Teste Rápido)");
        System.out.println("  0. Sair");
        System.out.println("------------------------------------------------------------------------");
    }

    private void menuDisciplinas() {
        System.out.println("\n--- GERENCIAR DISCIPLINAS ---");
        System.out.println("1. Cadastrar Nova Disciplina");
        System.out.println("2. Listar Disciplinas Cadastradas");
        System.out.println("3. Excluir Disciplina");
        System.out.println("0. Voltar");

        int op = leitor.lerInteiro("Opção: ", 0, 3);
        if (op == 1) {
            String nome = leitor.lerTextoObrigatorio("Nome da disciplina: ");
            String codigo = leitor.lerTextoObrigatorio("Código/Sigla (ex: ALP2026): ");
            String professor = leitor.lerTextoOpcional("Nome do professor (opcional): ");
            double metaHoras = leitor.lerDecimalPositivo("Meta de horas de estudo por semana: ");
            CorDisciplina cor = escolherCorDisciplina();

            Disciplina nova = new Disciplina(nome, codigo, professor, metaHoras, cor);
            gerenciador.cadastrarDisciplina(nova);
            exibirSucesso("Disciplina '" + nova.getNome() + "' cadastrada com ID #" + nova.getId());
        } else if (op == 2) {
            listarDisciplinasNaTela();
        } else if (op == 3) {
            if (listarDisciplinasNaTela()) {
                int id = leitor.lerInteiro("Digite o ID da disciplina a excluir: ", 1, 99999);
                if (gerenciador.removerDisciplina(id)) {
                    exibirSucesso("Disciplina e suas atividades vinculadas foram removidas.");
                } else {
                    leitor.exibirErro("Nenhuma disciplina encontrada com o ID informado.");
                }
            }
        }
        leitor.pausar();
    }

    private CorDisciplina escolherCorDisciplina() {
        CorDisciplina[] cores = CorDisciplina.values();
        System.out.println("\nEscolha uma cor para representar esta disciplina no calendário:");
        for (int i = 0; i < cores.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, cores[i].colorirTexto("██ " + cores[i].getNomeExibicao()));
        }
        int escolha = leitor.lerInteiro("Número da cor: ", 1, cores.length);
        return cores[escolha - 1];
    }

    private boolean listarDisciplinasNaTela() {
        List<Disciplina> disciplinas = gerenciador.listarDisciplinas();
        if (disciplinas.isEmpty()) {
            System.out.println("\nNenhuma disciplina cadastrada ainda.");
            return false;
        }
        System.out.println("\nDisciplinas Cadastradas:");
        for (Disciplina d : disciplinas) {
            System.out.println("  ID #" + d.getId() + " -> " + d.getCor().colorirTexto(d.toString()));
        }
        return true;
    }

    private void menuCadastrarAtividade() {
        if (!listarDisciplinasNaTela()) {
            leitor.exibirErro("Cadastre pelo menos uma disciplina antes de agendar atividades!");
            leitor.pausar();
            return;
        }

        int disciplinaId = leitor.lerInteiro("\nID da disciplina desta atividade: ", 1, 99999);
        Disciplina disciplina = gerenciador.buscarDisciplinaPorId(disciplinaId);

        System.out.println("\nQual o tipo de atividade?");
        System.out.println("  1. Tarefa de Estudo (Leitura, Exercícios, Revisão)");
        System.out.println("  2. Avaliação (Prova, Trabalho, Entrega A3)");
        int tipo = leitor.lerInteiro("Tipo (1 ou 2): ", 1, 2);

        String titulo = leitor.lerTextoObrigatorio("Título da atividade: ");
        String descricao = leitor.lerTextoOpcional("Descrição/Observações (opcional): ");
        LocalDate data = leitor.lerData("Data da atividade");
        LocalTime horaInicio = leitor.lerHorario("Horário de início");
        LocalTime horaFim = leitor.lerHorario("Horário de término");
        Prioridade prioridade = escolherPrioridade();

        AtividadeAcademica novaAtividade;
        if (tipo == 1) {
            String topico = leitor.lerTextoObrigatorio("Tópico/Conteúdo de estudo: ");
            boolean revisao = leitor.lerConfirmacao("Marcar como revisão necessária antes da prova?");
            novaAtividade = new TarefaEstudo(
                    titulo, descricao, disciplina, data, horaInicio, horaFim, prioridade, topico, revisao);
        } else {
            String tipoAvaliacao = leitor.lerTextoObrigatorio("Tipo da avaliação (ex: Prova A1, Projeto A3): ");
            double notaMaxima = leitor.lerDecimalPositivo("Valor/Nota máxima da avaliação: ");
            novaAtividade = new Avaliacao(
                    titulo, descricao, disciplina, data, horaInicio, horaFim, prioridade, tipoAvaliacao, notaMaxima);
        }

        gerenciador.cadastrarAtividade(novaAtividade);
        exibirSucesso("Atividade '" + novaAtividade.getTitulo() + "' agendada com sucesso no calendário!");
        leitor.pausar();
    }

    private Prioridade escolherPrioridade() {
        System.out.println("Nível de Prioridade: 1. Baixa | 2. Média | 3. Alta");
        int op = leitor.lerInteiro("Escolha a prioridade (1 a 3): ", 1, 3);
        if (op == 1) return Prioridade.BAIXA;
        if (op == 2) return Prioridade.MEDIA;
        return Prioridade.ALTA;
    }

    private void menuGerenciarAtividades() {
        List<AtividadeAcademica> todas = gerenciador.listarAtividadesOrdenadas();
        if (todas.isEmpty()) {
            System.out.println("\nNenhuma atividade cadastrada.");
            leitor.pausar();
            return;
        }

        System.out.println("\n--- ATIVIDADES CADASTRADAS ---");
        for (AtividadeAcademica a : todas) {
            renderizador.imprimirLinhaAtividadeColorida(a, true);
        }

        System.out.println("\n1. Marcar Atividade como Concluída");
        System.out.println("2. Registrar Nota Obtida em Avaliação");
        System.out.println("3. Excluir Atividade");
        System.out.println("0. Voltar");

        int op = leitor.lerInteiro("Opção: ", 0, 3);
        if (op == 1) {
            int id = leitor.lerInteiro("Digite o ID (#) da atividade concluída: ", 1, 99999);
            gerenciador.concluirAtividade(id);
            exibirSucesso("Atividade #" + id + " marcada como CONCLUÍDA!");
        } else if (op == 2) {
            int id = leitor.lerInteiro("Digite o ID (#) da avaliação: ", 1, 99999);
            AtividadeAcademica a = gerenciador.buscarAtividadePorId(id);
            if (!(a instanceof Avaliacao)) {
                throw new RegraNegocioException("A atividade #" + id + " é uma Tarefa de Estudo, não uma Avaliação.");
            }
            Avaliacao av = (Avaliacao) a;
            double nota = leitor.lerDecimalNoIntervalo(
                    "Digite a nota obtida (0 a " + av.getPesoOuNotaMaxima() + "): ", 0, av.getPesoOuNotaMaxima());
            gerenciador.registrarNotaAvaliacao(id, nota);
            exibirSucesso("Nota registrada e avaliação marcada como CONCLUÍDA!");
        } else if (op == 3) {
            int id = leitor.lerInteiro("Digite o ID (#) da atividade a remover: ", 1, 99999);
            if (gerenciador.removerAtividade(id)) {
                exibirSucesso("Atividade #" + id + " removida do calendário.");
            } else {
                leitor.exibirErro("Atividade não encontrada.");
            }
        }
        leitor.pausar();
    }

    private void menuCalendarioSemanal() {
        System.out.println("\n1. Ver Semana Atual");
        System.out.println("2. Escolher uma Data Específica");
        int op = leitor.lerInteiro("Opção: ", 1, 2);

        LocalDate referencia = (op == 1) ? LocalDate.now() : leitor.lerData("Informe uma data da semana desejada");
        LocalDate segunda = calendarioService.obterInicioDaSemana(referencia);
        List<AtividadeAcademica>[] gradeSemanal = calendarioService.montarCalendarioSemanal(referencia);

        renderizador.renderizarCalendarioSemanal(segunda, gradeSemanal, gerenciador.listarDisciplinas());
        leitor.pausar();
    }

    private void menuCalendarioMensal() {
        System.out.println("\n1. Ver Mês Atual");
        System.out.println("2. Escolher Mês e Ano");
        int op = leitor.lerInteiro("Opção: ", 1, 2);

        int mes;
        int ano;
        if (op == 1) {
            LocalDate hoje = LocalDate.now();
            mes = hoje.getMonthValue();
            ano = hoje.getYear();
        } else {
            mes = leitor.lerInteiro("Digite o número do mês (1 a 12): ", 1, 12);
            ano = leitor.lerInteiro("Digite o ano (ex: 2026): ", 2000, 2100);
        }

        int[][] matrizDias = calendarioService.montarMatrizDiasDoMes(ano, mes);
        List<AtividadeAcademica>[][] gradeMensal = calendarioService.montarCalendarioMensal(ano, mes);

        renderizador.renderizarCalendarioMensal(ano, mes, matrizDias, gradeMensal, gerenciador.listarDisciplinas());
        leitor.pausar();
    }

    private void menuPainelEstatisticas() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = calendarioService.obterInicioDaSemana(hoje);
        LocalDate fimSemana = calendarioService.obterFimDaSemana(hoje);

        Map<Disciplina, Double> planejadas = gerenciador.calcularHorasPlanejadasPorDisciplina(inicioSemana, fimSemana);
        Map<Disciplina, Double> concluidas = gerenciador.calcularHorasConcluidasPorDisciplina(inicioSemana, fimSemana);

        renderizador.renderizarPainelEstatisticas(
                gerenciador.listarDisciplinas(),
                planejadas,
                concluidas,
                "Semana Atual (" + inicioSemana + " a " + fimSemana + ")"
        );
        leitor.pausar();
    }

    private void menuPesquisarEFiltrar() {
        System.out.println("\n--- PESQUISAR E FILTRAR ATIVIDADES ---");
        System.out.println("1. Listar Todas em Ordem Cronológica");
        System.out.println("2. Listar Apenas Pendentes");
        System.out.println("3. Listar Apenas Atrasadas");
        System.out.println("4. Filtrar por Disciplina");
        System.out.println("5. Pesquisar por Palavra-Chave");

        int op = leitor.lerInteiro("Opção: ", 1, 5);
        List<AtividadeAcademica> resultado;

        if (op == 1) {
            resultado = gerenciador.listarAtividadesOrdenadas();
        } else if (op == 2) {
            resultado = gerenciador.listarAtividadesPendentes();
        } else if (op == 3) {
            resultado = gerenciador.listarAtividadesAtrasadas();
        } else if (op == 4) {
            if (!listarDisciplinasNaTela()) {
                leitor.pausar();
                return;
            }
            int idDisc = leitor.lerInteiro("ID da disciplina: ", 1, 99999);
            resultado = gerenciador.buscarAtividadesPorDisciplina(idDisc);
        } else {
            String termo = leitor.lerTextoObrigatorio("Digite a palavra-chave: ");
            resultado = gerenciador.pesquisarPorTexto(termo);
        }

        System.out.println("\nResultados encontrados (" + resultado.size() + "):");
        for (AtividadeAcademica a : resultado) {
            renderizador.imprimirLinhaAtividadeColorida(a, true);
        }
        leitor.pausar();
    }

    private void carregarDadosDemonstracao() {
        if (!gerenciador.listarDisciplinas().isEmpty()) {
            System.out.println("\nJá existem disciplinas cadastradas no sistema.");
            leitor.pausar();
            return;
        }

        Disciplina alg = new Disciplina("Algoritmo e Programação", "ALP2026", "Prof. Carlos", 6.0, CorDisciplina.AZUL);
        Disciplina bd = new Disciplina("Banco de Dados", "BDD2026", "Profa. Helena", 4.0, CorDisciplina.VERDE);
        Disciplina mat = new Disciplina("Matemática Discreta", "MAT2026", "Prof. Ricardo", 5.0, CorDisciplina.ROXO);

        gerenciador.cadastrarDisciplina(alg);
        gerenciador.cadastrarDisciplina(bd);
        gerenciador.cadastrarDisciplina(mat);

        LocalDate segunda = calendarioService.obterInicioDaSemana(LocalDate.now());

        TarefaEstudo t1 = new TarefaEstudo(
                "Revisar Herança e Polimorfismo",
                "Estudar classes abstratas do projeto A3",
                alg, segunda, LocalTime.of(14, 0), LocalTime.of(16, 30),
                Prioridade.ALTA, "POO em Java", true
        );
        gerenciador.cadastrarAtividade(t1);
        gerenciador.concluirAtividade(t1.getId());

        TarefaEstudo t2 = new TarefaEstudo(
                "Praticar Consultas SQL com JOIN",
                "Testar script schema_banco.sql",
                bd, segunda.plusDays(1), LocalTime.of(19, 0), LocalTime.of(21, 0),
                Prioridade.MEDIA, "INNER JOIN e ORDER BY", false
        );
        gerenciador.cadastrarAtividade(t2);

        TarefaEstudo t3 = new TarefaEstudo(
                "Lista de Exercícios de Lógica",
                "Resolver questões 1 a 15",
                mat, segunda.plusDays(2), LocalTime.of(15, 0), LocalTime.of(17, 0),
                Prioridade.BAIXA, "Tabelas-Verdade", true
        );
        gerenciador.cadastrarAtividade(t3);

        Avaliacao av1 = new Avaliacao(
                "Apresentação Projeto A3",
                "Entrega do Planejador de Estudos e Relatório Final",
                alg, segunda.plusDays(4), LocalTime.of(18, 30), LocalTime.of(20, 30),
                Prioridade.ALTA, "Projeto A3", 10.0
        );
        gerenciador.cadastrarAtividade(av1);

        exibirSucesso("Dados de demonstração carregados! Veja agora as opções 4 (Calendário Semanal), 5 (Mensal) ou 6 (Metas).");
        leitor.pausar();
    }

    private void exibirSucesso(String mensagem) {
        System.out.println(VERDE + "\n[OK] " + mensagem + RESET);
    }
}
