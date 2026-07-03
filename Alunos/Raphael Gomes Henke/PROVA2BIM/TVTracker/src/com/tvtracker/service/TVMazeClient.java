package com.tvtracker.service;

import com.tvtracker.json.JsonParser;
import com.tvtracker.model.Show;
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

/**
 * Cliente responsável por consultar a API pública do TVMaze (https://www.tvmaze.com/api).
 */
public class TVMazeClient {

    private static final String BASE_URL = "https://api.tvmaze.com";
    private final HttpClient httpClient;

    public TVMazeClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /** Busca séries pelo nome. Retorna lista vazia se nada for encontrado. */
    public List<Show> searchShows(String query) throws ApiException {

        if (query == null || query.trim().isEmpty()) {
            throw new ApiException("Digite um termo de busca.");
        }

        try {

            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            String url = BASE_URL + "/search/shows?q=" + encoded;





            /*
            ///////////////////////////////////
            
             REQUISIÇÃO HTTP PRA BUSCAR SERIES

            ///////////////////////////////////
            */
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                throw new ApiException(
                        "A API do TVMaze respondeu com erro (código "
                                + response.statusCode() + ").");
            }

            Object parsed = JsonParser.parse(response.body());

            List<Show> result = new ArrayList<>();

            if (parsed instanceof List<?> list) {

                for (Object item : list) {

                    if (item instanceof Map<?, ?> itemMap
                            && itemMap.get("show") instanceof Map<?, ?> showMap) {

                        @SuppressWarnings("unchecked")
                        Map<String, Object> castedShowMap =
                                (Map<String, Object>) showMap;

                        result.add(Show.fromApiJson(castedShowMap));
                    }
                }
            }

            return result;

        } catch (ApiException e) {

            throw e;

        } catch (IOException e) {

            throw new ApiException(
                    "Falha de conexão com a internet. Verifique sua rede e tente novamente.", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new ApiException("A busca foi interrompida.", e);

        } catch (Exception e) {

            throw new ApiException(
                    "Erro inesperado ao processar os dados da API: "
                            + e.getMessage(), e);
        }
    }
}