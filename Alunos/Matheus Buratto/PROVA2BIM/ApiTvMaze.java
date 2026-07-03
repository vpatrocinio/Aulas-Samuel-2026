import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Monta e envia a requisição HTTP para a API TVmaze e devolve a lista de séries.

public class ApiTvMaze {

    private final String base;
    private final HttpClient http;

    public ApiTvMaze() {
        this.base = ConfigEnv.obter("TVMAZE_API_URL", "https://api.tvmaze.com");
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(12)).build();
    }

    public List<SerieTV> buscarPorNome(String termo) throws AppErro {
        if (termo == null || termo.isBlank()) {
            throw new AppErro("Digite o nome de uma série para pesquisar.");
        }
        String url = base + "/search/shows?q=" + URLEncoder.encode(termo.trim(), StandardCharsets.UTF_8);
        Object raiz = parseSeguro(requisitar(url));

        if (!(raiz instanceof List<?> lista)) {
            throw new AppErro("Resposta inesperada da API ao buscar séries.");
        }
        List<SerieTV> resultado = new ArrayList<>();
        for (Object item : lista) {
            if (item instanceof Map<?, ?> envelope && envelope.get("show") instanceof Map<?, ?> show) {
                @SuppressWarnings("unchecked")
                Map<String, Object> showMap = (Map<String, Object>) show;
                resultado.add(SerieTV.deShowApi(showMap));
            }
        }
        return resultado;
    }

    private Object parseSeguro(String corpo) throws AppErro {
        try {
            return JsonUtil.parse(corpo);
        } catch (JsonParseException e) {
            throw new AppErro("Não foi possível interpretar a resposta da API.", e);
        }
    }

    private String requisitar(String url) throws AppErro {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Accept", "application/json")
                    .header("User-Agent", "TeleTrack/1.0 (projeto educacional)")
                    .GET().build(); // objeto de requisição HTTP

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)); //Envio da requisição (síncrono)
            int status = resp.statusCode();
            if (status == 200) return resp.body();
            if (status == 404) throw new AppErro("Nenhuma série encontrada (HTTP 404).");
            if (status == 429) throw new AppErro("Muitas requisições em pouco tempo. Aguarde e tente novamente.");
            if (status >= 500) throw new AppErro("O servidor da TVmaze está indisponível (HTTP " + status + ").");
            throw new AppErro("Falha ao consultar a API (HTTP " + status + ").");

        } catch (java.net.http.HttpConnectTimeoutException e) {
            throw new AppErro("Tempo de conexão esgotado. Verifique sua internet.", e);
        } catch (java.net.UnknownHostException e) {
            throw new AppErro("Sem conexão com a internet.", e);
        } catch (IOException e) {
            throw new AppErro("Falha de conexão: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppErro("A busca foi interrompida.", e);
        }
    }
}
