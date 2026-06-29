package fag;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;

// Classe responsável pela persistência local.
// Aqui o sistema salva e carrega os dados em formato JSON.
public class JsonStorage {

    private static final String ARQUIVO = "dados_series.json";

    /*
      SERIALIZAÇÃO DO ARQUIVO.
      Este método salva os dados no arquivo dados_series.json.
      Ele chama gerarJson(dados), que transforma os objetos em texto JSON.
     */
    public void salvar(DadosUsuario dados) throws AppException {
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            writer.write(gerarJson(dados));
        } catch (Exception e) {
            throw new AppException("Erro ao salvar os dados em JSON.");
        }
    }

    /*
      DESSERIALIZAÇÃO DO ARQUIVO.
     
      Este método carrega o arquivo dados_series.json.
      Ele lê o JSON salvo e recria os objetos DadosUsuario e Serie.
     */
    public DadosUsuario carregar() throws AppException {
        try {
            File arquivo = new File(ARQUIVO);

            if (!arquivo.exists()) {
                return new DadosUsuario();
            }

            String json = Files.readString(arquivo.toPath());

            DadosUsuario dados = new DadosUsuario();

            dados.getFavoritos().clear();
            dados.getAssistidas().clear();
            dados.getQueroAssistir().clear();

            dados.setApelido(JsonUtil.pegarString(json, "apelido"));

            carregarLista(json, "favoritos", dados.getFavoritos());
            carregarLista(json, "assistidas", dados.getAssistidas());
            carregarLista(json, "queroAssistir", dados.getQueroAssistir());

            return dados;

        } catch (Exception e) {
            throw new AppException("Erro ao carregar os dados salvos. Um novo perfil será iniciado.");
        }
    }

    // Carrega uma lista específica do JSON salvo.
    private void carregarLista(String json, String campo, List<Serie> lista) {
        List<String> objetos = JsonUtil.pegarObjetosDeArray(json, campo);

        for (String objeto : objetos) {
            lista.add(converterJsonParaSerie(objeto));
        }
    }

    // Transforma o JSON salvo de volta em objeto Serie.
    private Serie converterJsonParaSerie(String json) {
        int id = JsonUtil.pegarInt(json, "id");
        String nome = JsonUtil.pegarString(json, "nome");
        String idioma = JsonUtil.pegarString(json, "idioma");
        String generos = JsonUtil.pegarString(json, "generos");
        double nota = JsonUtil.pegarDouble(json, "nota");
        String estado = JsonUtil.pegarString(json, "estado");
        String dataEstreia = JsonUtil.pegarString(json, "dataEstreia");
        String dataTermino = JsonUtil.pegarString(json, "dataTermino");
        String emissora = JsonUtil.pegarString(json, "emissora");

        return new Serie(id, nome, idioma, generos, nota, estado, dataEstreia, dataTermino, emissora);
    }

    /*
     * AQUI ACONTECE A SERIALIZAÇÃO PRINCIPAL.
     *
     * Este método pega o objeto DadosUsuario e transforma em uma String JSON.
     */
    private String gerarJson(DadosUsuario dados) {
        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("  \"apelido\":\"").append(escapar(dados.getApelido())).append("\",\n");

        json.append("  \"favoritos\":");
        adicionarLista(json, dados.getFavoritos());
        json.append(",\n");

        json.append("  \"assistidas\":");
        adicionarLista(json, dados.getAssistidas());
        json.append(",\n");

        json.append("  \"queroAssistir\":");
        adicionarLista(json, dados.getQueroAssistir());
        json.append("\n");

        json.append("}");

        return json.toString();
    }

    // Serializa uma lista de séries para dentro do JSON.
    private void adicionarLista(StringBuilder json, List<Serie> lista) {
        json.append("[\n");

        for (int i = 0; i < lista.size(); i++) {
            json.append("    ").append(lista.get(i).toJson());

            if (i < lista.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("  ]");
    }

    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}