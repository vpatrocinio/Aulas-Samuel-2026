package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Usuario;
import util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsavel exclusivamente por ler e escrever os dados do sistema
 * em disco, em formato JSON, utilizando o Jackson ObjectMapper.
 * Armazena um mapa de usuarios (chave = nome do usuario em minusculas) no
 * arquivo dados_usuario.json, permitindo que multiplos usuarios usem o
 * sistema localmente, cada um com suas proprias listas.
 */
public class PersistenciaService {

    private static final String ARQUIVO_DADOS = "dados_usuario.json";

    private final ObjectMapper objectMapper;

    public PersistenciaService() {
        this.objectMapper = JsonUtil.getObjectMapper();
    }

    /**
     * Carrega todos os usuarios salvos no arquivo JSON.
     * Caso o arquivo nao exista, retorna um mapa vazio (novo perfil).
     * Caso o arquivo esteja corrompido, lanca PersistenciaException.
     */
    public Map<String, Usuario> carregarUsuarios() throws PersistenciaException {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(arquivo, new TypeReference<Map<String, Usuario>>() {
            });
        } catch (IOException e) {
            throw new PersistenciaException("Arquivo de dados invalido ou corrompido.", e);
        }
    }

    /**
     * Salva o mapa completo de usuarios no arquivo JSON local.
     */
    public void salvarUsuarios(Map<String, Usuario> usuarios) throws PersistenciaException {
        try {
            objectMapper.writeValue(new File(ARQUIVO_DADOS), usuarios);
        } catch (IOException e) {
            throw new PersistenciaException("Nao foi possivel salvar os dados no arquivo local.", e);
        }
    }

    /**
     * Excecao especifica para falhas de persistencia (leitura/escrita de arquivo).
     */
    public static class PersistenciaException extends Exception {
        public PersistenciaException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }
    }
}
