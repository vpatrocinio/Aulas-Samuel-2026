package com.tvtracker.json;

import java.util.*;

/**
 * PARSER JSON SIMPLES (SEM BIBLIOTECAS EXTERNAS)
 * - Converte texto JSON em estruturas Java:
 *   Map, List, String, Number, Boolean e null
 */
public final class JsonParser {

    private final String s;
    private int pos;

    private JsonParser(String s) {
        this.s = s;
        this.pos = 0;
    }

    /**
     * MÉTODO PRINCIPAL
     * Converte texto JSON em objeto Java
     */
    public static Object parse(String text) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Texto JSON vazio.");
        }

        JsonParser p = new JsonParser(text);
        p.skipWs();
        Object value = p.parseValue();
        p.skipWs();

        return value;
    }

    /**
     * IGNORA ESPAÇOS EM BRANCO
     */
    private void skipWs() {
        while (pos < s.length() &&
                Character.isWhitespace(s.charAt(pos))) {
            pos++;
        }
    }

    /**
     * IDENTIFICA O TIPO DO JSON E DIRECIONA O PARSE
     */
    private Object parseValue() {

        skipWs();

        if (pos >= s.length())
            throw new IllegalStateException("JSON incompleto.");

        char c = s.charAt(pos);

        switch (c) {

            case '{': return parseObject(); // objeto JSON
            case '[': return parseArray();  // array JSON
            case '"': return parseString(); // string JSON

            case 't':
                expect("true");
                return Boolean.TRUE;

            case 'f':
                expect("false");
                return Boolean.FALSE;

            case 'n':
                expect("null");
                return null;

            default:
                return parseNumber();
        }
    }

    /**
     * CONFERE LITERAIS (true, false, null)
     */
    private void expect(String literal) {

        if (pos + literal.length() > s.length()
                || !s.startsWith(literal, pos)) {

            throw new IllegalStateException(
                    "Token JSON inválido na posição " + pos);
        }

        pos += literal.length();
    }

    /**
     * PARSE DE OBJETO JSON { }
     */
    private Map<String, Object> parseObject() {

        Map<String, Object> map = new LinkedHashMap<>();

        pos++; // '{'
        skipWs();

        if (pos < s.length() && s.charAt(pos) == '}') {
            pos++;
            return map;
        }

        while (true) {

            skipWs();

            String key = parseString();

            skipWs();

            if (s.charAt(pos) != ':')
                throw new IllegalStateException(
                        "Esperado ':' na posição " + pos);

            pos++;

            Object val = parseValue();
            map.put(key, val);

            skipWs();

            char c = s.charAt(pos);

            if (c == ',') {
                pos++;
            } else if (c == '}') {
                pos++;
                break;
            } else {
                throw new IllegalStateException(
                        "Esperado ',' ou '}' na posição " + pos);
            }
        }

        return map;
    }

    /**
     * PARSE DE ARRAY JSON [ ]
     */
    private List<Object> parseArray() {

        List<Object> list = new ArrayList<>();

        pos++; // '['
        skipWs();

        if (pos < s.length() && s.charAt(pos) == ']') {
            pos++;
            return list;
        }

        while (true) {

            Object val = parseValue();
            list.add(val);

            skipWs();

            char c = s.charAt(pos);

            if (c == ',') {
                pos++;
            } else if (c == ']') {
                pos++;
                break;
            } else {
                throw new IllegalStateException(
                        "Esperado ',' ou ']' na posição " + pos);
            }
        }

        return list;
    }

    /**
     * PARSE DE STRING JSON
     */
    private String parseString() {

        if (s.charAt(pos) != '"')
            throw new IllegalStateException(
                    "Esperada string na posição " + pos);

        StringBuilder sb = new StringBuilder();
        pos++; // abre aspas

        while (true) {

            char c = s.charAt(pos++);

            if (c == '"') break;

            if (c == '\\') {

                char e = s.charAt(pos++);

                switch (e) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;

                    case 'u':
                        String hex = s.substring(pos, pos + 4);
                        pos += 4;
                        sb.append((char) Integer.parseInt(hex, 16));
                        break;

                    default:
                        throw new IllegalStateException(
                                "Escape inválido: \\" + e);
                }

            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * PARSE DE NÚMEROS (INT OU DOUBLE)
     */
    private Object parseNumber() {

        int start = pos;

        if (pos < s.length() && s.charAt(pos) == '-') pos++;

        while (pos < s.length() &&
                Character.isDigit(s.charAt(pos))) {
            pos++;
        }

        boolean isDouble = false;

        if (pos < s.length() && s.charAt(pos) == '.') {
            isDouble = true;
            pos++;

            while (pos < s.length() &&
                    Character.isDigit(s.charAt(pos))) {
                pos++;
            }
        }

        if (pos < s.length() &&
                (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {

            isDouble = true;
            pos++;

            if (pos < s.length() &&
                    (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                pos++;
            }

            while (pos < s.length() &&
                    Character.isDigit(s.charAt(pos))) {
                pos++;
            }
        }

        String numStr = s.substring(start, pos);

        if (numStr.isEmpty() || numStr.equals("-")) {
            throw new IllegalStateException(
                    "Número inválido na posição " + start);
        }

        if (isDouble) {
            return Double.parseDouble(numStr);
        }

        try {
            return Long.parseLong(numStr);
        } catch (NumberFormatException e) {
            return Double.parseDouble(numStr);
        }
    }
}