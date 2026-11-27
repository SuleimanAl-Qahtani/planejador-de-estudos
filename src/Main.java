import dao.AtividadeDAO;
import dao.ConexaoSQLite;
import dao.DisciplinaDAO;
import dao.SqlAtividadeDAO;
import dao.SqlDisciplinaDAO;
import service.CalendarioService;
import service.GerenciadorEstudos;
import view.MenuPrincipalView;

// Ponto de entrada principal que inicializa o banco SQLite, conecta as camadas e abre o Planejador
public class Main {
    public static void main(String[] args) {
        ConexaoSQLite.inicializarBanco();

        DisciplinaDAO disciplinaDAO = new SqlDisciplinaDAO();
        AtividadeDAO atividadeDAO = new SqlAtividadeDAO();

        GerenciadorEstudos gerenciador = new GerenciadorEstudos(disciplinaDAO, atividadeDAO);
        CalendarioService calendarioService = new CalendarioService(gerenciador);

        MenuPrincipalView menu = new MenuPrincipalView(gerenciador, calendarioService);
        menu.iniciar();
    }
}
