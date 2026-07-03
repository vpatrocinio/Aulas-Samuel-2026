package com.tvtracker.ui;

import com.tvtracker.model.ListType;
import com.tvtracker.model.Show;
import com.tvtracker.model.UserData;
import com.tvtracker.service.StorageService;
import com.tvtracker.service.TVMazeClient;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

/**
 * TELA PRINCIPAL DO SISTEMA
 * - Busca séries na API do TVMaze
 * - Gerencia listas do usuário (Favoritos, Assistidas, Quero Assistir)
 */
public class MainFrame extends JFrame {

    private final UserData userData;
    private final StorageService storageService;
    private final TVMazeClient apiClient;

    // CAMPO DE BUSCA DE SÉRIES
    private JTextField searchField;

    // TABELA DE RESULTADOS DA BUSCA
    private ShowTableModel searchResultsModel;
    private JTable searchResultsTable;

    // LABEL DE STATUS (ex: "buscando...")
    private JLabel statusLabel;

    // PAINÉIS DAS LISTAS DO USUÁRIO
    private ShowListPanel favoritesPanel;
    private ShowListPanel watchedPanel;
    private ShowListPanel wantPanel;

    public MainFrame(UserData userData, StorageService storageService) {

        super("TV Tracker - " + userData.getUsername());

        this.userData = userData;
        this.storageService = storageService;

        // CLIENTE DA API (TVMAZE)
        this.apiClient = new TVMazeClient();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // CABEÇALHO DO USUÁRIO
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel userLabel = new JLabel("Usuário: " + userData.getUsername());
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(userLabel, BorderLayout.WEST);



        /* 
        ////////////////////////
        
        BOTÃO TROCAR USUÁRIO
    
        ////////////////////////
        */ 
        JButton switchUserButton = new JButton("Trocar de Usuário");
        switchUserButton.addActionListener(e -> switchUser());
        headerPanel.add(switchUserButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ABA PRINCIPAL (TABS)
        JTabbedPane tabbedPane = new JTabbedPane();

        // ABA DE BUSCA NA API
        tabbedPane.addTab("Buscar Séries", buildSearchPanel());

        // ABAS DAS LISTAS DO USUÁRIO
        favoritesPanel = new ShowListPanel(this, ListType.FAVORITES);
        tabbedPane.addTab("Favoritos", favoritesPanel);

        watchedPanel = new ShowListPanel(this, ListType.WATCHED);
        tabbedPane.addTab("Já Assistidas", watchedPanel);

        wantPanel = new ShowListPanel(this, ListType.WANT_TO_WATCH);
        tabbedPane.addTab("Quero Assistir", wantPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // ATUALIZA AS LISTAS AO ABRIR
        refreshAllTabs();
    }

    /**
     * CRIA A ABA DE BUSCA DE SÉRIES
     */
    private JPanel buildSearchPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));


        /*
        ////////////////////////
        
            BOTÃO DE BUSCAR

        ////////////////////////
        */
        searchField = new JTextField();
        JButton searchButton = new JButton("Buscar");

        topPanel.add(new JLabel("Nome da série:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);



        
        // TABELA DE RESULTADOS DA API
        searchResultsModel = new ShowTableModel();
        searchResultsTable = new JTable(searchResultsModel);



        /* 
        ////////////////////////////
        
        DUPLO CLIQUE ABRE DETALHES 

        ////////////////////////////
        */
        searchResultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showSelectedDetails();
            }
        });

        panel.add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);





        /* 
        ////////////////////////////////////////////
        
        PARTE INFERIOR (STATUS + BOTÃO DETALHES)
        
        ////////////////////////////////////////////
        */ 
        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel(" ");
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton detailsButton = new JButton("Ver Detalhes");
        detailsButton.addActionListener(e -> showSelectedDetails());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrap.add(detailsButton);

        bottomPanel.add(btnWrap, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);






        /* 
        ////////////////////////////////
        
        AÇÃO DE BUSCA (BOTÃO + ENTER)
        
        ////////////////////////////////
        */
        ActionListener searchAction = e -> performSearch();
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        return panel;
    }




    /**
     * BUSCA NA API DO TVMAZE (THREAD ASSÍNCRONA)
     */
    private void performSearch() {

        String query = searchField.getText();

        statusLabel.setText("Buscando...");
        searchResultsModel.setShows(new java.util.ArrayList<>());

        SwingWorker<List<Show>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<Show> doInBackground() throws Exception {
                return apiClient.searchShows(query);
            }

            @Override
            protected void done() {
                try {
                    List<Show> results = get();

                    searchResultsModel.setShows(results);

                    statusLabel.setText(results.isEmpty()
                            ? "Nenhuma série encontrada."
                            : results.size() + " série(s) encontrada(s).");

                } catch (ExecutionException ex) {

                    Throwable cause = ex.getCause();
                    String msg = cause != null ? cause.getMessage() : ex.getMessage();

                    statusLabel.setText("Erro na busca.");
                    JOptionPane.showMessageDialog(MainFrame.this, msg,
                            "Erro na busca", JOptionPane.ERROR_MESSAGE);

                } catch (Exception ex) {

                    statusLabel.setText("Erro na busca.");
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Erro inesperado: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    /**
     * ABRE DETALHES DA SÉRIE SELECIONADA
     */
    private void showSelectedDetails() {

        int row = searchResultsTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma série na lista de resultados.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Show show = searchResultsModel.getShowAt(row);

        if (show != null) {
            new ShowDetailsDialog(this, show, this).setVisible(true);
        }
    }

    public UserData getUserData() {
        return userData;
    }

    /**
     * SALVA OS DADOS DO USUÁRIO EM DISCO (JSON)
     */
    public void saveData() {
        try {
            storageService.save(userData);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar os dados: " + e.getMessage(),
                    "Erro ao salvar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ATUALIZA TODAS AS ABAS
     */
    public void refreshAllTabs() {
        if (favoritesPanel != null) favoritesPanel.refresh();
        if (watchedPanel != null) watchedPanel.refresh();
        if (wantPanel != null) wantPanel.refresh();
    }

    /**
     * TROCA DE USUÁRIO (VOLTA PARA LOGIN)
     */
    private void switchUser() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}