import java.util.stream.StreamSupport;

public class TVMazeAPI {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Serie buscarSerie(String nomeSerie) throws Exception {
        var nomeFormatado = URLEncoder.encode(nomeSerie, StandardCharsets.UTF_8);
        var urlApi = "https://api.tvmaze.com/singlesearch/shows?q=" + nomeFormatado;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(urlApi))
                .GET()
                .build();

        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Série não encontrada. Status: " + response.statusCode());
        }


        JsonNode raiz = mapper.readTree(response.body());


        String nome = raiz.path("name").asText("Não informado");
        String idioma = raiz.path("language").asText("Não informado");
        String status = raiz.path("status").asText("Não informado");
        String estreia = raiz.path("premiered").asText("Não informado");
        String termino = raiz.path("ended").asText("Não informado");


        var generosLista = StreamSupport
                .stream(raiz.path("genres").spliterator(), false)
                .map(JsonNode::asText)
                .toList();
        String generos = generosLista.isEmpty() ? "Não informado" : String.join(", ", generosLista);


        double nota = raiz.path("rating").path("average").asDouble(0.0);
        String emissora = raiz.path("network").path("name").asText("Não informado");

        return new Serie(nome, idioma, generos, nota, status, estreia, termino, emissora);
    }
}
