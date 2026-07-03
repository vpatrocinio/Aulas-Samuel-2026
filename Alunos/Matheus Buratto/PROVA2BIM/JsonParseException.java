// Exceção interna lançada quando o texto JSON está mal formado.

public class JsonParseException extends Exception {

    public JsonParseException(String mensagem) {
        super(mensagem);
    }

    public JsonParseException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
