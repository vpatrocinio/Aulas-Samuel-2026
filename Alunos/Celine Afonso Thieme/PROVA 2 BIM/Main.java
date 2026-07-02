import controller.SistemaController;
import service.PersistenciaService.PersistenciaException;
import telas.TelaLogin;
import telas.TelaSplash;
import util.IdiomaUtil;
import util.MensagensUtil;
import util.TemaUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principal do sistema MySeries.
 * Responsavel por:
 * 1. Exibir a Splash Screen durante o carregamento
 * 2. Inicializar o controller e carregar os dados persistidos
 * 3. Abrir a tela de Login apos o carregamento
 *
 * Todo o codigo de inicializacao roda na Event Dispatch Thread (EDT)
 * para garantir seguranca com o Swing.
 */
public class Main {

    public static void main(String[] args) {
        // Executa o Swing sempre na EDT
        SwingUtilities.invokeLater(Main::iniciarAplicacao);
    }

    private static void iniciarAplicacao() {
        // Aplica aparencia do sistema operacional para melhor integracao visual
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nao foi possivel aplicar o look and feel: " + e.getMessage());
        }

        // Exibe Splash Screen enquanto carrega
        TelaSplash splash = new TelaSplash();
        splash.setVisible(true);

        SistemaController controller = new SistemaController();

        // Carrega dados em segundo plano para nao travar o splash
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws PersistenciaException {
                publish(IdiomaUtil.get("splash.carregando"));
                controller.inicializar();
                return null;
            }

            @Override
            protected void process(java.util.List<String> mensagens) {
                if (!mensagens.isEmpty()) {
                    splash.atualizarStatus(mensagens.get(mensagens.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    get(); // Propaga excecoes do doInBackground
                    // Breve pausa para o splash ser visivel
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Throwable causa = e.getCause();
                    if (causa instanceof PersistenciaException) {
                        MensagensUtil.exibirAtencao(null, IdiomaUtil.get("msg.jsonInvalido")
                                + "\n" + IdiomaUtil.get("msg.arquivoInexistente"));
                    } else {
                        MensagensUtil.exibirErro(null, IdiomaUtil.get("msg.erroInesperado"));
                    }
                    MensagensUtil.registrarErroTecnico(new Exception(e));
                } finally {
                    splash.setVisible(false);
                    splash.dispose();
                    new TelaLogin(controller).setVisible(true);
                }
            }
        };
        worker.execute();
    }
}
