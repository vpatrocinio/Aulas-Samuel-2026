package fag;

// Exception personalizada do sistema.
// Serve para mostrar erros controlados sem deixar o programa fechar inesperadamente.
public class AppException extends Exception {

    private static final long serialVersionUID = 1L;

    public AppException(String mensagem) {
        super(mensagem);
    }
}