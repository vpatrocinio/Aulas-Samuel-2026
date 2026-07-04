import org.json.JSONObject;
import org.json.JSONArray;
// Esta classe representa uma serie de TV com todos os seus
// atributos (nome, nota, generos, etc).
// Tambem contem os metodos de SERIALIZACAO (toJSON) e
// DESSERIALIZACAO (fromJSON) que convertem o objeto para JSON
// e vice-versa, permitindo salvar/carregar no arquivo.
//

public class Serie implements Comparable<Serie> {
    private int id;
    private String nome;
    private String idioma;
    private String generos;
    private double nota;
    private String estado;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;
    private String imagemUrl;

    public Serie() {}

    public Serie(int id, String nome, String idioma, String generos, double nota,
                 String estado, String dataEstreia, String dataTermino, String emissora, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.estado = estado;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
        this.imagemUrl = imagemUrl;
    }


    // SERIALIZACAO: converte o objeto Serie em JSON
    // Este metodo pega todos os atributos da serie e converte
    // para um objeto JSON com pares chave-valor.
    // Esse JSON eh usado para salvar a serie no arquivo de dados.
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("nome", nome);
        obj.put("idioma", idioma);
        obj.put("generos", generos);
        obj.put("nota", nota);
        obj.put("estado", estado);
        obj.put("dataEstreia", dataEstreia);
        obj.put("dataTermino", dataTermino);
        obj.put("emissora", emissora);
        obj.put("imagemUrl", imagemUrl);
        return obj;   // <-- Retorna o JSON da serie
    }

    // DESSERIALIZACAO: converte JSON de volta para objeto Serie
    // Este metodo estatico recebe um JSONObject (vindo do arquivo)
    // e recria o objeto Serie com todos os seus atributos.
    // Usa optInt/optString/optDouble para evitar erros se algum
    // campo estiver faltando no JSON.
    public static Serie fromJSON(JSONObject obj) {
        return new Serie(
                obj.optInt("id"),
                obj.optString("nome"),
                obj.optString("idioma"),
                obj.optString("generos"),
                obj.optDouble("nota"),
                obj.optString("estado"),
                obj.optString("dataEstreia"),
                obj.optString("dataTermino"),
                obj.optString("emissora"),
                obj.optString("imagemUrl")
        );
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getIdioma() { return idioma; }
    public String getGeneros() { return generos; }
    public double getNota() { return nota; }
    public String getEstado() { return estado; }
    public String getDataEstreia() { return dataEstreia; }
    public String getDataTermino() { return dataTermino; }
    public String getEmissora() { return emissora; }
    public String getImagemUrl() { return imagemUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public void setGeneros(String generos) { this.generos = generos; }
    public void setNota(double nota) { this.nota = nota; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setDataEstreia(String dataEstreia) { this.dataEstreia = dataEstreia; }
    public void setDataTermino(String dataTermino) { this.dataTermino = dataTermino; }
    public void setEmissora(String emissora) { this.emissora = emissora; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    @Override
    public String toString() {
        return nome + " (" + estado + ") - Nota: " + nota;
    }

    @Override
    public int compareTo(Serie outra) {
        return this.nome.compareToIgnoreCase(outra.nome);
    }
}