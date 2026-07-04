import Modelos.Usuario;
import Service.JsonService;
import Ui.TelaBuscaV2;

public class Main {
    public static void main(String[] args) {
        Usuario usuario = JsonService.carregarUsuario();

        TelaBuscaV2 tela = new TelaBuscaV2(usuario);
        tela.setVisible(true);
    }
}