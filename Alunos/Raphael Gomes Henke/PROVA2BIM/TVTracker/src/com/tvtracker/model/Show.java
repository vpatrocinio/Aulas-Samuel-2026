package com.tvtracker.model;

import java.util.*;

/**
 * Representa uma série de TV, com os dados relevantes vindos da API do TVMaze.
 */
public class Show {

    private int id;
    private String name;
    private String language;
    private List<String> genres = new ArrayList<>();
    private Double rating;
    private String status;
    private String premiered;
    private String ended;
    private String network;
    private String imageUrl;
    private String summary;

    public Show() {
    }

    /** Constrói um Show a partir do JSON retornado pela API do TVMaze. */
    public static Show fromApiJson(Map<String, Object> json) {
        Show s = new Show();
        s.id = toInt(json.get("id"));
        s.name = toStr(json.get("name"));
        s.language = toStr(json.get("language"));

        Object genresObj = json.get("genres");
        if (genresObj instanceof List) {
            for (Object g : (List<?>) genresObj) {
                if (g != null) s.genres.add(g.toString());
            }
        }

        Object ratingObj = json.get("rating");
        if (ratingObj instanceof Map) {
            Object avg = ((Map<?, ?>) ratingObj).get("average");
            if (avg instanceof Number) s.rating = ((Number) avg).doubleValue();
        }

        s.status = toStr(json.get("status"));
        s.premiered = toStr(json.get("premiered"));
        s.ended = toStr(json.get("ended"));

        Object networkObj = json.get("network");
        if (networkObj == null) networkObj = json.get("webChannel");
        if (networkObj instanceof Map) {
            Object nm = ((Map<?, ?>) networkObj).get("name");
            s.network = nm != null ? nm.toString() : null;
        }

        Object imageObj = json.get("image");
        if (imageObj instanceof Map) {
            Object medium = ((Map<?, ?>) imageObj).get("medium");
            s.imageUrl = medium != null ? medium.toString() : null;
        }

        String rawSummary = toStr(json.get("summary"));
        if (rawSummary != null) {
            s.summary = rawSummary.replaceAll("<[^>]+>", "").trim();
        }

        return s;
    }




    /*
    /////////////////////////

    SERIALIZAÇÃO

    /////////////////////////
    */
    /** Converte este Show em um mapa "plano", usado para persistência local em JSON. */
    public Map<String, Object> toStorageJson() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", (long) id);
        map.put("name", name);
        map.put("language", language);
        map.put("genres", new ArrayList<Object>(genres));
        map.put("rating", rating);
        map.put("status", status);
        map.put("premiered", premiered);
        map.put("ended", ended);
        map.put("network", network);
        map.put("imageUrl", imageUrl);
        map.put("summary", summary);
        return map;
    }



    /*
    /////////////////////////
    
    DESSERIALIZAÇÃO

    /////////////////////////
    */
    /** Reconstrói um Show a partir do formato salvo localmente (toStorageJson). */
    @SuppressWarnings("unchecked")
    public static Show fromStorageJson(Map<String, Object> json) {
        Show s = new Show();
        s.id = toInt(json.get("id"));
        s.name = toStr(json.get("name"));
        s.language = toStr(json.get("language"));

        Object genresObj = json.get("genres");
        if (genresObj instanceof List) {
            for (Object g : (List<Object>) genresObj) {
                if (g != null) s.genres.add(g.toString());
            }
        }

        Object ratingObj = json.get("rating");
        if (ratingObj instanceof Number) s.rating = ((Number) ratingObj).doubleValue();

        s.status = toStr(json.get("status"));
        s.premiered = toStr(json.get("premiered"));
        s.ended = toStr(json.get("ended"));
        s.network = toStr(json.get("network"));
        s.imageUrl = toStr(json.get("imageUrl"));
        s.summary = toStr(json.get("summary"));
        return s;
    }



    
    private static int toInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        if (o instanceof String) {
            try {
                return Integer.parseInt((String) o);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private static String toStr(Object o) {
        return o == null ? null : o.toString();
    }

    // ---- getters ----

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLanguage() { return language; }
    public List<String> getGenres() { return genres; }
    public Double getRating() { return rating; }
    public String getStatus() { return status; }
    public String getPremiered() { return premiered; }
    public String getEnded() { return ended; }
    public String getNetwork() { return network; }
    public String getImageUrl() { return imageUrl; }
    public String getSummary() { return summary; }

    public String getStatusPortugues() {
        if (status == null) return "Desconhecido";
        switch (status) {
            case "Running": return "Em exibição";
            case "Ended": return "Finalizada";
            case "To Be Determined": return "Indefinido";
            case "In Development": return "Em desenvolvimento";
            default: return status;
        }
    }

    public String getGenresAsString() {
        return genres.isEmpty() ? "-" : String.join(", ", genres);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Show)) return false;
        return id == ((Show) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
