import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class TvMaze {

    public Serie buscarSerie(String nome) {
        try {
            String endereco = "https://api.tvmaze.com/search/shows?q=" + nome;
            URI uri = URI.create(endereco);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonArray lista = gson.fromJson(response.body(), JsonArray.class);

            if (lista == null || lista.size() == 0) {
                return null;
            }

            JsonObject resultado = lista.get(0).getAsJsonObject();

            JsonObject show = resultado.getAsJsonObject("show");

            String nomeSerie = show.get("name").getAsString();
            String idioma = show.get("language").getAsString();

            List<String> listaGeneros = new ArrayList<>();
            JsonArray generos = show.getAsJsonArray("genres");
            for (int i = 0; i < generos.size(); i++) {
                listaGeneros.add(generos.get(i).getAsString());
            }

            String status = show.get("status").getAsString();

            String estreia = "";
            if (!show.get("premiered").isJsonNull()) {
                estreia = show.get("premiered").getAsString();
            }

            String termino = "";
            if (!show.get("ended").isJsonNull()){
                termino = show.get("ended").getAsString();
            }

            JsonObject rating = show.getAsJsonObject("rating");
            double nota = 0;
            if (!rating.get("average").isJsonNull()){
                nota = rating.get("average").getAsDouble();
            }

            String emissora = "";
            if (show.has("network") && !show.get("network").isJsonNull()){
                JsonObject network = show.getAsJsonObject("network");
                emissora = network.get("name").getAsString();
            }

            String imagem = "";
            if (show.has("image") && !show.get("image").isJsonNull()) {
                JsonObject image = show.getAsJsonObject("image");
                if (image.has("medium")) {
                    imagem = image.get("medium").getAsString();
                }
            }

            Serie serie = new Serie(
                    nomeSerie,
                    idioma,
                    listaGeneros,
                    nota,
                    status,
                    estreia,
                    termino,
                    emissora,
                    imagem
            );
            return serie;
        } catch(Exception e) {
            System.out.println("Erro ao conectar com a API.");
            return null;
        }
    }
}