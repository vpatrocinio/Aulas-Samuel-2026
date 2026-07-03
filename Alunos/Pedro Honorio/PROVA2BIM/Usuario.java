import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {
    private String nome;
    private List<Serie> favoritas = new ArrayList<>();
    private List<Serie> assistidas = new ArrayList<>();
    private List<Serie> desejaAssistir = new ArrayList<>();


    public Usuario() {}

    public Usuario(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Serie> getFavoritas() { return favoritas; }
    public void setFavoritas(List<Serie> favoritas) { this.favoritas = favoritas; }

    public List<Serie> getAssistidas() { return assistidas; }
    public void setAssistidas(List<Serie> assistidas) { this.assistidas = assistidas; }

    public List<Serie> getDesejaAssistir() { return desejaAssistir; }
    public void setDesejaAssistir(List<Serie> desejaAssistir) { this.desejaAssistir = desejaAssistir; }

    public void adicionarFavorita(Serie serie) { if (!favoritas.contains(serie)) favoritas.add(serie); }
    public void adicionarAssistida(Serie serie) { if (!assistidas.contains(serie)) assistidas.add(serie); }
    public void adicionarDesejaAssistir(Serie serie) { if (!desejaAssistir.contains(serie)) desejaAssistir.add(serie); }

    public void removerFavorita(Serie serie) { favoritas.remove(serie); }
    public void removerAssistida(Serie serie) { assistidas.remove(serie); }
    public void removerDesejaAssistir(Serie serie) { desejaAssistir.remove(serie); }
}
