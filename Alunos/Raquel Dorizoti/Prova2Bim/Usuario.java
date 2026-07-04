import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;



// Esta classe representa o usuario do aplicativo e suas listas
// de series (favoritos, assistidas, quero assistir).
// Tambem contem os metodos de SERIALIZACAO (toJSON) e
// DESSERIALIZACAO (fromJSON) que convertem o objeto para JSON
// e vice-versa, permitindo salvar/carregar os dados.

public class Usuario {
    private String nome;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> desejaAssistir;

    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.desejaAssistir = new ArrayList<>();
    }

    // SERIALIZACAO: converte o objeto Usuario em JSON
    // Este metodo pega todos os dados do usuario (nome e listas)
    // e converte para um objeto JSON que pode ser salvo em arquivo.
    // Cada Serie na lista tambem eh convertida para JSON pelo
    // metodo toJSON() da classe Serie.
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("nome", nome);
        obj.put("favoritos", seriesToJSON(favoritos));      // <-- SERIALIZA lista de favoritos
        obj.put("assistidas", seriesToJSON(assistidas));    // <-- SERIALIZA lista de assistidas
        obj.put("desejaAssistir", seriesToJSON(desejaAssistir));  // <-- SERIALIZA lista de desejos
        return obj;   // Retorna o JSON completo do usuario
    }

    // DESSERIALIZACAO: converte JSON de volta para objeto Usuario
    // Este metodo estatico recebe um JSONObject (lido do arquivo)
    // e recria o objeto Usuario com todas as suas listas.
    // Cada objeto JSON de serie eh convertido de volta para
    // um objeto Serie pelo metodo fromJSON() da classe Serie.
    public static Usuario fromJSON(JSONObject obj) {
        Usuario u = new Usuario(obj.optString("nome", "Usuario"));
        u.favoritos = jsonToSeries(obj.optJSONArray("favoritos"));
        u.assistidas = jsonToSeries(obj.optJSONArray("assistidas"));
        u.desejaAssistir = jsonToSeries(obj.optJSONArray("desejaAssistir"));
        return u;
    }

    // Converte uma lista de Series em um JSONArray (SERIALIZACAO)
    private static JSONArray seriesToJSON(List<Serie> series) {
        JSONArray arr = new JSONArray();
        for (Serie s : series) arr.put(s.toJSON());   // <-- SERIALIZA cada Serie individualmente
        return arr;
    }

    // Converte um JSONArray em uma lista de Series (DESSERIALIZACAO)
    private static List<Serie> jsonToSeries(JSONArray arr) {
        List<Serie> lista = new ArrayList<>();
        if (arr == null) return lista;
        for (int i = 0; i < arr.length(); i++) {
            lista.add(Serie.fromJSON(arr.getJSONObject(i)));
        }
        return lista;
    }

    // Metodos para adicionar/remover series das listas
    public void adicionarFavorito(Serie s) {
        if (!favoritos.stream().anyMatch(f -> f.getId() == s.getId())) favoritos.add(s);
    }
    public void removerFavorito(Serie s) { favoritos.removeIf(f -> f.getId() == s.getId()); }

    public void adicionarAssistida(Serie s) {
        if (!assistidas.stream().anyMatch(a -> a.getId() == s.getId())) assistidas.add(s);
    }
    public void removerAssistida(Serie s) { assistidas.removeIf(a -> a.getId() == s.getId()); }

    public void adicionarDesejaAssistir(Serie s) {
        if (!desejaAssistir.stream().anyMatch(d -> d.getId() == s.getId())) desejaAssistir.add(s);
    }
    public void removerDesejaAssistir(Serie s) { desejaAssistir.removeIf(d -> d.getId() == s.getId()); }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Serie> getFavoritos() { return favoritos; }
    public List<Serie> getAssistidas() { return assistidas; }
    public List<Serie> getDesejaAssistir() { return desejaAssistir; }
}