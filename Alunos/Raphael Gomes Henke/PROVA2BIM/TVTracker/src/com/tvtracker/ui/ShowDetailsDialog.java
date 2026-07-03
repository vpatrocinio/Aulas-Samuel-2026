package com.tvtracker.ui;

import com.tvtracker.model.ListType;
import com.tvtracker.model.Show;
import com.tvtracker.model.UserData;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * TELA DE DETALHES DA SÉRIE
 * Mostra informações completas da série selecionada
 * e permite adicionar/remover das listas do usuário.
 */
public class ShowDetailsDialog extends JDialog {

    private final Show show;
    private final MainFrame mainFrame;




    /*
    ////////////////////////////
    
    BOTAO LISTA USUARIO

    ////////////////////////////
    */
    private JButton favButton;
    private JButton watchedButton;
    private JButton wantButton;

    // IMAGEM DA SÉRIE
    private JLabel imageLabel;

    public ShowDetailsDialog(JFrame owner, Show show, MainFrame mainFrame) {

        super(owner, "Detalhes da Série", true);

        this.show = show;
        this.mainFrame = mainFrame;

        setSize(580, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // CONTEÚDO PRINCIPAL (INFORMAÇÕES DA SÉRIE)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // NOME DA SÉRIE
        JLabel nameLabel = new JLabel(show.getName() != null ? show.getName() : "(sem nome)");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(10));

        // IMAGEM DA SÉRIE
        imageLabel = new JLabel("Carregando imagem...");
        content.add(imageLabel);
        content.add(Box.createVerticalStrut(10));

        // INFORMAÇÕES DA SÉRIE (DADOS PRINCIPAIS)
        content.add(infoLine("Idioma:", show.getLanguage()));
        content.add(infoLine("Gêneros:", show.getGenresAsString()));
        content.add(infoLine("Nota geral:", show.getRating() != null ? String.valueOf(show.getRating()) : "Sem avaliação"));
        content.add(infoLine("Estado:", show.getStatusPortugues()));
        content.add(infoLine("Data de estreia:", show.getPremiered() != null ? show.getPremiered() : "Desconhecida"));
        content.add(infoLine("Data de término:", show.getEnded() != null ? show.getEnded() : "Em aberto / não informado"));
        content.add(infoLine("Emissora:", show.getNetwork() != null ? show.getNetwork() : "Desconhecida"));

        // SINOPSE
        JLabel summaryTitle = new JLabel("Sinopse:");
        summaryTitle.setFont(summaryTitle.getFont().deriveFont(Font.BOLD));
        content.add(summaryTitle);

        JTextArea summaryArea = new JTextArea(
                show.getSummary() != null ? show.getSummary() : "Sem sinopse disponível.");
        summaryArea.setWrapStyleWord(true);
        summaryArea.setLineWrap(true);
        summaryArea.setEditable(false);

        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setPreferredSize(new Dimension(520, 140));
        content.add(summaryScroll);

        add(new JScrollPane(content), BorderLayout.CENTER);





        /*
        ////////////////////////////////////
         
        BOTOES AÇÃO

        ////////////////////////////////////
         */
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 15, 15));

        favButton = new JButton();
        watchedButton = new JButton();
        wantButton = new JButton();
        JButton closeButton = new JButton("Fechar");

        updateButtonLabels();

        favButton.addActionListener(e -> toggle(ListType.FAVORITES));
        watchedButton.addActionListener(e -> toggle(ListType.WATCHED));
        wantButton.addActionListener(e -> toggle(ListType.WANT_TO_WATCH));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(favButton);
        buttonPanel.add(watchedButton);
        buttonPanel.add(wantButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // CARREGAMENTO ASSÍNCRONO DA IMAGEM
        loadImageAsync();
    }

    /**
     * LINHA DE INFORMAÇÃO (LABEL + VALOR)
     */
    private JPanel infoLine(String label, String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        JLabel l1 = new JLabel(label + " ");
        l1.setFont(l1.getFont().deriveFont(Font.BOLD));
        JLabel l2 = new JLabel(value != null ? value : "-");
        p.add(l1);
        p.add(l2);
        return p;
    }





    /*
    //////////////////////////////////////
    
        ADICIONAR LISTA FAVORIOS ETC

    //////////////////////////////////////
    */
    private void toggle(ListType type) {
        try {
            UserData data = mainFrame.getUserData();
            java.util.List<Show> list = data.getListByType(type);

            if (list.contains(show)) {
                list.remove(show);
            } else {
                list.add(show);
            }

            mainFrame.saveData();
            mainFrame.refreshAllTabs();
            updateButtonLabels();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar lista: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ATUALIZA TEXTO DOS BOTÕES (ADICIONAR / REMOVER)
     */
    private void updateButtonLabels() {
        UserData data = mainFrame.getUserData();

        favButton.setText(data.getFavorites().contains(show)
                ? "Remover dos Favoritos"
                : "Adicionar aos Favoritos");

        watchedButton.setText(data.getWatched().contains(show)
                ? "Remover de Assistidas"
                : "Marcar como Assistida");

        wantButton.setText(data.getWantToWatch().contains(show)
                ? "Remover de Quero Assistir"
                : "Adicionar a Quero Assistir");
    }


    /*
    //////////////////////
            FOTO
    //////////////////////
    */
    /**
     * CARREGA IMAGEM DA SÉRIE EM THREAD SEPARADA
     */
    private void loadImageAsync() {

        String url = show.getImageUrl();

        if (url == null || url.isEmpty()) {
            imageLabel.setText("(sem imagem disponível)");
            return;
        }

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {

            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL imgUrl = new URL(url);
                Image img = ImageIO.read(imgUrl);

                if (img == null) {
                    throw new IOException("Imagem inválida");
                }

                Image scaled = img.getScaledInstance(140, -1, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }

            @Override
            protected void done() {
                try {
                    imageLabel.setIcon(get());
                    imageLabel.setText(null);
                } catch (Exception e) {
                    imageLabel.setText("(erro ao carregar imagem)");
                }
            }
        };

        worker.execute();
    }
}