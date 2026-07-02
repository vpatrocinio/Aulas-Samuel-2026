package telas;

import util.IdiomaUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Splash Screen exibido brevemente ao iniciar o sistema, enquanto os dados
 * sao carregados em segundo plano. Proporciona uma experiencia de abertura
 * mais profissional ao usuario.
 */
public class TelaSplash extends JWindow {

    private final JLabel labelStatus;

    public TelaSplash() {
        configurarJanela();
        labelStatus = montarInterface();
        TemaUtil.centralizarJanela(this);
    }

    private void configurarJanela() {
        setSize(420, 220);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
    }

    private JLabel montarInterface() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(TemaUtil.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUtil.LINHA, 1),
                new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JLabel labelIcone = new JLabel("🎬", SwingConstants.CENTER);
        labelIcone.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        gbc.gridy = 0;
        painel.add(labelIcone, gbc);

        JLabel labelTitulo = new JLabel("MySeries", SwingConstants.CENTER);
        labelTitulo.setFont(TemaUtil.FONTE_TITULO);
        labelTitulo.setForeground(TemaUtil.TEXTO);
        gbc.gridy = 1;
        painel.add(labelTitulo, gbc);

        JLabel labelStatus = new JLabel(IdiomaUtil.get("splash.carregando"), SwingConstants.CENTER);
        labelStatus.setFont(TemaUtil.FONTE_PADRAO.deriveFont(13f));
        labelStatus.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        gbc.gridy = 2;
        painel.add(labelStatus, gbc);

        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true);
        barra.setBackground(TemaUtil.CARD);
        barra.setForeground(TemaUtil.BOTAO);
        barra.setBorderPainted(false);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        painel.add(barra, gbc);

        setContentPane(painel);
        return labelStatus;
    }

    public void atualizarStatus(String mensagem) {
        SwingUtilities.invokeLater(() -> labelStatus.setText(mensagem));
    }
}
