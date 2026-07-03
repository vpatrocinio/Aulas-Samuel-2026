// (Favoritas, Assistidas, Quero assistir).

public enum CategoriaLista {

    FAVORITOS("Favoritas", "♥"),
    ASSISTIDAS("Já assistidas", "✔"),
    QUERO_ASSISTIR("Quero assistir", "☆");

    private final String rotulo;
    private final String simbolo;

    CategoriaLista(String rotulo, String simbolo) {
        this.rotulo = rotulo;
        this.simbolo = simbolo;
    }

    public String getRotulo() { return rotulo; }
    public String getSimbolo() { return simbolo; }

    @Override
    public String toString() { return rotulo; }
}
