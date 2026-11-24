package dao;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação de AtividadeDAO usando SQLite (JDBC).
 * TarefaEstudo e Avaliacao ficam na MESMA tabela (atividades),
 * diferenciadas pela coluna tipo_atividade (Single Table Inheritance).
 */
public class SqlAtividadeDAO implements AtividadeDAO {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // SELECT base: traz a atividade + os dados da disciplina dela (JOIN)
    private static final String SELECT_COM_DISCIPLINA =
            "SELECT a.*, d.nome AS d_nome, d.codigo AS d_codigo, d.professor AS d_professor, "
          + "d.meta_horas_semanais AS d_meta, d.cor AS d_cor "
          + "FROM atividades a INNER JOIN disciplinas d ON a.disciplina_id = d.id ";

    private static final String ORDEM_CRONOLOGICA =
            " ORDER BY a.data_atividade ASC, a.hora_inicio ASC";

    @Override
    public void salvar(AtividadeAcademica a) {
        String sql = "INSERT INTO atividades (disciplina_id, tipo_atividade, titulo, descricao, "
                   + "data_atividade, hora_inicio, hora_fim, prioridade, status, "
                   + "topico_estudo, revisao_necessaria, tipo_avaliacao, peso_nota_maxima, nota_obtida) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherCampos(ps, a);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    a.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao salvar a atividade " + a.getTitulo(), e);
        }
    }

    @Override
    public void atualizar(AtividadeAcademica a) {
        String sql = "UPDATE atividades SET disciplina_id = ?, tipo_atividade = ?, titulo = ?, "
                   + "descricao = ?, data_atividade = ?, hora_inicio = ?, hora_fim = ?, "
                   + "prioridade = ?, status = ?, topico_estudo = ?, revisao_necessaria = ?, "
                   + "tipo_avaliacao = ?, peso_nota_maxima = ?, nota_obtida = ? WHERE id = ?";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencherCampos(ps, a);
            ps.setInt(15, a.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao atualizar a atividade id " + a.getId(), e);
        }
    }

    @Override
    public boolean removerPorId(int id) {
        String sql = "DELETE FROM atividades WHERE id = ?";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao remover a atividade id " + id, e);
        }
    }

    @Override
    public AtividadeAcademica buscarPorId(int id) {
        List<AtividadeAcademica> resultado = consultar(SELECT_COM_DISCIPLINA + "WHERE a.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public List<AtividadeAcademica> listarTodas() {
        return consultar(SELECT_COM_DISCIPLINA + ORDEM_CRONOLOGICA);
    }

    @Override
    public List<AtividadeAcademica> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        // Funciona porque as datas estão salvas como texto AAAA-MM-DD,
        // e nesse formato a ordem alfabética é igual à ordem cronológica
        return consultar(SELECT_COM_DISCIPLINA + "WHERE a.data_atividade BETWEEN ? AND ?" + ORDEM_CRONOLOGICA,
                dataInicio.toString(), dataFim.toString());
    }

    @Override
    public List<AtividadeAcademica> buscarPorDisciplina(int disciplinaId) {
        return consultar(SELECT_COM_DISCIPLINA + "WHERE a.disciplina_id = ?" + ORDEM_CRONOLOGICA,
                disciplinaId);
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares (privados)
    // ------------------------------------------------------------------

    /** Preenche os 14 "?" do INSERT/UPDATE na mesma ordem das colunas. */
    private void preencherCampos(PreparedStatement ps, AtividadeAcademica a) throws SQLException {
        ps.setInt(1, a.getDisciplina().getId());
        ps.setString(2, a.getTipoRegistro());          // "TAREFA" ou "AVALIACAO" (polimorfismo)
        ps.setString(3, a.getTitulo());
        ps.setString(4, a.getDescricao());
        ps.setString(5, a.getData().toString());       // LocalDate -> "2026-11-18"
        ps.setString(6, a.getHoraInicio().format(FORMATO_HORA)); // LocalTime -> "14:00"
        ps.setString(7, a.getHoraFim().format(FORMATO_HORA));
        ps.setString(8, a.getPrioridade().name());
        ps.setString(9, a.getStatus().name());

        if (a instanceof TarefaEstudo t) {
            ps.setString(10, t.getTopicoEstudo());
            ps.setInt(11, t.isRevisaoNecessaria() ? 1 : 0);
            ps.setNull(12, Types.VARCHAR);
            ps.setNull(13, Types.REAL);
            ps.setNull(14, Types.REAL);
        } else if (a instanceof Avaliacao av) {
            ps.setNull(10, Types.VARCHAR);
            ps.setInt(11, 0);
            ps.setString(12, av.getTipoAvaliacao());
            ps.setDouble(13, av.getPesoOuNotaMaxima());
            ps.setDouble(14, av.getNotaObtida());
        }
    }

    /** Executa um SELECT com parâmetros e transforma cada linha em objeto. */
    private List<AtividadeAcademica> consultar(String sql, Object... parametros) {
        List<AtividadeAcademica> lista = new ArrayList<>();
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarAtividade(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao consultar atividades", e);
        }
    }

    /** Lê uma linha da tabela e decide se vira TarefaEstudo ou Avaliacao. */
    private AtividadeAcademica montarAtividade(ResultSet rs) throws SQLException {
        Disciplina disciplina = new Disciplina(
                rs.getInt("disciplina_id"),
                rs.getString("d_nome"),
                rs.getString("d_codigo"),
                rs.getString("d_professor"),
                rs.getDouble("d_meta"),
                CorDisciplina.valueOf(rs.getString("d_cor")));

        int id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String descricao = rs.getString("descricao");
        LocalDate data = LocalDate.parse(rs.getString("data_atividade"));
        LocalTime inicio = LocalTime.parse(rs.getString("hora_inicio"));
        LocalTime fim = LocalTime.parse(rs.getString("hora_fim"));
        Prioridade prioridade = Prioridade.valueOf(rs.getString("prioridade"));
        StatusAtividade status = StatusAtividade.valueOf(rs.getString("status"));

        if ("TAREFA".equals(rs.getString("tipo_atividade"))) {
            return new TarefaEstudo(id, titulo, descricao, disciplina, data, inicio, fim,
                    prioridade, status,
                    rs.getString("topico_estudo"),
                    rs.getInt("revisao_necessaria") == 1);
        }
        return new Avaliacao(id, titulo, descricao, disciplina, data, inicio, fim,
                prioridade, status,
                rs.getString("tipo_avaliacao"),
                rs.getDouble("peso_nota_maxima"),
                rs.getDouble("nota_obtida"));
    }
}
