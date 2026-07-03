import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Evita erros se a API mandar campos a mais
public class Serie {
    private String nome;
    private String idioma;
    private String generos;
    private double nota;
    private String status;
    private String estreia;
    private String termino;
    private String emissora;


    public Serie() {}

    public Serie(String nome, String idioma, String generos, double nota, String status,
                 String estreia, String termino, String emissora) {
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.status = status;
        this.estreia = estreia;
        this.termino = termino;
        this.emissora = emissora;
    }

    @JsonProperty("name") // Mapeia o campo "name" do JSON para "nome" no Java
    public void setNome(String nome) { this.nome = nome; }
    public String getNome() { return nome; }

    @JsonProperty("language")
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public String getIdioma() { return idioma; }

    public void setGeneros(String generos) { this.generos = generos; }
    public String getGeneros() { return generos; }

    public void setNota(double nota) { this.nota = nota; }
    public double getNota() { return nota; }

    @JsonProperty("status")
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @JsonProperty("premiered")
    public void setEstreia(String estreia) { this.estreia = estreia; }
    public String getEstreia() { return estreia; }

    @JsonProperty("ended")
    public void setTermino(String termino) { this.termino = termino; }
    public String getTermino() { return termino; }

    public void setEmissora(String emissora) { this.emissora = emissora; }
    public String getEmissora() { return emissora; }

    @Override
    public String toString() { return nome; }

    public String detalhes() {
        return "Nome: " + nome + "\nIdioma: " + idioma + "\nGêneros: " + generos +
                "\nNota: " + nota + "\nStatus: " + status + "\nEstreia: " + estreia +
                "\nTérmino: " + termino + "\nEmissora: " + emissora;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Serie outra) {
            return nome != null && nome.equalsIgnoreCase(outra.getNome());
        }
        return false;
    }
}
