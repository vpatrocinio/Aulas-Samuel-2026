package service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Classes auxiliares (DTOs) usadas apenas para mapear o JSON retornado pela
 * API publica do TVMaze atraves do Jackson. Nao sao expostas fora do pacote
 * service - o ApiTvMazeService converte estes objetos para model.Serie.
 */
public class TvMazeDTO {

    /**
     * Representa um item do resultado de /search/shows?q=
     * Formato: [{ "score": 1.0, "show": { ... } }, ...]
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResultDTO {
        private double score;
        private ShowDTO show;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public ShowDTO getShow() {
            return show;
        }

        public void setShow(ShowDTO show) {
            this.show = show;
        }
    }

    /**
     * Representa o objeto "show" retornado pela API TVMaze.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShowDTO {
        private int id;
        private String name;
        private String language;
        private List<String> genres;
        private String status;
        private String premiered;
        private String ended;
        private String summary;
        private RatingDTO rating;
        private NetworkDTO network;
        private NetworkDTO webChannel;
        private ImageDTO image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPremiered() {
            return premiered;
        }

        public void setPremiered(String premiered) {
            this.premiered = premiered;
        }

        public String getEnded() {
            return ended;
        }

        public void setEnded(String ended) {
            this.ended = ended;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public RatingDTO getRating() {
            return rating;
        }

        public void setRating(RatingDTO rating) {
            this.rating = rating;
        }

        public NetworkDTO getNetwork() {
            return network;
        }

        public void setNetwork(NetworkDTO network) {
            this.network = network;
        }

        public NetworkDTO getWebChannel() {
            return webChannel;
        }

        public void setWebChannel(NetworkDTO webChannel) {
            this.webChannel = webChannel;
        }

        public ImageDTO getImage() {
            return image;
        }

        public void setImage(ImageDTO image) {
            this.image = image;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RatingDTO {
        private Double average;

        public Double getAverage() {
            return average;
        }

        public void setAverage(Double average) {
            this.average = average;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkDTO {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageDTO {
        private String medium;
        private String original;

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
