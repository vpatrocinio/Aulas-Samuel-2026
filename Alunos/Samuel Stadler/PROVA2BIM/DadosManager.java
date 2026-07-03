import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class DadosManager {

    private static final String PASTA_DADOS = "dados";

    public void salvar(Usuario usuario) throws IOException {
        File pasta = new File(PASTA_DADOS);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        Map<String, Object> raiz = new LinkedHashMap<>();
        raiz.put("nome", usuario.getNome());
        raiz.put("favoritos", listaParaJson(usuario.getFavoritos()));
        raiz.put("assistidas", listaParaJson(usuario.getAssistidas()));
        raiz.put("queroAssistir", listaParaJson(usuario.getQueroAssistir()));

        String json = MiniJson.escrever(raiz);

        File arquivo = new File(pasta, nomeArquivo(usuario.getNome()));
        try (Writer escritor = new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8)) {
            escritor.write(json);
        }
    }

    @SuppressWarnings("unchecked")
    public Usuario carregar(String nomeUsuario) throws IOException {
        File arquivo = new File(PASTA_DADOS, nomeArquivo(nomeUsuario));

        if (!arquivo.exists()) {
            // usuario novo, ainda nao tem arquivo salvo
            return new Usuario(nomeUsuario);
        }

        String conteudo = new String(Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);

        if (conteudo.trim().isEmpty()) {
            return new Usuario(nomeUsuario);
        }

        Map<String, Object> raiz = (Map<String, Object>) MiniJson.parse(conteudo);

        String nomeSalvo = (String) raiz.get("nome");
        Usuario usuario = new Usuario(nomeSalvo != null ? nomeSalvo : nomeUsuario);

        usuario.setFavoritos(jsonParaLista((List<Object>) raiz.get("favoritos")));
        usuario.setAssistidas(jsonParaLista((List<Object>) raiz.get("assistidas")));
        usuario.setQueroAssistir(jsonParaLista((List<Object>) raiz.get("queroAssistir")));

        return usuario;
    }

    private String nomeArquivo(String nomeUsuario) {
        String limpo = nomeUsuario.trim().toLowerCase().replaceAll("[^a-z0-9]", "_");
        return "usuario_" + limpo + ".json";
    }

    private List<Object> listaParaJson(List<Serie> series) {
        List<Object> lista = new ArrayList<>();
        for (Serie s : series) {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("id", (double) s.getId());
            obj.put("nome", s.getNome());
            obj.put("idioma", s.getIdioma());
            obj.put("generos", new ArrayList<Object>(s.getGeneros()));
            obj.put("nota", s.getNota());
            obj.put("estado", s.getEstado());
            obj.put("dataEstreia", s.getDataEstreia());
            obj.put("dataTermino", s.getDataTermino());
            obj.put("emissora", s.getEmissora());
            lista.add(obj);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    private List<Serie> jsonParaLista(List<Object> lista) {
        List<Serie> series = new ArrayList<>();
        if (lista == null) {
            return series;
        }
        for (Object item : lista) {
            Map<String, Object> obj = (Map<String, Object>) item;
            Serie s = new Serie();

            Object idObj = obj.get("id");
            s.setId(idObj != null ? ((Double) idObj).intValue() : 0);

            s.setNome((String) obj.get("nome"));
            s.setIdioma((String) obj.get("idioma"));

            List<String> generos = new ArrayList<>();
            Object generosObj = obj.get("generos");
            if (generosObj instanceof List) {
                for (Object g : (List<Object>) generosObj) {
                    if (g != null) {
                        generos.add((String) g);
                    }
                }
            }
            s.setGeneros(generos);

            Object notaObj = obj.get("nota");
            s.setNota(notaObj != null ? (Double) notaObj : 0.0);

            s.setEstado((String) obj.get("estado"));
            s.setDataEstreia((String) obj.get("dataEstreia"));
            s.setDataTermino((String) obj.get("dataTermino"));
            s.setEmissora((String) obj.get("emissora"));

            series.add(s);
        }
        return series;
    }
}
