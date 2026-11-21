package view;

import model.AtividadeAcademica;
import model.CorDisciplina;
import model.Disciplina;
import model.StatusAtividade;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

// Responsável por desenhar no terminal o calendário semanal, a grade mensal 6x7 e as estatísticas coloridas
public class RenderizadorCalendario {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] NOMES_DIAS = {
            "Segunda-feira", "Terça-feira", "Quarta-feira",
            "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"
    };
    private static final String[] NOMES_MESES = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    private static final String NEGRITO = "\u001B[1m";
    private static final String RESET = "\u001B[0m";

    public void exibirLegendaDisciplinas(List<Disciplina> disciplinas) {
        if (disciplinas.isEmpty()) {
            return;
        }
        System.out.println("\n" + NEGRITO + "Legenda de Cores por Disciplina:" + RESET);
        for (Disciplina d : disciplinas) {
            String blocoCor = d.getCor().colorirTexto("██ [" + d.getCodigo() + "] " + d.getNome());
            System.out.print(blocoCor + "   ");
        }
        System.out.println("\n");
    }

    public void renderizarCalendarioSemanal(LocalDate segundaFeira,
                                            List<AtividadeAcademica>[] gradeSemanal,
                                            List<Disciplina> disciplinas) {
        LocalDate domingo = segundaFeira.plusDays(6);
        System.out.println("\n================================================================================");
        System.out.printf("   %sCALENDÁRIO SEMANAL: %s a %s%s%n",
                NEGRITO, segundaFeira.format(FORMATO_DATA), domingo.format(FORMATO_DATA), RESET);
        System.out.println("================================================================================");

        exibirLegendaDisciplinas(disciplinas);

        for (int i = 0; i < 7; i++) {
            LocalDate dataDia = segundaFeira.plusDays(i);
            List<AtividadeAcademica> atividadesDia = gradeSemanal[i];

            System.out.printf("%s► %s (%s)%s%n",
                    NEGRITO, NOMES_DIAS[i], dataDia.format(FORMATO_DATA), RESET);

            if (atividadesDia.isEmpty()) {
                System.out.println("   (Nenhuma atividade agendada)");
            } else {
                for (AtividadeAcademica a : atividadesDia) {
                    imprimirLinhaAtividadeColorida(a, false);
                }
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
    }

    public void renderizarCalendarioMensal(int ano, int mes,
                                           int[][] matrizDias,
                                           List<AtividadeAcademica>[][] gradeMensal,
                                           List<Disciplina> disciplinas) {
        YearMonth anoMes = YearMonth.of(ano, mes);
        String nomeMes = NOMES_MESES[mes - 1].toUpperCase();

        System.out.println("\n================================================================================");
        System.out.printf("   %sCALENDÁRIO MENSAL — %s / %d%s%n", NEGRITO, nomeMes, ano, RESET);
        System.out.println("================================================================================");

        exibirLegendaDisciplinas(disciplinas);

        System.out.println("+----------+----------+----------+----------+----------+----------+----------+");
        System.out.println("|   SEG    |   TER    |   QUA    |   QUI    |   SEX    |   SAB    |   DOM    |");
        System.out.println("+----------+----------+----------+----------+----------+----------+----------+");

        for (int semana = 0; semana < 6; semana++) {
            boolean semanaTemDias = false;
            for (int d = 0; d < 7; d++) {
                if (matrizDias[semana][d] != 0) {
                    semanaTemDias = true;
                    break;
                }
            }
            if (!semanaTemDias) {
                continue;
            }

            System.out.print("|");
            for (int diaSemana = 0; diaSemana < 7; diaSemana++) {
                int diaNumero = matrizDias[semana][diaSemana];
                if (diaNumero == 0) {
                    System.out.print("          |");
                } else {
                    List<AtividadeAcademica> tarefasDoDia = gradeMensal[semana][diaSemana];
                    if (tarefasDoDia.isEmpty()) {
                        System.out.printf("    %02d    |", diaNumero);
                    } else {
                        String celulaTexto = String.format(" %02d (%d*)  ", diaNumero, tarefasDoDia.size());
                        CorDisciplina corDestaque = tarefasDoDia.get(0).getDisciplina().getCor();
                        System.out.print(corDestaque.colorirTexto(celulaTexto) + "|");
                    }
                }
            }
            System.out.println();
            System.out.println("+----------+----------+----------+----------+----------+----------+----------+");
        }

        System.out.println("  Legenda da Grade: 'Dia (N*)' indica a quantidade de atividades agendadas no dia.\n");
        System.out.printf("%sCronograma Detalhado de %s/%d (Ordem Cronológica):%s%n",
                NEGRITO, NOMES_MESES[mes - 1], anoMes.getYear(), RESET);

        boolean encontrouAlguma = false;
        for (int semana = 0; semana < 6; semana++) {
            for (int diaSemana = 0; diaSemana < 7; diaSemana++) {
                for (AtividadeAcademica a : gradeMensal[semana][diaSemana]) {
                    imprimirLinhaAtividadeColorida(a, true);
                    encontrouAlguma = true;
                }
            }
        }
        if (!encontrouAlguma) {
            System.out.println("   (Nenhuma atividade registrada neste mês)");
        }
    }

    public void imprimirLinhaAtividadeColorida(AtividadeAcademica a, boolean mostrarData) {
        CorDisciplina cor = a.getDisciplina().getCor();
        String prefixoData = mostrarData ? a.getData().format(FORMATO_DATA) + " | " : "";
        String horario = a.getHoraInicio().format(FORMATO_HORA) + " - " + a.getHoraFim().format(FORMATO_HORA);

        String tagStatus;
        if (a.getStatus() == StatusAtividade.CONCLUIDA) {
            tagStatus = "[CONCLUÍDA]";
        } else if (a.isAtrasada()) {
            tagStatus = "[ATRASADA!]";
        } else {
            tagStatus = "[PENDENTE]";
        }

        String cabecalho = String.format("   #%d | %s%s (%.1fh) | [%s - %s] %s | Prioridade: %s | %s",
                a.getId(),
                prefixoData,
                horario,
                a.getDuracaoHoras(),
                a.getDisciplina().getCodigo(),
                a.getTipoRegistro(),
                a.getTitulo(),
                a.getPrioridade().getDescricao(),
                tagStatus
        );

        System.out.println(cor.colorirTexto(cabecalho));
        System.out.println(cor.colorirTexto("        ↳ " + a.exibirDetalhesEspecificos()
                + (a.getDescricao().isEmpty() ? "" : " | Obs: " + a.getDescricao())));
    }

    public void renderizarPainelEstatisticas(List<Disciplina> disciplinas,
                                             Map<Disciplina, Double> horasPlanejadas,
                                             Map<Disciplina, Double> horasConcluidas,
                                             String tituloPeriodo) {
        System.out.println("\n================================================================================");
        System.out.println("   " + NEGRITO + "PAINEL DE METAS E HORAS DE ESTUDO — " + tituloPeriodo + RESET);
        System.out.println("================================================================================");

        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada.");
            return;
        }

        for (Disciplina d : disciplinas) {
            double meta = d.getMetaHorasSemanais();
            double planejado = horasPlanejadas.getOrDefault(d, 0.0);
            double concluido = horasConcluidas.getOrDefault(d, 0.0);
            double percentualMeta = (meta > 0) ? Math.min(100.0, (concluido / meta) * 100.0) : 0.0;

            String barra = gerarBarraProgresso(percentualMeta);
            String linha = String.format("[%s] %-28s | Meta: %4.1fh | Planejado: %4.1fh | Concluído: %4.1fh | %s %5.1f%%",
                    d.getCodigo(),
                    d.getNome(),
                    meta,
                    planejado,
                    concluido,
                    barra,
                    percentualMeta
            );
            System.out.println(d.getCor().colorirTexto(linha));
        }
        System.out.println("================================================================================");
    }

    private String gerarBarraProgresso(double percentual) {
        int blocosPreenchidos = (int) Math.round((percentual / 100.0) * 10);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            sb.append(i < blocosPreenchidos ? "█" : "░");
        }
        sb.append("]");
        return sb.toString();
    }
}
