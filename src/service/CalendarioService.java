package service;

import model.AtividadeAcademica;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

// Organiza as atividades nas estruturas de dados de Vetor [7] (Semanal) e Matriz [6][7] (Mensal)
public class CalendarioService {
    private final GerenciadorEstudos gerenciador;

    public CalendarioService(GerenciadorEstudos gerenciador) {
        if (gerenciador == null) {
            throw new IllegalArgumentException("O gerenciador de estudos não pode ser nulo.");
        }
        this.gerenciador = gerenciador;
    }

    public LocalDate obterInicioDaSemana(LocalDate dataReferencia) {
        return dataReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public LocalDate obterFimDaSemana(LocalDate dataReferencia) {
        return obterInicioDaSemana(dataReferencia).plusDays(6);
    }

    @SuppressWarnings("unchecked")
    public List<AtividadeAcademica>[] montarCalendarioSemanal(LocalDate dataReferencia) {
        List<AtividadeAcademica>[] gradeSemanal = new ArrayList[7];
        for (int i = 0; i < 7; i++) {
            gradeSemanal[i] = new ArrayList<>();
        }

        LocalDate segunda = obterInicioDaSemana(dataReferencia);
        LocalDate domingo = segunda.plusDays(6);

        List<AtividadeAcademica> atividadesDaSemana = gerenciador.buscarAtividadesPorPeriodo(segunda, domingo);
        for (AtividadeAcademica atividade : atividadesDaSemana) {
            int indiceDia = atividade.getData().getDayOfWeek().getValue() - 1;
            gradeSemanal[indiceDia].add(atividade);
        }

        return gradeSemanal;
    }

    public int[][] montarMatrizDiasDoMes(int ano, int mes) {
        int[][] matrizDias = new int[6][7];
        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate primeiroDia = anoMes.atDay(1);
        int totalDias = anoMes.lengthOfMonth();

        int colunaInicial = primeiroDia.getDayOfWeek().getValue() - 1;
        int diaAtual = 1;

        for (int semana = 0; semana < 6; semana++) {
            for (int diaSemana = 0; diaSemana < 7; diaSemana++) {
                if (semana == 0 && diaSemana < colunaInicial) {
                    matrizDias[semana][diaSemana] = 0;
                } else if (diaAtual <= totalDias) {
                    matrizDias[semana][diaSemana] = diaAtual++;
                } else {
                    matrizDias[semana][diaSemana] = 0;
                }
            }
        }
        return matrizDias;
    }

    @SuppressWarnings("unchecked")
    public List<AtividadeAcademica>[][] montarCalendarioMensal(int ano, int mes) {
        List<AtividadeAcademica>[][] gradeMensal = new ArrayList[6][7];
        for (int semana = 0; semana < 6; semana++) {
            for (int diaSemana = 0; diaSemana < 7; diaSemana++) {
                gradeMensal[semana][diaSemana] = new ArrayList<>();
            }
        }

        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate inicioMes = anoMes.atDay(1);
        LocalDate fimMes = anoMes.atEndOfMonth();
        int colunaInicial = inicioMes.getDayOfWeek().getValue() - 1;

        List<AtividadeAcademica> atividadesDoMes = gerenciador.buscarAtividadesPorPeriodo(inicioMes, fimMes);
        for (AtividadeAcademica atividade : atividadesDoMes) {
            int diaDoMes = atividade.getData().getDayOfMonth();
            int posicaoLinear = colunaInicial + (diaDoMes - 1);
            int linhaSemana = posicaoLinear / 7;
            int colunaDia = posicaoLinear % 7;
            gradeMensal[linhaSemana][colunaDia].add(atividade);
        }

        return gradeMensal;
    }
}
