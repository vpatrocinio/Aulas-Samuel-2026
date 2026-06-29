package fag;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por conversar com a API da TVMaze.
// A interface não faz requisição diretamente. Ela chama esta classe.
public class TvMazeService {

    private static final String URL_BASE = "https://api.tvmaze.com/search/shows?q=";

    public List<Serie> buscarSeries(String nome) throws AppException {
        if (nome == null || nome.trim().isBlank()) {
            throw new AppException("Digite o nome de uma série para pesquisar.");
        }

        try {
            String nomeFormatado = URLEncoder.encode(nome.trim(), StandardCharsets.UTF_8);
            String urlCompleta = URL_BASE + nomeFormatado;

            // AQUI CHAMA O MÉTODO QUE FAZ A REQUISIÇÃO DA API.
            String json = fazerRequisicaoApi(urlCompleta);

            /*
             * AQUI ESTÁ O JSON DA API.
             *
             * A variável "json" recebe a resposta da TVMaze em formato JSON.
             * Depois o sistema usa a classe JsonUtil para extrair os campos:
             * nome, idioma, gêneros, nota, estado, datas e emissora.
             */
            List<String> showsJson = JsonUtil.pegarObjetosDaChave(json, "show");

            List<Serie> series = new ArrayList<>();

            for (String showJson : showsJson) {
                series.add(converterJsonParaSerie(showJson));
            }

            return series;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Erro inesperado ao buscar séries.");
        }
    }

    /*
      MÉTODO RESPONSÁVEL PELA REQUISIÇÃO DA API.
     
      Aqui o sistema usa HttpClient, cria uma requisição GET,
      envia para a TVMaze e recebe a resposta em JSON.
     
     */
    private String fazerRequisicaoApi(String urlCompleta) throws AppException {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlCompleta))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new AppException("Erro ao consultar a API. Código: " + response.statusCode());
            }

            // Aqui retorna o JSON recebido da API.
            return response.body();

        } catch (IOException e) {
            throw new AppException("Erro de conexão. Verifique sua internet.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException("A busca foi interrompida.");
        } catch (IllegalArgumentException e) {
            throw new AppException("Erro ao montar a URL da busca.");
        }
    }

    /*
     CONVERSÃO DO JSON PARA OBJETO.
     
     Aqui o JSON de uma série é transformado em um objeto Serie.
     o sistema pega dados externos e representa eles como objeto.
     */
    private Serie converterJsonParaSerie(String showJson) {
        int id = JsonUtil.pegarInt(showJson, "id");
        String nome = JsonUtil.pegarString(showJson, "name");
        String idioma = JsonUtil.pegarString(showJson, "language");
        String generos = JsonUtil.pegarArrayComoTexto(showJson, "genres");
        String estado = JsonUtil.pegarString(showJson, "status");
        String dataEstreia = JsonUtil.pegarString(showJson, "premiered");
        String dataTermino = JsonUtil.pegarString(showJson, "ended");

        String ratingJson = JsonUtil.pegarObjeto(showJson, "rating");
        double nota = JsonUtil.pegarDouble(ratingJson, "average");

        String networkJson = JsonUtil.pegarObjeto(showJson, "network");
        String emissora = JsonUtil.pegarString(networkJson, "name");

        if (emissora.equals("Não informado")) {
            String webChannelJson = JsonUtil.pegarObjeto(showJson, "webChannel");
            emissora = JsonUtil.pegarString(webChannelJson, "name");
        }

        return new Serie(
                id,
                nome,
                idioma,
                generos,
                nota,
                estado,
                dataEstreia,
                dataTermino,
                emissora
        );
    }
}