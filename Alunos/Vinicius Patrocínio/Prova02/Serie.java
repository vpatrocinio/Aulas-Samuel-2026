import java.util.List;

public class Serie {

    private String nome;
    private String idioma;
    private List<String> generos;
    private double nota;
    private String status;
    private String estreia;
    private String termino;
    private String emissora;
    private String imagem;

    public Serie(){
    }

    public Serie(String nome, String idioma, List<String> generos, double nota,
                 String status, String estreia, String termino, String emissora, String imagem){
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.status = status;
        this.estreia = estreia;
        this.termino = termino;
        this.emissora = emissora;
        this.imagem = imagem;

    }

    public String getNome() { return nome; }
    public String getIdioma() { return idioma; }
    public List<String> getGeneros() { return generos; }
    public double getNota() { return nota; }
    public String getStatus() {
        return status;
    }
    public String getEstreia() {
        return estreia;
    }
    public String getTermino() {
        return termino;
    }
    public String getEmissora() { return emissora; }
    public String getImagem(){ return imagem; }

    @Override
    public String toString() {
        return "Nome: " + nome +
                "\nIdioma: " + idioma +
                "\nGêneros: " + generos +
                "\nNota: " + nota +
                "\nStatus: " + status +
                "\nEstreia: " + estreia +
                "\nTérmino: " + termino +
                "\nEmissora: " + emissora;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Serie outra = (Serie) obj;
        return nome.equalsIgnoreCase(outra.nome);
    }
    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }
}