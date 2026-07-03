package com.tvtracker.model;

import java.util.Comparator;
import java.util.List;

/**
 * CLASSE RESPONSÁVEL POR ORDENAR LISTAS DE SÉRIES
 * - Usa diferentes critérios (nome, nota, estado, estreia)
 * - Aplica ordenação nas listas do usuário
 */
public final class ShowSorter {

    // impede criação de instância (classe utilitária)
    private ShowSorter() {}

    /**
     * ORDENA A LISTA DE ACORDO COM O CRITÉRIO ESCOLHIDO
     */
    public static void sort(List<Show> shows, SortCriteria criteria) {

        if (shows == null) return;

        if (criteria == null)
            criteria = SortCriteria.NOME;

        Comparator<Show> comparator;

        switch (criteria) {

            // ORDENAR POR NOTA (MAIOR → MENOR)
            case NOTA:
                comparator = Comparator.comparingDouble(
                        (Show s) -> s.getRating() == null ? -1.0 : s.getRating()
                ).reversed();
                break;

            // ORDENAR POR STATUS (ex: Running, Ended)
            case ESTADO:
                comparator = Comparator.comparing(
                        (Show s) -> s.getStatus() == null ? "" : s.getStatus()
                );
                break;

            // ORDENAR POR DATA DE ESTREIA
            case ESTREIA:
                comparator = Comparator.comparing(
                        (Show s) -> s.getPremiered() == null ? "9999-99-99" : s.getPremiered()
                );
                break;

            // ORDEM PADRÃO: NOME (A-Z)
            case NOME:
            default:
                comparator = Comparator.comparing(
                        (Show s) -> s.getName() == null ? "" : s.getName().toLowerCase()
                );
                break;
        }

        // APLICA A ORDENAÇÃO NA LISTA
        shows.sort(comparator);
    }
}