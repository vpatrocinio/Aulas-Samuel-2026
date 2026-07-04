import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nome;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> desejoAssistir;

    public Usuario(){
    }

    public Usuario(String nome){
        this.nome = nome;
        favoritos = new ArrayList<>();
        assistidas = new ArrayList<>();
        desejoAssistir = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public List<Serie> getFavoritos() {
        return favoritos;
    }
    public List<Serie> getAssistidas() {
        return assistidas;
    }
    public List<Serie> getDesejoAssistir() {
        return desejoAssistir;
    }

    public boolean adicionarFavoritos(Serie serie){
        if(!favoritos.contains(serie)){
            favoritos.add(serie);
            return true;
        }
        return false;
    }

    public void removerFavoritos(Serie serie){
        favoritos.remove(serie);
    }

    public void adicionarAssistidas(Serie serie){
        if(!assistidas.contains(serie)){
            assistidas.add(serie);
        }
    }

    public void removerAssistidas(Serie serie){
        assistidas.remove(serie);
    }

    public void adicionarDesejoAssistir(Serie serie){
        if(!desejoAssistir.contains(serie)){
            desejoAssistir.add(serie);
        }
    }

    public void removerDesejoAssistir(Serie serie){
        desejoAssistir.remove(serie);
    }
}