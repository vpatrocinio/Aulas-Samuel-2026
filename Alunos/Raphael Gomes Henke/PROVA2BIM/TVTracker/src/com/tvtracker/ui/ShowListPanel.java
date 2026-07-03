package com.tvtracker.ui;

import com.tvtracker.model.ListType;
import com.tvtracker.model.Show;
import com.tvtracker.model.ShowSorter;
import com.tvtracker.model.SortCriteria;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * PAINEL DAS LISTAS DO USUÁRIO
 * - Usado nas abas: Favoritos, Assistidas e Quero Assistir
 * - Permite ordenar, visualizar e remover séries
 */
public class ShowListPanel extends JPanel {

    // REFERÊNCIA À TELA PRINCIPAL
    private final MainFrame mainFrame;

    // TIPO DA LISTA (FAVORITOS / ASSISTIDOS / QUERO ASSISTIR)
    private final ListType listType;

    // TABELA E MODELO DOS DADOS
    private final ShowTableModel tableModel;
    private final JTable table;

    // COMBOBOX DE ORDENAÇÃO
    private final JComboBox<SortCriteria> sortCombo;

    public ShowListPanel(MainFrame mainFrame, ListType listType) {

        super(new BorderLayout(8, 8));

        this.mainFrame = mainFrame;
        this.listType = listType;

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PARTE SUPERIOR (ORDENAÇÃO)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Ordenar por:"));

        sortCombo = new JComboBox<>(SortCriteria.values());

        // SE FOR FAVORITOS, CARREGA ORDENÇÃO SALVA
        if (listType == ListType.FAVORITES) {
            sortCombo.setSelectedItem(mainFrame.getUserData().getFavoriteSort());
        }

        // QUANDO TROCA ORDENÇÃO, SALVA E ATUALIZA LISTA
        sortCombo.addActionListener(e -> {

            if (listType == ListType.FAVORITES) {
                mainFrame.getUserData().setFavoriteSort(
                        (SortCriteria) sortCombo.getSelectedItem());

                mainFrame.saveData();
            }

            refresh();
        });

        topPanel.add(sortCombo);
        add(topPanel, BorderLayout.NORTH);

        // TABELA PRINCIPAL
        tableModel = new ShowTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // DUPLO CLIQUE ABRE DETALHES
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openDetails();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // BOTÕES INFERIORES
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton detailsButton = new JButton("Ver Detalhes");
        JButton removeButton = new JButton("Remover da Lista");

        detailsButton.addActionListener(e -> openDetails());
        removeButton.addActionListener(e -> removeSelected());

        bottomPanel.add(detailsButton);
        bottomPanel.add(removeButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * ATUALIZA A LISTA NA TABELA
     */
    public void refresh() {

        List<Show> list = new ArrayList<>(
                mainFrame.getUserData().getListByType(listType));

        SortCriteria criteria;

        // SE FOR FAVORITOS USA ORDENÇÃO SALVA
        if (listType == ListType.FAVORITES) {
            criteria = mainFrame.getUserData().getFavoriteSort();
            sortCombo.setSelectedItem(criteria);
        } else {
            criteria = (SortCriteria) sortCombo.getSelectedItem();
        }

        ShowSorter.sort(list, criteria);
        tableModel.setShows(list);
    }

    /**
     * ABRE DETALHES DA SÉRIE SELECIONADA
     */
    private void openDetails() {

        int row = table.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série na lista.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        Show show = tableModel.getShowAt(row);

        if (show != null) {
            new ShowDetailsDialog(mainFrame, show, mainFrame).setVisible(true);
        }
    }

    /**
     * REMOVE SÉRIE DA LISTA ATUAL
     */
    private void removeSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série para remover.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        Show show = tableModel.getShowAt(row);

        if (show == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Remover \"" + show.getName() + "\" desta lista?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                mainFrame.getUserData()
                        .getListByType(listType)
                        .remove(show);

                mainFrame.saveData();
                mainFrame.refreshAllTabs();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao remover série: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}