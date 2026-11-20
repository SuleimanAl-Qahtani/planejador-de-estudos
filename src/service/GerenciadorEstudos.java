package service;

import dao.AtividadeDAO;
import dao.DisciplinaDAO;
import model.AtividadeAcademica;
import model.Avaliacao;
import model.Disciplina;
import model.StatusAtividade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Centraliza as regras de negócio, validações de conflito, ordenação, filtros e cálculos de horas
public class GerenciadorEstudos {
    private final DisciplinaDAO disciplinaDAO;
    private final AtividadeDAO atividadeDAO;

    public GerenciadorEstudos(DisciplinaDAO disciplinaDAO, AtividadeDAO atividadeDAO) {
        if (disciplinaDAO == null || atividadeDAO == null) {
            throw new IllegalArgumentException("Os DAOs não podem ser nulos.");
        }
        this.disciplinaDAO = disciplinaDAO;
        this.atividadeDAO = atividadeDAO;
    }

    public void cadastrarDisciplina(Disciplina novaDisciplina) {
        if (novaDisciplina == null) {
            throw new RegraNegocioException("A disciplina informada é inválida.");
        }
        for (Disciplina existente : disciplinaDAO.listarTodas()) {
            if (existente.getCodigo().equalsIgnoreCase(novaDisciplina.getCodigo())) {
                throw new RegraNegocioException(
                        "Já existe uma disciplina cadastrada com o código: " + novaDisciplina.getCodigo());
            }
        }
        disciplinaDAO.salvar(novaDisciplina);
    }

