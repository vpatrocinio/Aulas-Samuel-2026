import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Modelo (POJO) imutável de uma série de TV, com conversão de/para JSON.

public final class SerieTV {

    private final int id;
    private final String nome;
    private final String idioma;
    private final List<String> generos;
    private final Double nota;
    private final String estado;
    private final String estreia;
    private final String termino;
    private final String emissora;
    private final String sinopse;
    private final String poster;

    public SerieTV(int id, String nome, String idioma, List<String> generos, Double nota,
                    String estado, String estreia, String termino, String emissora,
                    String sinopse, String poster) {
        this.id = id;
        this.nome = txt(nome);
        this.idioma = txt(idioma);
        this.generos = generos == null ? List.of() : List.copyOf(generos);
        this.nota = nota;
        this.estado = txt(estado);
        this.estreia = txt(estreia);
        this.termino = txt(termino);
        this.emissora = txt(emissora);
        this.sinopse = txt(sinopse);
        this.poster = txt(poster);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getIdioma() { return idioma; }
    public List<String> getGeneros() { return generos; }
    public Double getNota() { return nota; }
    public String getEstadoBruto() { return estado; }
    public String getEstreia() { return estreia; }
    public String getTermino() { return termino; }
    public String getEmissora() { return emissora; }
    public String getSinopse() { return sinopse; }
    public String getPoster() { return poster; }

    public double notaParaOrdenar() { return nota != null ? nota : -1.0; }

    public String notaFormatada() { return nota != null ? String.format("%.1f", nota) : "—"; }

    public String generosFormatados() { return generos.isEmpty() ? "—" : String.join(" · ", generos); }

    public String estadoLegivel() {
        return switch (estado.toLowerCase()) {
            case "running" -> "Em exibição";
            case "ended" -> "Finalizada";
            case "to be determined" -> "A definir";
            case "in development" -> "Em desenvolvimento";
            default -> estado.isBlank() ? "Desconhecido" : estado;
        };
    }

    public int ordemEstado() {
        return switch (estado.toLowerCase()) {
            case "running" -> 0;
            case "to be determined" -> 1;
            case "in development" -> 2;
            case "ended" -> 3;
            default -> 4;
        };
    }

    public Map<String, Object> paraMapa() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("nome", nome);
        m.put("idioma", idioma);
        m.put("generos", new ArrayList<Object>(generos));
        m.put("nota", nota);
        m.put("estado", estado);
        m.put("estreia", estreia);
        m.put("termino", termino);
        m.put("emissora", emissora);
        m.put("sinopse", sinopse);
        m.put("poster", poster);
        return m;
    }

    @SuppressWarnings("unchecked")
    public static SerieTV deMapa(Map<String, Object> m) {
        List<String> generos = new ArrayList<>();
        if (m.get("generos") instanceof List<?> lista) {
            for (Object o : lista) if (o != null) generos.add(o.toString());
        }
        Double nota = (m.get("nota") instanceof Number n) ? n.doubleValue() : null;
        return new SerieTV(
                paraInt(m.get("id")), txt(m.get("nome")), txt(m.get("idioma")), generos, nota,
                txt(m.get("estado")), txt(m.get("estreia")), txt(m.get("termino")),
                txt(m.get("emissora")), txt(m.get("sinopse")), txt(m.get("poster")));
    }

    @SuppressWarnings("unchecked")
    public static SerieTV deShowApi(Map<String, Object> show) {
        List<String> generos = new ArrayList<>();
        if (show.get("genres") instanceof List<?> lista) {
            for (Object o : lista) if (o != null) generos.add(o.toString());
        }
        Double nota = null;
        if (show.get("rating") instanceof Map<?, ?> rating && rating.get("average") instanceof Number n) {
            nota = n.doubleValue();
        }
        String emissora = "";
        if (show.get("network") instanceof Map<?, ?> net) emissora = txt(net.get("name"));
        if (emissora.isBlank() && show.get("webChannel") instanceof Map<?, ?> web) {
            emissora = txt(web.get("name"));
        }
        String poster = "";
        if (show.get("image") instanceof Map<?, ?> img) {
            poster = txt(img.get("medium"));
            if (poster.isBlank()) poster = txt(img.get("original"));
        }
        return new SerieTV(
                paraInt(show.get("id")), txt(show.get("name")), txt(show.get("language")), generos,
                nota, txt(show.get("status")), txt(show.get("premiered")), txt(show.get("ended")),
                emissora, removerHtml(txt(show.get("summary"))), poster);
    }

    private static String removerHtml(String html) {
        if (html.isBlank()) return "";
        return html.replaceAll("<[^>]+>", "")
                .replace("&amp;", "&").replace("&quot;", "\"")
                .replace("&#39;", "'").replace("&lt;", "<").replace("&gt;", ">").trim();
    }

    private static String txt(Object o) { return o == null ? "" : o.toString(); }

    private static int paraInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof SerieTV outra) && outra.id == id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return nome; }
}
