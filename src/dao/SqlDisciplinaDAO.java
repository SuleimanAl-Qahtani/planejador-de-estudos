package dao;

import model.CorDisciplina;
import model.Disciplina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementação de DisciplinaDAO usando SQLite (JDBC). */
public class SqlDisciplinaDAO implements DisciplinaDAO {

    @Override
    public void salvar(Disciplina d) {
        String sql = "INSERT INTO disciplinas (nome, codigo, professor, meta_horas_semanais, cor) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNome());
            ps.setString(2, d.getCodigo());
            ps.setString(3, d.getProfessor());
            ps.setDouble(4, d.getMetaHorasSemanais());
            ps.setString(5, d.getCor().name());
            ps.executeUpdate();

            // Pega o id gerado pelo AUTOINCREMENT e coloca no objeto
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    d.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao salvar a disciplina " + d.getNome(), e);
        }
    }

    @Override
    public void atualizar(Disciplina d) {
        String sql = "UPDATE disciplinas SET nome = ?, codigo = ?, professor = ?, "
                   + "meta_horas_semanais = ?, cor = ? WHERE id = ?";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNome());
            ps.setString(2, d.getCodigo());
            ps.setString(3, d.getProfessor());
            ps.setDouble(4, d.getMetaHorasSemanais());
            ps.setString(5, d.getCor().name());
            ps.setInt(6, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao atualizar a disciplina id " + d.getId(), e);
        }
    }

    @Override
    public boolean removerPorId(int id) {
        String sql = "DELETE FROM disciplinas WHERE id = ?";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0; // true se apagou alguma linha
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao remover a disciplina id " + id, e);
        }
    }

    @Override
    public Disciplina buscarPorId(int id) {
        String sql = "SELECT * FROM disciplinas WHERE id = ?";
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarDisciplina(rs) : null;
            }
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao buscar a disciplina id " + id, e);
        }
    }

    @Override
    public List<Disciplina> listarTodas() {
        String sql = "SELECT * FROM disciplinas ORDER BY nome ASC";
        List<Disciplina> lista = new ArrayList<>();
        try (Connection con = ConexaoSQLite.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montarDisciplina(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao listar as disciplinas", e);
        }
    }

    /** Transforma a linha atual do ResultSet em um objeto Disciplina. */
    private Disciplina montarDisciplina(ResultSet rs) throws SQLException {
        return new Disciplina(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("codigo"),
                rs.getString("professor"),
                rs.getDouble("meta_horas_semanais"),
                CorDisciplina.valueOf(rs.getString("cor")));
    }
}