    public List<Disciplina> listarDisciplinas() {
        List<Disciplina> lista = new ArrayList<>(disciplinaDAO.listarTodas());
        lista.sort(Comparator.comparing(Disciplina::getNome, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public Disciplina buscarDisciplinaPorId(int id) {
        Disciplina disciplina = disciplinaDAO.buscarPorId(id);
        if (disciplina == null) {
            throw new RegraNegocioException("Disciplina não encontrada para o ID: " + id);
        }
        return disciplina;
    }

    public boolean removerDisciplina(int disciplinaId) {
        List<AtividadeAcademica> vinculadas = atividadeDAO.buscarPorDisciplina(disciplinaId);
        for (AtividadeAcademica a : vinculadas) {
            atividadeDAO.removerPorId(a.getId());
        }
        return disciplinaDAO.removerPorId(disciplinaId);
    }

    public void cadastrarAtividade(AtividadeAcademica novaAtividade) {
        if (novaAtividade == null) {
            throw new RegraNegocioException("A atividade informada é inválida.");
        }
        validarConflitoDeHorario(novaAtividade);
        atividadeDAO.salvar(novaAtividade);
    }

    public void validarConflitoDeHorario(AtividadeAcademica atividade) {
        List<AtividadeAcademica> atividadesDoDia = atividadeDAO.buscarPorPeriodo(
                atividade.getData(), atividade.getData());

        for (AtividadeAcademica existente : atividadesDoDia) {
            if (atividade.conflitaCom(existente)) {
                throw new RegraNegocioException(String.format(
                        "Conflito de horário! A atividade '%s' (%s às %s) choca com '%s' (%s às %s) no dia %s.",
                        atividade.getTitulo(),
                        atividade.getHoraInicio(),
                        atividade.getHoraFim(),
                        existente.getTitulo(),
                        existente.getHoraInicio(),
                        existente.getHoraFim(),
                        atividade.getData()
                ));
            }
        }
    }

    public void concluirAtividade(int atividadeId) {
        AtividadeAcademica atividade = buscarAtividadePorId(atividadeId);
        atividade.setStatus(StatusAtividade.CONCLUIDA);
        atividadeDAO.atualizar(atividade);
    }

    public void registrarNotaAvaliacao(int avaliacaoId, double notaObtida) {
        AtividadeAcademica atividade = buscarAtividadePorId(avaliacaoId);
        if (!(atividade instanceof Avaliacao)) {
            throw new RegraNegocioException("A atividade selecionada não é uma Avaliação.");
        }
        Avaliacao avaliacao = (Avaliacao) atividade;
        avaliacao.setNotaObtida(notaObtida);
        avaliacao.setStatus(StatusAtividade.CONCLUIDA);
        atividadeDAO.atualizar(avaliacao);
    }

    public boolean removerAtividade(int id) {
        return atividadeDAO.removerPorId(id);
    }

    public AtividadeAcademica buscarAtividadePorId(int id) {
        AtividadeAcademica atividade = atividadeDAO.buscarPorId(id);
        if (atividade == null) {
            throw new RegraNegocioException("Atividade não encontrada para o ID: " + id);
        }
        return atividade;
    }

    public List<AtividadeAcademica> listarAtividadesOrdenadas() {
        List<AtividadeAcademica> lista = new ArrayList<>(atividadeDAO.listarTodas());
        ordenarCronologicamente(lista);
        return lista;
    }

    public List<AtividadeAcademica> buscarAtividadesPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<AtividadeAcademica> lista = new ArrayList<>(atividadeDAO.buscarPorPeriodo(inicio, fim));
        ordenarCronologicamente(lista);
        return lista;
    }

    public List<AtividadeAcademica> buscarAtividadesPorDisciplina(int disciplinaId) {
        List<AtividadeAcademica> lista = new ArrayList<>(atividadeDAO.buscarPorDisciplina(disciplinaId));
        ordenarCronologicamente(lista);
        return lista;
    }

    public List<AtividadeAcademica> listarAtividadesPendentes() {
        List<AtividadeAcademica> resultado = new ArrayList<>();
        for (AtividadeAcademica a : atividadeDAO.listarTodas()) {
            if (a.getStatus() == StatusAtividade.PENDENTE) {
                resultado.add(a);
            }
        }
        ordenarCronologicamente(resultado);
        return resultado;
    }

    public List<AtividadeAcademica> listarAtividadesAtrasadas() {
        List<AtividadeAcademica> resultado = new ArrayList<>();
        for (AtividadeAcademica a : atividadeDAO.listarTodas()) {
            if (a.isAtrasada()) {
                resultado.add(a);
            }
        }
        ordenarCronologicamente(resultado);
        return resultado;
    }

    public List<AtividadeAcademica> pesquisarPorTexto(String termo) {
        List<AtividadeAcademica> resultado = new ArrayList<>();
        if (termo == null || termo.trim().isEmpty()) {
            return resultado;
        }
        String busca = termo.trim().toLowerCase();
        for (AtividadeAcademica a : atividadeDAO.listarTodas()) {
            boolean bateTitulo = a.getTitulo().toLowerCase().contains(busca);
            boolean bateDescricao = a.getDescricao().toLowerCase().contains(busca);
            boolean bateDisciplina = a.getDisciplina().getNome().toLowerCase().contains(busca)
                    || a.getDisciplina().getCodigo().toLowerCase().contains(busca);
            if (bateTitulo || bateDescricao || bateDisciplina) {
                resultado.add(a);
            }
        }
        ordenarCronologicamente(resultado);
        return resultado;
    }

    public void ordenarCronologicamente(List<AtividadeAcademica> lista) {
        lista.sort(Comparator
                .comparing(AtividadeAcademica::getData)
                .thenComparing(AtividadeAcademica::getHoraInicio)
                .thenComparing((a1, a2) -> Integer.compare(
                        a2.getPrioridade().getNivel(),
                        a1.getPrioridade().getNivel())));
    }

    public Map<Disciplina, Double> calcularHorasPlanejadasPorDisciplina(LocalDate inicio, LocalDate fim) {
        Map<Disciplina, Double> mapaHoras = new HashMap<>();
        for (Disciplina d : disciplinaDAO.listarTodas()) {
            mapaHoras.put(d, 0.0);
        }
        List<AtividadeAcademica> atividades = (inicio != null && fim != null)
                ? atividadeDAO.buscarPorPeriodo(inicio, fim)
                : atividadeDAO.listarTodas();

        for (AtividadeAcademica a : atividades) {
            Disciplina d = a.getDisciplina();
            double acumulado = mapaHoras.getOrDefault(d, 0.0);
            mapaHoras.put(d, acumulado + a.getDuracaoHoras());
        }
        return mapaHoras;
    }

    public Map<Disciplina, Double> calcularHorasConcluidasPorDisciplina(LocalDate inicio, LocalDate fim) {
        Map<Disciplina, Double> mapaHoras = new HashMap<>();
        for (Disciplina d : disciplinaDAO.listarTodas()) {
            mapaHoras.put(d, 0.0);
        }
        List<AtividadeAcademica> atividades = (inicio != null && fim != null)
                ? atividadeDAO.buscarPorPeriodo(inicio, fim)
                : atividadeDAO.listarTodas();

        for (AtividadeAcademica a : atividades) {
            if (a.getStatus() == StatusAtividade.CONCLUIDA) {
                Disciplina d = a.getDisciplina();
                double acumulado = mapaHoras.getOrDefault(d, 0.0);
                mapaHoras.put(d, acumulado + a.getDuracaoHoras());
            }
        }
        return mapaHoras;
    }
}
