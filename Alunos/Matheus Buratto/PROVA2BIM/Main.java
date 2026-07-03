import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Ponto de entrada; abre a janela principal e instala o tratador global de exceções.

public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, erro) -> {
            erro.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Ocorreu um erro inesperado, mas o programa continuará funcionando.\n\nDetalhe: " + erro.getMessage(),
                    "Erro inesperado", JOptionPane.ERROR_MESSAGE));
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorado) {
        }

        SwingUtilities.invokeLater(() -> {
            ArquivoPerfil arquivoPerfil = new ArquivoPerfil();
            ApiTvMaze api = new ApiTvMaze();
            JanelaPrincipal janela = new JanelaPrincipal(arquivoPerfil, api);
            janela.setVisible(true);
        });
    }
}
