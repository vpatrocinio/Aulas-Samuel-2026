package util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Classe utilitaria responsavel por fornecer uma instancia unica e
 * configurada do ObjectMapper (Jackson) para todo o sistema.
 * Centralizar isso evita criar varias instancias e garante configuracao
 * consistente de serializacao/desserializacao em todo o projeto.
 */
public class JsonUtil {

    private static final ObjectMapper INSTANCIA = criarObjectMapper();

    private JsonUtil() {
        // Classe utilitaria - nao deve ser instanciada
    }

    private static ObjectMapper criarObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Ignora campos desconhecidos para nao quebrar ao ler JSON antigo/diferente
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Deixa o JSON salvo em arquivo mais legivel (identado)
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

    /**
     * Retorna a instancia compartilhada do ObjectMapper.
     */
    public static ObjectMapper getObjectMapper() {
        return INSTANCIA;
    }
}
