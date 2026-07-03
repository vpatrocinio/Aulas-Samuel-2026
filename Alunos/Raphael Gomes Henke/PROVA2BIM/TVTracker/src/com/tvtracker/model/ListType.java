package com.tvtracker.model;
//////////////////////////////
// TIPOS DE LISTA DO USUÁRIO
// Define as 3 categorias:
// Favoritos, Assistidas e Quero Assistir
//////////////////////////////
/** Os três tipos de lista que o usuário pode manter. */
public enum ListType {
    FAVORITES("Favoritos"),
    WATCHED("Já Assistidas"),
    WANT_TO_WATCH("Quero Assistir");

    private final String label;

    ListType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
