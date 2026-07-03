import java.util.Comparator;
import java.util.List;

public class GerenciadorSeries {
    private Usuario usuario;

    public GerenciadorSeries(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Serie> getFavoritas() { return usuario.getFavoritas(); }
    public List<Serie> getAssistidas() { return usuario.getAssistidas(); }
    public List<Serie> getDesejaAssistir() { return usuario.getDesejaAssistir(); }

    public void adicionarFavorita(Serie s) { usuario.adicionarFavorita(s); }
    public void adicionarAssistida(Serie s) { usuario.adicionarAssistida(s); }
    public void adicionarDesejaAssistir(Serie s) { usuario.adicionarDesejaAssistir(s); }

    public void removerFavorita(Serie s) { usuario.removerFavorita(s); }
    public void removerAssistida(Serie s) { usuario.removerAssistida(s); }
    public void removerDesejaAssistir(Serie s) { usuario.removerDesejaAssistir(s); }

    public void ordenarPorNome(List<Serie> lista) {
        if (lista != null) lista.sort(Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER));
    }

    public void ordenarPorNota(List<Serie> lista) {
        if (lista != null) lista.sort(Comparator.comparingDouble(Serie::getNota).reversed());
    }

    public void ordenarPorStatus(List<Serie> lista) {
        if (lista != null) lista.sort(Comparator.comparing(Serie::getStatus, String.CASE_INSENSITIVE_ORDER));
    }

    public void ordenarPorEstreia(List<Serie> lista) {
        if (lista != null) lista.sort(Comparator.comparing(Serie::getEstreia, String.CASE_INSENSITIVE_ORDER));
    }
}
