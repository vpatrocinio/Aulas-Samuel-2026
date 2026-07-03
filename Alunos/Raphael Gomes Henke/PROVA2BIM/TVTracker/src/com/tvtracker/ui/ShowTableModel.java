package com.tvtracker.ui;

import com.tvtracker.model.Show;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * MODELO DA TABELA DE SÉRIES
 * - Usado na busca e nas listas do usuário
 * - Define como os dados são exibidos na tabela
 */
public class ShowTableModel extends AbstractTableModel {

    // COLUNAS DA TABELA
    private final String[] columns = {
            "Nome", "Nota", "Estado", "Estreia", "Emissora"
    };

    // LISTA DE SÉRIES EXIBIDAS
    private List<Show> shows = new ArrayList<>();

    /**
     * DEFINE OS DADOS DA TABELA
     */
    public void setShows(List<Show> shows) {
        this.shows = shows != null ? shows : new ArrayList<>();
        fireTableDataChanged(); // atualiza a tabela na interface
    }

    /**
     * RETORNA O OBJETO SHOW DA LINHA
     */
    public Show getShowAt(int row) {
        if (row < 0 || row >= shows.size()) return null;
        return shows.get(row);
    }

    @Override
    public int getRowCount() {
        return shows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    /**
     * DEFINE O QUE APARECE EM CADA CÉLULA DA TABELA
     */
    @Override
    public Object getValueAt(int row, int col) {

        Show s = shows.get(row);

        switch (col) {
            case 0: return s.getName() != null ? s.getName() : "-";
            case 1: return s.getRating() != null ? s.getRating() : "-";
            case 2: return s.getStatusPortugues();
            case 3: return s.getPremiered() != null ? s.getPremiered() : "-";
            case 4: return s.getNetwork() != null ? s.getNetwork() : "-";
            default: return "";
        }
    }

    /**
     * TABELA NÃO EDITÁVEL
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}