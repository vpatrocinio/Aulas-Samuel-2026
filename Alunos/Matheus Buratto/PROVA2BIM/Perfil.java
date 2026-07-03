import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dados do usuário: apelido + as três listas de séries (favoritas, assistidas, quero assistir).

public class Perfil {

    private String apelido;
    private final Map<CategoriaLista, List<SerieTV>> listas = new EnumMap<>(CategoriaLista.class);

    public Perfil(String apelido) {
        this.apelido = apelido == null ? "" : apelido.trim();
        for (CategoriaLista c : CategoriaLista.values()) listas.put(c, new ArrayList<>());
    }

    public String getApelido() { return apelido; }

    public void setApelido(String apelido) { this.apelido = apelido == null ? "" : apelido.trim(); }

    public List<SerieTV> getLista(CategoriaLista cat) { return new ArrayList<>(listas.get(cat)); }

    public boolean contem(CategoriaLista cat, SerieTV serie) { return listas.get(cat).contains(serie); }

    public boolean adicionar(CategoriaLista cat, SerieTV serie) {
        if (serie == null || listas.get(cat).contains(serie)) return false;
        return listas.get(cat).add(serie);
    }

    public boolean remover(CategoriaLista cat, SerieTV serie) {
        return serie != null && listas.get(cat).remove(serie);
    }

    public Map<String, Object> paraMapa() {
        Map<String, Object> raiz = new LinkedHashMap<>();
        raiz.put("apelido", apelido); // Salva o nome na máquina
        Map<String, Object> mapaListas = new LinkedHashMap<>();
        for (CategoriaLista c : CategoriaLista.values()) {
            List<Object> serializadas = new ArrayList<>();
            for (SerieTV s : listas.get(c)) serializadas.add(s.paraMapa());
            mapaListas.put(c.name(), serializadas);
        }
        raiz.put("listas", mapaListas);
        return raiz;
    }

    @SuppressWarnings("unchecked")
    public static Perfil deMapa(Map<String, Object> raiz) {
        Perfil p = new Perfil(raiz.get("apelido") == null ? "" : raiz.get("apelido").toString());
        if (raiz.get("listas") instanceof Map<?, ?> mapaListas) {
            for (CategoriaLista c : CategoriaLista.values()) {
                if (mapaListas.get(c.name()) instanceof List<?> itens) {
                    for (Object o : itens) {
                        if (o instanceof Map<?, ?> mapaSerie) {
                            p.listas.get(c).add(SerieTV.deMapa((Map<String, Object>) mapaSerie));
                        }
                    }
                }
            }
        }
        return p;
    }
}
