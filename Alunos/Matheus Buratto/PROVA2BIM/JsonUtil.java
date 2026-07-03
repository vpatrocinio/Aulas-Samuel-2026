import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Parser/serializador de JSON escrito manualmente (texto ⇄ Map/List Java).

public final class JsonUtil {

    private final char[] origem;
    private int pos;

    private JsonUtil(String texto) {
        this.origem = texto.toCharArray();
    }

    public static Object parse(String texto) throws JsonParseException {
        if (texto == null || texto.isBlank()) {
            throw new JsonParseException("Texto JSON vazio.");
        }
        JsonUtil leitor = new JsonUtil(texto);
        leitor.pularEspacos();
        Object valor = leitor.valor();
        leitor.pularEspacos();
        if (leitor.pos != leitor.origem.length) {
            throw new JsonParseException("Caracteres extras após o JSON.");
        }
        return valor;
    }

    public static String stringify(Object valor) {
        StringBuilder out = new StringBuilder();
        escrever(valor, out, 0);
        return out.toString();
    }

    private Object valor() throws JsonParseException {
        pularEspacos();
        if (pos >= origem.length) throw new JsonParseException("JSON incompleto.");
        char c = origem[pos];
        if (c == '{') return objeto();
        if (c == '[') return array();
        if (c == '"') return texto();
        if (c == 't' || c == 'f') return booleano();
        if (c == 'n') return nulo();
        if (c == '-' || Character.isDigit(c)) return numero();
        throw new JsonParseException("Símbolo inesperado '" + c + "'.");
    }

    private Map<String, Object> objeto() throws JsonParseException {
        Map<String, Object> mapa = new LinkedHashMap<>();
        pos++; // '{'
        pularEspacos();
        if (atual() == '}') { pos++; return mapa; }
        while (true) {
            pularEspacos();
            String chave = texto();
            pularEspacos();
            consumir(':');
            mapa.put(chave, valor());
            pularEspacos();
            char c = origem[pos++];
            if (c == '}') break;
            if (c != ',') throw new JsonParseException("Esperava ',' ou '}'.");
        }
        return mapa;
    }

    private List<Object> array() throws JsonParseException {
        List<Object> lista = new ArrayList<>();
        pos++; // '['
        pularEspacos();
        if (atual() == ']') { pos++; return lista; }
        while (true) {
            lista.add(valor());
            pularEspacos();
            char c = origem[pos++];
            if (c == ']') break;
            if (c != ',') throw new JsonParseException("Esperava ',' ou ']'.");
        }
        return lista;
    }

    private String texto() throws JsonParseException {
        consumir('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (pos >= origem.length) throw new JsonParseException("Texto não fechado.");
            char c = origem[pos++];
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            char esc = origem[pos++];
            switch (esc) {
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case '/': sb.append('/'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'n': sb.append('\n'); break;
                case 'r': sb.append('\r'); break;
                case 't': sb.append('\t'); break;
                case 'u':
                    String hex = new String(origem, pos, 4);
                    pos += 4;
                    sb.append((char) Integer.parseInt(hex, 16));
                    break;
                default: throw new JsonParseException("Escape inválido \\" + esc);
            }
        }
    }

    private Double numero() {
        int inicio = pos;
        while (pos < origem.length && "-+.eE0123456789".indexOf(origem[pos]) >= 0) pos++;
        return Double.parseDouble(new String(origem, inicio, pos - inicio));
    }

    private Boolean booleano() throws JsonParseException {
        if (combina("true")) { pos += 4; return Boolean.TRUE; }
        if (combina("false")) { pos += 5; return Boolean.FALSE; }
        throw new JsonParseException("Valor booleano inválido.");
    }

    private Object nulo() throws JsonParseException {
        if (combina("null")) { pos += 4; return null; }
        throw new JsonParseException("Valor inválido.");
    }

    private boolean combina(String palavra) {
        return pos + palavra.length() <= origem.length
                && new String(origem, pos, palavra.length()).equals(palavra);
    }

    private void pularEspacos() {
        while (pos < origem.length && Character.isWhitespace(origem[pos])) pos++;
    }

    private char atual() {
        return pos < origem.length ? origem[pos] : '\0';
    }

    private void consumir(char esperado) throws JsonParseException {
        if (pos >= origem.length || origem[pos] != esperado) {
            throw new JsonParseException("Esperava '" + esperado + "'.");
        }
        pos++;
    }

    @SuppressWarnings("unchecked")
    private static void escrever(Object valor, StringBuilder out, int nivel) {
        if (valor == null) {
            out.append("null");
        } else if (valor instanceof String s) {
            escreverTexto(s, out);
        } else if (valor instanceof Number n) {
            double d = n.doubleValue();
            out.append(d == Math.floor(d) && !Double.isInfinite(d)
                    ? Long.toString((long) d) : Double.toString(d));
        } else if (valor instanceof Boolean b) {
            out.append(b);
        } else if (valor instanceof Map<?, ?> mapa) {
            escreverObjeto((Map<String, Object>) mapa, out, nivel);
        } else if (valor instanceof List<?> lista) {
            escreverArray((List<Object>) lista, out, nivel);
        } else {
            escreverTexto(valor.toString(), out);
        }
    }

    private static void escreverObjeto(Map<String, Object> mapa, StringBuilder out, int nivel) {
        if (mapa.isEmpty()) { out.append("{}"); return; }
        out.append("{\n");
        int i = 0;
        for (Map.Entry<String, Object> e : mapa.entrySet()) {
            recuo(out, nivel + 1);
            escreverTexto(e.getKey(), out);
            out.append(": ");
            escrever(e.getValue(), out, nivel + 1);
            if (++i < mapa.size()) out.append(',');
            out.append('\n');
        }
        recuo(out, nivel);
        out.append('}');
    }

    private static void escreverArray(List<Object> lista, StringBuilder out, int nivel) {
        if (lista.isEmpty()) { out.append("[]"); return; }
        out.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            recuo(out, nivel + 1);
            escrever(lista.get(i), out, nivel + 1);
            if (i + 1 < lista.size()) out.append(',');
            out.append('\n');
        }
        recuo(out, nivel);
        out.append(']');
    }

    private static void escreverTexto(String s, StringBuilder out) {
        out.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
            }
        }
        out.append('"');
    }

    private static void recuo(StringBuilder out, int nivel) {
        out.append("  ".repeat(nivel));
    }
}
