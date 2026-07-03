import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa uma serie de TV com as informacoes
 * que o sistema precisa mostrar para o usuario.
 */
public class Serie {

    private int id;
    private String nome;
    private String idioma;
    private List<String> generos;
    private double nota;
    private String estado;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;

    public Serie() {
        this.generos = new ArrayList<>();
    }

    public Serie(int id, String nome, String idioma, List<String> generos, double nota,
                 String estado, String dataEstreia, String dataTermino, String emissora) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = (generos != null) ? generos : new ArrayList<>();
        this.nota = nota;
        this.estado = estado;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDataEstreia() {
        return dataEstreia;
    }

    public void setDataEstreia(String dataEstreia) {
        this.dataEstreia = dataEstreia;
    }

    public String getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getEmissora() {
        return emissora;
    }

    public void setEmissora(String emissora) {
        this.emissora = emissora;
    }

    // Metodo auxiliar para mostrar os generos como um texto so
    public String getGenerosTexto() {
        if (generos == null || generos.isEmpty()) {
            return "Nao informado";
        }
        return String.join(", ", generos);
    }

    // Usado para aparecer bonito dentro das JList
    @Override
    public String toString() {
        String estadoTexto = (estado == null || estado.isEmpty()) ? "?" : estado;
        return nome + "   [" + estadoTexto + "]";
    }

    // Duas series sao iguais se tiverem o mesmo id (vem da API do TVMaze)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Serie)) {
            return false;
        }
        Serie outra = (Serie) obj;
        return this.id == outra.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
