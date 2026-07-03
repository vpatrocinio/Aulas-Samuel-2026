package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Usuario - representa o usuario local do sistema (apenas nome/apelido,
 * sem autenticacao real) e suas listas pessoais de series.
 * Esta classe e o objeto principal persistido em JSON pelo PersistenciaService.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    private String nome;
    private String idioma; // codigo do idioma escolhido: pt_BR, en, es
    private List<Serie> favoritos = new ArrayList<>();
    private List<Serie> assistidas = new ArrayList<>();
    private List<Serie> desejoAssistir = new ArrayList<>();

    public Usuario() {
        // Construtor vazio exigido pelo Jackson
    }

    public Usuario(String nome) {
        this.nome = nome;
        this.idioma = "pt_BR";
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

    public List<Serie> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Serie> favoritos) {
        this.favoritos = favoritos != null ? favoritos : new ArrayList<>();
    }

    public List<Serie> getAssistidas() {
        return assistidas;
    }

    public void setAssistidas(List<Serie> assistidas) {
        this.assistidas = assistidas != null ? assistidas : new ArrayList<>();
    }

    public List<Serie> getDesejoAssistir() {
        return desejoAssistir;
    }

    public void setDesejoAssistir(List<Serie> desejoAssistir) {
        this.desejoAssistir = desejoAssistir != null ? desejoAssistir : new ArrayList<>();
    }

    /**
     * Adiciona uma serie aos favoritos, evitando duplicacao.
     */
    public boolean adicionarFavorito(Serie serie) {
        if (favoritos.contains(serie)) {
            return false;
        }
        favoritos.add(serie);
        return true;
    }

    /**
     * Adiciona uma serie as assistidas, evitando duplicacao.
     */
    public boolean adicionarAssistida(Serie serie) {
        if (assistidas.contains(serie)) {
            return false;
        }
        assistidas.add(serie);
        return true;
    }

    /**
     * Adiciona uma serie ao desejo de assistir, evitando duplicacao.
     */
    public boolean adicionarDesejoAssistir(Serie serie) {
        if (desejoAssistir.contains(serie)) {
            return false;
        }
        desejoAssistir.add(serie);
        return true;
    }

    public void removerFavorito(Serie serie) {
        favoritos.remove(serie);
    }

    public void removerAssistida(Serie serie) {
        assistidas.remove(serie);
    }

    public void removerDesejoAssistir(Serie serie) {
        desejoAssistir.remove(serie);
    }

    public boolean isFavorito(Serie serie) {
        return favoritos.contains(serie);
    }

    public boolean isAssistida(Serie serie) {
        return assistidas.contains(serie);
    }

    public boolean isDesejoAssistir(Serie serie) {
        return desejoAssistir.contains(serie);
    }
}
