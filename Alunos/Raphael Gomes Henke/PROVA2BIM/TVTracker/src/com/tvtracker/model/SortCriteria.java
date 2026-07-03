package com.tvtracker.model;

/** Critérios de ordenação disponíveis para as listas de séries. */
public enum SortCriteria {
    NOME("Nome (A-Z)"),
    NOTA("Nota Geral"),
    ESTADO("Estado"),
    ESTREIA("Data de Estreia");

    private final String label;

    SortCriteria(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
