package fag;

public class Serie {

    private int id;
    private String nome;
    private String idioma;
    private String generos;
    private double nota;
    private String estado;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;

    public Serie(int id, String nome, String idioma, String generos, double nota,
                 String estado, String dataEstreia, String dataTermino, String emissora) {
        this.id = id;
        this.nome = tratarTexto(nome);
        this.idioma = tratarTexto(idioma);
        this.generos = tratarTexto(generos);
        this.nota = nota;
        this.estado = tratarTexto(estado);
        this.dataEstreia = tratarTexto(dataEstreia);
        this.dataTermino = tratarTexto(dataTermino);
        this.emissora = tratarTexto(emissora);
    }

    private String tratarTexto(String texto) {
        if (texto == null || texto.isBlank() || texto.equalsIgnoreCase("null")) {
            return "Não informado";
        }

        return texto;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getGeneros() {
        return generos;
    }

    public double getNota() {
        return nota;
    }

    public String getEstado() {
        return estado;
    }

    public String getDataEstreia() {
        return dataEstreia;
    }

    public String getDataTermino() {
        return dataTermino;
    }

    public String getEmissora() {
        return emissora;
    }

    public String getNotaFormatada() {
        if (nota <= 0) {
            return "Sem nota";
        }

        return String.valueOf(nota);
    }

    public String detalhes() {
        return "Nome: " + nome + "\n" +
                "Idioma: " + idioma + "\n" +
                "Gêneros: " + generos + "\n" +
                "Nota geral: " + getNotaFormatada() + "\n" +
                "Estado: " + estado + "\n" +
                "Data de estreia: " + dataEstreia + "\n" +
                "Data de término: " + dataTermino + "\n" +
                "Emissora: " + emissora;
    }

    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"nome\":\"" + escapar(nome) + "\","
                + "\"idioma\":\"" + escapar(idioma) + "\","
                + "\"generos\":\"" + escapar(generos) + "\","
                + "\"nota\":" + nota + ","
                + "\"estado\":\"" + escapar(estado) + "\","
                + "\"dataEstreia\":\"" + escapar(dataEstreia) + "\","
                + "\"dataTermino\":\"" + escapar(dataTermino) + "\","
                + "\"emissora\":\"" + escapar(emissora) + "\""
                + "}";
    }

    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return nome + " | Nota: " + getNotaFormatada() + " | Estado: " + estado;
    }
}