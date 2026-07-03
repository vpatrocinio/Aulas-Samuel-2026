import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class MiniJson {

    public static Object parse(String texto) {
        Parser parser = new Parser(texto);
        return parser.parseValor();
    }

    private static class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = s;
            this.pos = 0;
        }

        private void pularEspacos() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        Object parseValor() {
            pularEspacos();
            char c = s.charAt(pos);
            if (c == '{') {
                return parseObjeto();
            } else if (c == '[') {
                return parseArray();
            } else if (c == '"') {
                return parseString();
            } else if (c == 't' || c == 'f') {
                return parseBoolean();
            } else if (c == 'n') {
                pos += 4; // pula "null"
                return null;
            } else {
                return parseNumero();
            }
        }

        Map<String, Object> parseObjeto() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            pos++; // pula {
            pularEspacos();
            if (s.charAt(pos) == '}') {
                pos++;
                return mapa;
            }
            while (true) {
                pularEspacos();
                String chave = parseString();
                pularEspacos();
                pos++; // pula :
                Object valor = parseValor();
                mapa.put(chave, valor);
                pularEspacos();
                char c = s.charAt(pos);
                if (c == ',') {
                    pos++;
                } else if (c == '}') {
                    pos++;
                    break;
                }
            }
            return mapa;
        }

        List<Object> parseArray() {
            List<Object> lista = new ArrayList<>();
            pos++; // pula [
            pularEspacos();
            if (s.charAt(pos) == ']') {
                pos++;
                return lista;
            }
            while (true) {
                Object valor = parseValor();
                lista.add(valor);
                pularEspacos();
                char c = s.charAt(pos);
                if (c == ',') {
                    pos++;
                } else if (c == ']') {
                    pos++;
                    break;
                }
            }
            return lista;
        }

        String parseString() {
            StringBuilder sb = new StringBuilder();
            pos++; // pula aspas de abertura
            while (s.charAt(pos) != '"') {
                char c = s.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char escapado = s.charAt(pos);
                    switch (escapado) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'u':
                            String hex = s.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default:
                            sb.append(escapado);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // pula aspas de fechamento
            return sb.toString();
        }

        Boolean parseBoolean() {
            if (s.charAt(pos) == 't') {
                pos += 4; // "true"
                return Boolean.TRUE;
            } else {
                pos += 5; // "false"
                return Boolean.FALSE;
            }
        }

        Double parseNumero() {
            int inicio = pos;
            while (pos < s.length() && "-+.eE0123456789".indexOf(s.charAt(pos)) >= 0) {
                pos++;
            }
            return Double.parseDouble(s.substring(inicio, pos));
        }
    }

    // ===================== WRITER (objetos Java -> texto) =====================

    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escreverValor(valor, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void escreverValor(Object valor, StringBuilder sb) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof String) {
            escreverString((String) valor, sb);
        } else if (valor instanceof Number || valor instanceof Boolean) {
            sb.append(valor.toString());
        } else if (valor instanceof Map) {
            Map<String, Object> mapa = (Map<String, Object>) valor;
            sb.append('{');
            int i = 0;
            for (Map.Entry<String, Object> entrada : mapa.entrySet()) {
                if (i++ > 0) {
                    sb.append(',');
                }
                escreverString(entrada.getKey(), sb);
                sb.append(':');
                escreverValor(entrada.getValue(), sb);
            }
            sb.append('}');
        } else if (valor instanceof List) {
            List<Object> lista = (List<Object>) valor;
            sb.append('[');
            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) {
                    sb.append(',');
                }
                escreverValor(lista.get(i), sb);
            }
            sb.append(']');
        }
    }

    private static void escreverString(String texto, StringBuilder sb) {
        sb.append('"');
        for (char c : texto.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c == '\n') {
                sb.append("\\n");
            } else {
                sb.append(c);
            }
        }
        sb.append('"');
    }
}
