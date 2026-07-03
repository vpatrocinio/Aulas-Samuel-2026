package com.tvtracker;

import com.tvtracker.ui.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * PONTO DE ENTRADA DO SISTEMA (MAIN)
 * - Inicia a aplicação Swing
 * - Define tratamento global de erros
 * - Abre a tela de Login
 */
public class Main {

    public static void main(String[] args) {

        // TRATAMENTO GLOBAL DE ERROS (EVITA FECHAR O PROGRAMA)
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Ocorreu um erro inesperado:\n" + throwable.getMessage(),
                    "Erro Inesperado",
                    JOptionPane.ERROR_MESSAGE);
        });

        // INICIALIZA A INTERFACE GRÁFICA NA THREAD CORRETA (SWING)
        SwingUtilities.invokeLater(() -> {

            try {
                // USA O VISUAL DO SISTEMA OPERACIONAL
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // SE FALHAR, USA O PADRÃO DO SWING
            }

            try {
                // ABRE A TELA DE LOGIN (INÍCIO DO SISTEMA)
                new LoginFrame().setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar a aplicação: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}