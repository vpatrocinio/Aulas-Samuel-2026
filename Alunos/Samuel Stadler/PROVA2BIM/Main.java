import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principal. So da o start na aplicacao abrindo a tela de login.
 */
public class Main {
    public static void main(String[] args) {

        // Deixa a interface com a cara do sistema operacional (mais bonito)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se nao conseguir, segue com o visual padrao do Java mesmo
        }

        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
        });
    }
}
