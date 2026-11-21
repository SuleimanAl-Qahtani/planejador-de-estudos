package view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Utilitário para leitura segura de dados no terminal com tratamento de erros de digitação
public class LeitorConsole {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String VERMELHO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final Scanner scanner;

    public LeitorConsole() {
        this.scanner = new Scanner(System.in);
    }

    public String lerTextoObrigatorio(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            exibirErro("Este campo é obrigatório e não pode ficar em branco.");
        }
    }

    public String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    public int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                exibirErro("Digite um número inteiro entre " + minimo + " e " + maximo + ".");
            } catch (NumberFormatException e) {
                exibirErro("Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    public double lerDecimalPositivo(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(entrada);
                if (valor > 0) {
                    return valor;
                }
                exibirErro("O valor deve ser maior que zero.");
            } catch (NumberFormatException e) {
                exibirErro("Entrada inválida! Digite um número válido (ex: 4.5).");
            }
        }
    }

    public double lerDecimalNoIntervalo(String mensagem, double minimo, double maximo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                exibirErro(String.format("Digite um valor entre %.1f e %.1f.", minimo, maximo));
            } catch (NumberFormatException e) {
                exibirErro("Entrada inválida! Digite um valor numérico.");
            }
        }
    }

    public boolean lerConfirmacao(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (S/N): ");
            String entrada = scanner.nextLine().trim().toUpperCase();
            if (entrada.equals("S") || entrada.equals("SIM")) {
                return true;
            }
            if (entrada.equals("N") || entrada.equals("NAO") || entrada.equals("NÃO")) {
                return false;
            }
            exibirErro("Responda com 'S' para Sim ou 'N' para Não.");
        }
    }

    public LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (dd/MM/yyyy): ");
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada, FORMATO_DATA);
            } catch (DateTimeParseException e) {
                exibirErro("Data inválida! Use o formato dia/mês/ano (ex: 18/11/2026).");
            }
        }
    }

    public LocalTime lerHorario(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (HH:mm): ");
            String entrada = scanner.nextLine().trim();
            try {
                return LocalTime.parse(entrada, FORMATO_HORA);
            } catch (DateTimeParseException e) {
                exibirErro("Horário inválido! Use o formato 24h hora:minuto (ex: 14:30).");
            }
        }
    }

    public void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public void exibirErro(String mensagem) {
        System.out.println(VERMELHO + "[ERRO] " + mensagem + RESET);
    }
}
