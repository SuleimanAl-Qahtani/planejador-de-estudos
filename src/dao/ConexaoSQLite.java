package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsável por abrir conexões com o arquivo planejador_estudos.db
 * e por criar as tabelas na primeira vez que o programa roda.
 */
public class ConexaoSQLite {

    private static String url = "jdbc:sqlite:planejador_estudos.db";

    private static final String CRIAR_TABELA_DISCIPLINAS =
            "CREATE TABLE IF NOT EXISTS disciplinas ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " nome VARCHAR(100) NOT NULL,"
            + " codigo VARCHAR(30) NOT NULL UNIQUE,"
            + " professor VARCHAR(100),"
            + " meta_horas_semanais REAL NOT NULL CHECK (meta_horas_semanais > 0),"
            + " cor VARCHAR(20) NOT NULL CHECK (cor IN ('AZUL','VERDE','AMARELO','VERMELHO','ROXO','CIANO'))"
            + ")";

    private static final String CRIAR_TABELA_ATIVIDADES =
            "CREATE TABLE IF NOT EXISTS atividades ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " disciplina_id INTEGER NOT NULL,"
            + " tipo_atividade VARCHAR(20) NOT NULL CHECK (tipo_atividade IN ('TAREFA','AVALIACAO')),"
            + " titulo VARCHAR(120) NOT NULL,"
            + " descricao TEXT,"
            + " data_atividade VARCHAR(10) NOT NULL,"
            + " hora_inicio VARCHAR(5) NOT NULL,"
            + " hora_fim VARCHAR(5) NOT NULL,"
            + " prioridade VARCHAR(10) NOT NULL CHECK (prioridade IN ('BAIXA','MEDIA','ALTA')),"
            + " status VARCHAR(15) NOT NULL CHECK (status IN ('PENDENTE','CONCLUIDA')),"
            + " topico_estudo VARCHAR(150),"
            + " revisao_necessaria INTEGER DEFAULT 0 CHECK (revisao_necessaria IN (0,1)),"
            + " tipo_avaliacao VARCHAR(50),"
            + " peso_nota_maxima REAL,"
            + " nota_obtida REAL DEFAULT 0.0,"
            + " FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id) ON DELETE CASCADE"
            + ")";

    /** Permite trocar o arquivo do banco (usado nos testes). */
    public static void setUrl(String novaUrl) {
        url = novaUrl;
    }

    /** Abre uma conexão nova. Use sempre com try-with-resources para fechar sozinha. */
    public static Connection getConexao() {
        try {
            Connection conexao = DriverManager.getConnection(url);
            // No SQLite a chave estrangeira (e o ON DELETE CASCADE) vem desligada por padrão
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            return conexao;
        } catch (SQLException e) {
            throw new ErroBancoDados("Não foi possível conectar ao banco: " + url, e);
        }
    }

    /** Cria as tabelas se ainda não existirem. Chamar uma vez quando o programa inicia. */
    public static void inicializarBanco() {
        try (Connection conexao = getConexao();
             Statement st = conexao.createStatement()) {
            st.execute(CRIAR_TABELA_DISCIPLINAS);
            st.execute(CRIAR_TABELA_ATIVIDADES);
        } catch (SQLException e) {
            throw new ErroBancoDados("Erro ao criar as tabelas", e);
        }
    }
}
