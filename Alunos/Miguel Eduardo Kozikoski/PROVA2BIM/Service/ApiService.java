package Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {
    public String buscar(String nomeSerie) {
        String url = "https://api.tvmaze.com/search/shows?q=" + nomeSerie;

        HttpClient client = HttpClient.newHttpClient(); // navegador web do java
        HttpRequest request = HttpRequest.newBuilder() // o java faz a requisição para o URL
                .uri(URI.create(url))
                .build();
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return url;

    }
}
