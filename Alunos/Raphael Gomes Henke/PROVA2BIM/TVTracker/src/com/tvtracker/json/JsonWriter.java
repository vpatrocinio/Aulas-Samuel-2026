package com.tvtracker.json;

import java.util.List;
import java.util.Map;

/**
 * SERIALIZADOR JSON SIMPLES (SEM BIBLIOTECA EXTERNA)
 * - Converte objetos Java (Map, List, String, etc.)
 *   em texto JSON formatado (pretty print)
 */
public final class JsonWriter {

    private JsonWriter() {}

    /**
     * MÉTODO PRINCIPAL
     * Converte qualquer objeto em JSON String
     */
    public static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(value, sb, 0);
        return sb.toString();
    }

    /**
     * PROCESSA O TIPO DO OBJETO E CONSTRÓI O JSON
     */
    @SuppressWarnings("unchecked")
    private static void writeValue(Object value, StringBuilder sb, int indent) {

        if (value == null) {
            sb.append("null");

        } else if (value instanceof String) {
            writeString((String) value, sb);

        } else if (value instanceof Number) {

            Number n = (Number) value;

            // trata números inteiros vs decimais
            if (n instanceof Double || n instanceof Float) {

                double d = n.doubleValue();

                if (!Double.isNaN(d) && !Double.isInfinite(d)
                        && d == Math.floor(d)
                        && Math.abs(d) < 1e15) {
                    sb.append((long) d);
                } else {
                    sb.append(n.toString());
                }

            } else {
                sb.append(n.toString());
            }

        } else if (value instanceof Boolean) {
            sb.append(value.toString());

        } else if (value instanceof Map) {

            Map<String, Object> map = (Map<String, Object>) value;

            if (map.isEmpty()) {
                sb.append("{}");
                return;
            }

            sb.append("{\n");

            int i = 0;

            for (Map.Entry<String, Object> e : map.entrySet()) {

                indent(sb, indent + 1);

                writeString(e.getKey(), sb);
                sb.append(": ");

                writeValue(e.getValue(), sb, indent + 1);

                if (i < map.size() - 1) sb.append(",");
                sb.append("\n");

                i++;
            }

            indent(sb, indent);
            sb.append("}");

        } else if (value instanceof List) {

            List<Object> list = (List<Object>) value;

            if (list.isEmpty()) {
                sb.append("[]");
                return;
            }

            sb.append("[\n");

            for (int i = 0; i < list.size(); i++) {

                indent(sb, indent + 1);

                writeValue(list.get(i), sb, indent + 1);

                if (i < list.size() - 1) sb.append(",");

                sb.append("\n");
            }

            indent(sb, indent);
            sb.append("]");

        } else {
            // fallback para qualquer outro objeto
            writeString(value.toString(), sb);
        }
    }

    /**
     * ADICIONA INDENTAÇÃO (FORMATAÇÃO BONITA)
     */
    private static void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) sb.append("  ");
    }

    /**
     * ESCAPA STRINGS PARA JSON (", \n, \t, etc.)
     */
    private static void writeString(String s, StringBuilder sb) {

        sb.append('"');

        for (int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);

            switch (c) {

                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;

                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }

        sb.append('"');
    }
}