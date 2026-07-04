package Service;

public class JsonService {

    public static String pegarValor(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int inicio = json.indexOf(busca);

        if (inicio == -1) {
            return "";
        }

        inicio += busca.length();

        while (inicio < json.length() && json.charAt(inicio) == ' ') {
            inicio++;
        }

        if (json.charAt(inicio) == '"') {
            inicio++;
            int fim = json.indexOf("\"", inicio);
            return json.substring(inicio, fim);
        }

        int fim = json.indexOf(",", inicio);
        if (fim == -1) {
            fim = json.indexOf("}", inicio);
        }

        return json.substring(inicio, fim).trim();
    }
    public static void salvarUsuario(Modelos.Usuario usuario) {
        try {
            System.out.println("Salvando usuário...");
            java.io.FileWriter writer = new java.io.FileWriter("usuario.json");

            writer.write("{\n");
            writer.write("\"nome\":\"" + usuario.getNome() + "\",\n");

            writer.write("\"favoritos\":[\n");
            salvarLista(writer, usuario.getFavoritos());
            writer.write("],\n");

            writer.write("\"assistidas\":[\n");
            salvarLista(writer, usuario.getAssistidas());
            writer.write("],\n");

            writer.write("\"desejoAssistir\":[\n");
            salvarLista(writer, usuario.getDesejoAssistir());
            writer.write("]\n");

            writer.write("}");

            writer.close();
            System.out.println("Arquivo salvo com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao salvar usuário.");
        }
    }

    private static void salvarLista(java.io.FileWriter writer, java.util.ArrayList<Modelos.Serie> lista) throws java.io.IOException {
        for (int i = 0; i < lista.size(); i++) {
            Modelos.Serie s = lista.get(i);

            writer.write("{");
            writer.write("\"nome\":\"" + s.getNome() + "\",");
            writer.write("\"idioma\":\"" + s.getIdioma() + "\",");
            writer.write("\"status\":\"" + s.getStatus() + "\",");
            writer.write("\"estreia\":\"" + s.getEstreia() + "\",");
            writer.write("\"termino\":\"" + s.getTermino() + "\",");
            writer.write("\"imagem\":\"" + s.getImagem() + "\"");
            writer.write("}");

            if (i < lista.size() - 1) {
                writer.write(",");
            }

            writer.write("\n");
        }
    }
    public static Modelos.Usuario carregarUsuario() {
        try {
            java.io.File arquivo = new java.io.File("usuario.json");

            if (!arquivo.exists()) {
                return new Modelos.Usuario("Miguel");
            }

            java.util.Scanner leitor = new java.util.Scanner(arquivo);
            String json = "";

            while (leitor.hasNextLine()) {
                json += leitor.nextLine();
            }

            leitor.close();

            String nome = pegarValor(json, "nome");
            Modelos.Usuario usuario = new Modelos.Usuario(nome);

            carregarLista(json, "favoritos", usuario.getFavoritos());
            carregarLista(json, "assistidas", usuario.getAssistidas());
            carregarLista(json, "desejoAssistir", usuario.getDesejoAssistir());

            return usuario;

        } catch (Exception e) {
            e.printStackTrace();
            return new Modelos.Usuario("Miguel");
        }
    }
    private static void carregarLista(String json, String nomeLista, java.util.ArrayList<Modelos.Serie> lista) {
        int inicioLista = json.indexOf("\"" + nomeLista + "\":[");

        if (inicioLista == -1) {
            return;
        }

        inicioLista = json.indexOf("[", inicioLista);
        int fimLista = json.indexOf("]", inicioLista);

        String conteudoLista = json.substring(inicioLista + 1, fimLista);

        String[] series = conteudoLista.split("\\},");

        for (String item : series) {
            if (item.trim().isEmpty()) {
                continue;
            }

            if (!item.endsWith("}")) {
                item += "}";
            }

            Modelos.Serie serie = new Modelos.Serie();

            serie.setNome(pegarValor(item, "nome"));
            serie.setIdioma(pegarValor(item, "idioma"));
            serie.setStatus(pegarValor(item, "status"));
            serie.setEstreia(pegarValor(item, "estreia"));
            serie.setTermino(pegarValor(item, "termino"));
            serie.setImagem(pegarValor(item, "imagem"));

            lista.add(serie);
        }
    }
    public static String pegarArray(String json, String chave) {
        String busca = "\"" + chave + "\":[";
        int inicio = json.indexOf(busca);

        if (inicio == -1) {
            return "";
        }

        inicio += busca.length();

        int fim = json.indexOf("]", inicio);

        String conteudo = json.substring(inicio, fim);

        conteudo = conteudo.replace("\"", "");
        conteudo = conteudo.replace(",", ", ");

        return conteudo;
    }
    public static String pegarEmissora(String json) {
        int inicioNetwork = json.indexOf("\"network\":");

        if (inicioNetwork != -1) {
            int inicioName = json.indexOf("\"name\":", inicioNetwork);
            if (inicioName != -1) {
                return pegarValor(json.substring(inicioName), "name");
            }
        }

        int inicioWeb = json.indexOf("\"webChannel\":");

        if (inicioWeb != -1) {
            int inicioName = json.indexOf("\"name\":", inicioWeb);
            if (inicioName != -1) {
                return pegarValor(json.substring(inicioName), "name");
            }
        }

        return "Não informado";
    }
    public static java.util.ArrayList<String> separarResultados(String json) {
        java.util.ArrayList<String> resultados = new java.util.ArrayList<>();

        String[] partes = json.split("\\{\"score\":");

        for (int i = 1; i < partes.length; i++) {
            String item = "{\"score\":" + partes[i];

            int fim = item.indexOf("}}");
            if (fim != -1) {
                item = item.substring(0, fim + 2);
            }

            resultados.add(item);
        }

        return resultados;
    }
}