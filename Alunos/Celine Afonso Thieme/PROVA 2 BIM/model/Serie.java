package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe Serie - representa uma serie de TV consumida da API TVMaze.
 * Contem apenas os dados necessarios para exibicao e persistencia local.
 * Esta classe e usada tanto para guardar os dados vindos da API quanto
 * para ser salva/lida do arquivo JSON local (Jackson).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Serie {

    private int id;
    private String nome;
    private String idioma;
    private List<String> generos = new ArrayList<>();
    private Double nota; // pode ser nulo quando a API nao informa
    private String status;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;
    private String resumo; // resumo ja convertido para texto simples (sem HTML)
    private String imagemUrl;

    public Serie() {
        // Construtor vazio exigido pelo Jackson para desserializacao
    }

    public Serie(int id, String nome, String idioma, List<String> generos, Double nota,
                 String status, String dataEstreia, String dataTermino, String emissora,
                 String resumo, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos != null ? generos : new ArrayList<>();
        this.nota = nota;
        this.status = status;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
        this.resumo = resumo;
        this.imagemUrl = imagemUrl;
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
        this.generos = generos != null ? generos : new ArrayList<>();
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    /**
     * Retorna a nota como texto, exibindo "N/A" quando nao informada.
     */
    public String getNotaFormatada() {
        return nota == null ? "N/A" : String.format("%.1f", nota);
    }

    /**
     * Retorna os generos concatenados em uma unica string, separados por virgula.
     */
    public String getGenerosFormatados() {
        return String.join(", ", generos);
    }

    /**
     * Duas series sao consideradas iguais quando possuem o mesmo id da API.
     * Usado para evitar duplicacao entre listas (favoritos, assistidas, desejo).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Serie)) return false;
        Serie serie = (Serie) o;
        return id == serie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
