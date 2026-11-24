package dao;

/**
 * Erro lançado quando alguma operação no banco falha.
 * As interfaces DAO não declaram SQLException, então "embrulhamos"
 * o erro original aqui para a camada de cima (service/view) decidir o que mostrar.
 */
public class ErroBancoDados extends RuntimeException {
    public ErroBancoDados(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
