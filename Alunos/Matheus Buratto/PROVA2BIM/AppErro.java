// Exceção principal do sistema; qualquer falha previsível vira essa exceção com mensagem amigável.

public class AppErro extends Exception {

    public AppErro(String mensagem) {
        super(mensagem);
    }

    public AppErro(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
