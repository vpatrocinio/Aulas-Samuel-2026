package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Serie;
import service.TvMazeDTO.SearchResultDTO;
import service.TvMazeDTO.ShowDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsavel exclusivamente por consumir a API publica do TVMaze.
 * Toda a comunicacao HTTP e conversao de JSON fica isolada aqui, separada
 * completamente da interface grafica (responsabilidade unica).
 */
public class ApiTvMazeService {

    private static final String BASE_URL = "https://api.tvmaze.com";
    private static final int TIMEOUT_MS = 8000;

    private final ObjectMapper objectMapper;

    public ApiTvMazeService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Busca series na API TVMaze pelo nome informado.
     *
     * @param nome termo de pesquisa
     * @return lista de series encontradas (pode ser vazia, nunca nula)
     * @throws ApiException em caso de falha de rede, timeout ou resposta invalida
     */
    public List<Serie> buscarSeries(String nome) throws ApiException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ApiException("O termo de busca nao pode estar vazio.");
        }

        List<Serie> resultado = new ArrayList<>();
        try {
            String termoCodificado = URLEncoder.encode(nome.trim(), StandardCharsets.UTF_8);
            String url = BASE_URL + "/search/shows?q=" + termoCodificado;
            String json = executarRequisicao(url);

            SearchResultDTO[] resultados = objectMapper.readValue(json, SearchResultDTO[].class);
            for (SearchResultDTO item : resultados) {
                if (item.getShow() != null) {
                    resultado.add(converterParaSerie(item.getShow()));
                }
            }
        } catch (SocketTimeoutException e) {
            throw new ApiException("Tempo de conexao esgotado. Verifique sua internet.", e);
        } catch (IOException e) {
            throw new ApiException("Nao foi possivel conectar a API do TVMaze.", e);
        } catch (Exception e) {
            throw new ApiException("Erro inesperado ao processar dados da API.", e);
        }
        return resultado;
    }

    /**
     * Executa a requisicao HTTP GET e retorna o corpo da resposta como String.
     */
    private String executarRequisicao(String urlStr) throws IOException, ApiException {
        HttpURLConnection conexao = null;
        try {
            URL url = new URL(urlStr);
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setConnectTimeout(TIMEOUT_MS);
            conexao.setReadTimeout(TIMEOUT_MS);
            conexao.setRequestProperty("Accept", "application/json");

            int status = conexao.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new ApiException("A API retornou um erro (codigo " + status + ").");
            }

            try (InputStream is = conexao.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    sb.append(linha);
                }
                return sb.toString();
            }
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }

    /**
     * Converte o DTO vindo da API para o modelo interno Serie, ja tratando
     * campos nulos e removendo tags HTML do resumo.
     */
    private Serie converterParaSerie(ShowDTO dto) {
        Serie serie = new Serie();
        serie.setId(dto.getId());
        serie.setNome(dto.getName() != null ? dto.getName() : "Sem nome");
        serie.setIdioma(dto.getLanguage() != null ? dto.getLanguage() : "N/A");
        serie.setGeneros(dto.getGenres() != null ? dto.getGenres() : new ArrayList<>());
        serie.setStatus(dto.getStatus() != null ? dto.getStatus() : "N/A");
        serie.setDataEstreia(dto.getPremiered());
        serie.setDataTermino(dto.getEnded());

        if (dto.getRating() != null) {
            serie.setNota(dto.getRating().getAverage());
        }

        String emissora = "N/A";
        if (dto.getNetwork() != null && dto.getNetwork().getName() != null) {
            emissora = dto.getNetwork().getName();
        } else if (dto.getWebChannel() != null && dto.getWebChannel().getName() != null) {
            emissora = dto.getWebChannel().getName();
        }
        serie.setEmissora(emissora);

        if (dto.getImage() != null) {
            serie.setImagemUrl(dto.getImage().getMedium() != null
                    ? dto.getImage().getMedium() : dto.getImage().getOriginal());
        }

        serie.setResumo(removerTagsHtml(dto.getSummary()));
        return serie;
    }

    /**
     * Remove tags HTML simples do resumo retornado pela API, deixando texto puro.
     */
    private String removerTagsHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * Excecao especifica para falhas relacionadas a API, usada para que a
     * camada de interface possa exibir mensagens amigaveis ao usuario.
     */
    public static class ApiException extends Exception {
        public ApiException(String mensagem) {
            super(mensagem);
        }

        public ApiException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }
    }
}
