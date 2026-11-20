package service;

// Exceção personalizada para indicar violações nas regras de negócio do planejador
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
