package fag;

import java.util.ArrayList;
import java.util.List;

// Esta classe é responsável por ajudar na leitura dos dados em JSON.
// Ela é usada tanto para interpretar o JSON vindo da API TVMaze,
// quanto para ler o JSON salvo localmente no arquivo dados_series.json.
public class JsonUtil {

    // Busca um campo de texto dentro do JSON.
    // Exemplo: pegarString(json, "name") busca o valor do campo "name".
    public static String pegarString(String json, String campo) {
        try {
            int indiceCampo = localizarCampo(json, campo);

            if (indiceCampo == -1) {
                return "Não informado";
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioValor = pularEspacos(json, doisPontos + 1);

            if (json.startsWith("null", inicioValor)) {
                return "Não informado";
            }

            // Caso o valor não esteja entre aspas, pega como valor simples.
            if (json.charAt(inicioValor) != '"') {
                return pegarValorSimples(json, inicioValor);
            }

            int inicioTexto = inicioValor + 1;
            int fimTexto = encontrarFimString(json, inicioTexto);

            if (fimTexto == -1) {
                return "Não informado";
            }

            return limparTexto(json.substring(inicioTexto, fimTexto));

        } catch (Exception e) {
            return "Não informado";
        }
    }

    // Busca um número inteiro dentro do JSON.
    public static int pegarInt(String json, String campo) {
        return (int) pegarDouble(json, campo);
    }

    // Busca um número decimal dentro do JSON.
    // É usado principalmente para pegar a nota da série.
    public static double pegarDouble(String json, String campo) {
        try {
            int indiceCampo = localizarCampo(json, campo);

            if (indiceCampo == -1) {
                return 0;
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioValor = pularEspacos(json, doisPontos + 1);

            if (json.startsWith("null", inicioValor)) {
                return 0;
            }

            String valor = pegarValorSimples(json, inicioValor);

            return Double.parseDouble(valor);

        } catch (Exception e) {
            return 0;
        }
    }

    // Busca um array de texto dentro do JSON e transforma em uma String.
    // Exemplo no JSON da API: "genres": ["Drama", "Action"]
    // O sistema transforma isso em: Drama, Action
    public static String pegarArrayComoTexto(String json, String campo) {
        try {
            int indiceCampo = localizarCampo(json, campo);

            if (indiceCampo == -1) {
                return "Não informado";
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioArray = json.indexOf("[", doisPontos);
            int fimArray = encontrarFimArray(json, inicioArray);

            if (inicioArray == -1 || fimArray == -1) {
                return "Não informado";
            }

            String conteudo = json.substring(inicioArray + 1, fimArray)
                    .replace("\"", "")
                    .trim();

            if (conteudo.isBlank()) {
                return "Não informado";
            }

            return conteudo.replace(",", ", ");

        } catch (Exception e) {
            return "Não informado";
        }
    }

    // Busca um objeto dentro do JSON.
    // Exemplo: dentro da API existe "rating": { "average": 8.5 }
    // Este método pega o bloco inteiro do objeto rating.
    public static String pegarObjeto(String json, String campo) {
        try {
            int indiceCampo = localizarCampo(json, campo);

            if (indiceCampo == -1) {
                return "";
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioValor = pularEspacos(json, doisPontos + 1);

            if (inicioValor >= json.length() || json.charAt(inicioValor) != '{') {
                return "";
            }

            int fimObjeto = encontrarFimObjeto(json, inicioValor);

            if (fimObjeto == -1) {
                return "";
            }

            return json.substring(inicioValor, fimObjeto + 1);

        } catch (Exception e) {
            return "";
        }
    }

    // Busca vários objetos dentro do JSON a partir de uma chave.
    // Na API TVMaze, a resposta vem com vários blocos "show".
    // Este método separa cada "show" para depois virar um objeto Serie.
    public static List<String> pegarObjetosDaChave(String json, String campo) {
        List<String> objetos = new ArrayList<>();

        int posicao = 0;

        while (true) {
            int indiceCampo = json.indexOf("\"" + campo + "\"", posicao);

            if (indiceCampo == -1) {
                break;
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioValor = pularEspacos(json, doisPontos + 1);

            if (inicioValor < json.length() && json.charAt(inicioValor) == '{') {
                int fimObjeto = encontrarFimObjeto(json, inicioValor);

                if (fimObjeto != -1) {
                    objetos.add(json.substring(inicioValor, fimObjeto + 1));
                    posicao = fimObjeto + 1;
                } else {
                    break;
                }
            } else {
                posicao = inicioValor + 1;
            }
        }

        return objetos;
    }

    // Busca objetos dentro de um array salvo no JSON local.
    // É usado para carregar favoritos, assistidas e queroAssistir do arquivo dados_series.json.
    public static List<String> pegarObjetosDeArray(String json, String campo) {
        List<String> objetos = new ArrayList<>();

        try {
            int indiceCampo = localizarCampo(json, campo);

            if (indiceCampo == -1) {
                return objetos;
            }

            int doisPontos = json.indexOf(":", indiceCampo);
            int inicioArray = json.indexOf("[", doisPontos);
            int fimArray = encontrarFimArray(json, inicioArray);

            if (inicioArray == -1 || fimArray == -1) {
                return objetos;
            }

            String array = json.substring(inicioArray + 1, fimArray);

            int posicao = 0;

            while (true) {
                int inicioObjeto = array.indexOf("{", posicao);

                if (inicioObjeto == -1) {
                    break;
                }

                int fimObjeto = encontrarFimObjeto(array, inicioObjeto);

                if (fimObjeto == -1) {
                    break;
                }

                objetos.add(array.substring(inicioObjeto, fimObjeto + 1));
                posicao = fimObjeto + 1;
            }

        } catch (Exception e) {
            return objetos;
        }

        return objetos;
    }

    // Localiza a posição de um campo dentro do JSON.
    private static int localizarCampo(String json, String campo) {
        return json.indexOf("\"" + campo + "\"");
    }

    // Ignora espaços em branco depois dos dois pontos do JSON.
    private static int pularEspacos(String texto, int inicio) {
        int i = inicio;

        while (i < texto.length() && Character.isWhitespace(texto.charAt(i))) {
            i++;
        }

        return i;
    }

    // Pega valores simples do JSON, como número, null ou texto sem aspas.
    private static String pegarValorSimples(String json, int inicio) {
        int fim = inicio;

        while (fim < json.length()) {
            char c = json.charAt(fim);

            if (c == ',' || c == '}' || c == ']') {
                break;
            }

            fim++;
        }

        return json.substring(inicio, fim).trim();
    }

    // Encontra onde termina uma string dentro do JSON.
    private static int encontrarFimString(String json, int inicio) {
        for (int i = inicio; i < json.length(); i++) {
            char atual = json.charAt(i);
            char anterior = i > 0 ? json.charAt(i - 1) : ' ';

            if (atual == '"' && anterior != '\\') {
                return i;
            }
        }

        return -1;
    }

    // Encontra onde termina um objeto JSON.
    // Conta as chaves { } para saber o fechamento correto.
    private static int encontrarFimObjeto(String json, int inicioObjeto) {
        int contador = 0;

        for (int i = inicioObjeto; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                contador++;
            } else if (c == '}') {
                contador--;
            }

            if (contador == 0) {
                return i;
            }
        }

        return -1;
    }

    // Encontra onde termina um array JSON.
    // Conta os colchetes [ ] para saber o fechamento correto.
    private static int encontrarFimArray(String json, int inicioArray) {
        int contador = 0;

        for (int i = inicioArray; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '[') {
                contador++;
            } else if (c == ']') {
                contador--;
            }

            if (contador == 0) {
                return i;
            }
        }

        return -1;
    }

    // Limpa caracteres especiais que podem vir no JSON.
    private static String limparTexto(String texto) {
        if (texto == null) {
            return "Não informado";
        }

        return texto
                .replace("\\\"", "\"")
                .replace("\\/", "/")
                .replace("\\n", " ")
                .replace("\\u0026", "&");
    }
}