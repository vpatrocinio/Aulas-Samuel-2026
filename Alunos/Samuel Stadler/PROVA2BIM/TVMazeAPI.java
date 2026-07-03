import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe responsavel por conversar com a API do TVMaze
 * (https://www.tvmaze.com/api) e transformar a resposta em
 * objetos Serie que o resto do sistema entende.
 */
public class TVMazeAPI {

    private static final String URL_BASE = "https://api.tvmaze.com";

    /**
     * Busca series pelo nome usando o endpoint /search/shows da API.
     */
    @SuppressWarnings("unchecked")
    public List<Serie> buscarSeriesPorNome(String nome) throws Exception {
        String termoCodificado = URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
        String urlBusca = URL_BASE + "/search/shows?q=" + termoCodificado;

        String resposta = fazerRequisicao(urlBusca);

        Object jsonLido = MiniJson.parse(resposta);
        List<Object> listaJson = (List<Object>) jsonLido;

        List<Serie> resultado = new ArrayList<>();
        for (Object item : listaJson) {
            Map<String, Object> objItem = (Map<String, Object>) item;
            Map<String, Object> objSerie = (Map<String, Object>) objItem.get("show");
            if (objSerie != null) {
                resultado.add(converterParaSerie(objSerie));
            }
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private Serie converterParaSerie(Map<String, Object> obj) {
        Serie serie = new Serie();

        Object idObj = obj.get("id");
        serie.setId(idObj != null ? ((Double) idObj).intValue() : 0);

        serie.setNome((String) obj.get("name"));
        serie.setIdioma((String) obj.get("language"));
        serie.setEstado((String) obj.get("status"));
        serie.setDataEstreia((String) obj.get("premiered"));
        serie.setDataTermino((String) obj.get("ended"));

        // generos
        List<String> generos = new ArrayList<>();
        Object generosObj = obj.get("genres");
        if (generosObj instanceof List) {
            for (Object g : (List<Object>) generosObj) {
                if (g != null) {
                    generos.add((String) g);
                }
            }
        }
        serie.setGeneros(generos);

        // nota geral
        Object ratingObj = obj.get("rating");
        if (ratingObj instanceof Map) {
            Object media = ((Map<String, Object>) ratingObj).get("average");
            serie.setNota(media != null ? (Double) media : 0.0);
        }

        // emissora: pode vir em "network" (TV tradicional) ou "webChannel" (streaming)
        Object networkObj = obj.get("network");
        if (networkObj instanceof Map) {
            serie.setEmissora((String) ((Map<String, Object>) networkObj).get("name"));
        } else {
            Object webChannelObj = obj.get("webChannel");
            if (webChannelObj instanceof Map) {
                serie.setEmissora((String) ((Map<String, Object>) webChannelObj).get("name"));
            }
        }

        return serie;
    }

    private String fazerRequisicao(String urlTexto) throws IOException {
        URL url = new URL(urlTexto);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(8000);
        conexao.setReadTimeout(8000);

        int status = conexao.getResponseCode();
        if (status != 200) {
            throw new IOException("A API do TVMaze respondeu com erro. Codigo HTTP: " + status);
        }

        StringBuilder resposta = new StringBuilder();
        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
        }
        conexao.disconnect();

        return resposta.toString();
    }
}
