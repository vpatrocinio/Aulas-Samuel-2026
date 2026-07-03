import java.util.ArrayList;
import java.util.List;

/**
 * Representa o usuario local do sistema (apenas nome/apelido)
 * e guarda as listas de favoritos, assistidas e quero assistir.
 */
public class Usuario {

    private String nome;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> queroAssistir;

    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.queroAssistir = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Serie> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Serie> favoritos) {
        this.favoritos = favoritos;
    }

    public List<Serie> getAssistidas() {
        return assistidas;
    }

    public void setAssistidas(List<Serie> assistidas) {
        this.assistidas = assistidas;
    }

    public List<Serie> getQueroAssistir() {
        return queroAssistir;
    }

    public void setQueroAssistir(List<Serie> queroAssistir) {
        this.queroAssistir = queroAssistir;
    }

    // ---------- FAVORITOS ----------

    public boolean adicionarFavorito(Serie serie) {
        if (favoritos.contains(serie)) {
            return false; // ja esta na lista
        }
        favoritos.add(serie);
        return true;
    }

    public boolean removerFavorito(Serie serie) {
        return favoritos.remove(serie);
    }

    // ---------- ASSISTIDAS ----------

    public boolean adicionarAssistida(Serie serie) {
        if (assistidas.contains(serie)) {
            return false;
        }
        assistidas.add(serie);
        return true;
    }

    public boolean removerAssistida(Serie serie) {
        return assistidas.remove(serie);
    }

    // ---------- QUERO ASSISTIR ----------

    public boolean adicionarQueroAssistir(Serie serie) {
        if (queroAssistir.contains(serie)) {
            return false;
        }
        queroAssistir.add(serie);
        return true;
    }

    public boolean removerQueroAssistir(Serie serie) {
        return queroAssistir.remove(serie);
    }
}
