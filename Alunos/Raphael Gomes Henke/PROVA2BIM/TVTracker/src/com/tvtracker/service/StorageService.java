package com.tvtracker.service;

import com.tvtracker.json.JsonParser;

//cria o json
import com.tvtracker.json.JsonWriter;


import com.tvtracker.model.UserData;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Persiste os dados de cada usuário em um arquivo JSON local,
 * dentro da pasta "tvtracker_data", garantindo que os dados
 * sejam mantidos entre execuções do programa.
 */

public class StorageService {

    private static final String DATA_DIR = "tvtracker_data";

    public StorageService() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Não foi possível criar o diretório de dados: " + e.getMessage());
        }
    }

    private String sanitize(String username) {
        return username.trim().replaceAll("[^a-zA-Z0-9_\\-À-ÿ ]", "_");
    }

    private Path fileFor(String username) {
        return Paths.get(DATA_DIR, sanitize(username) + ".json");
    }



    // Localiza o arquivo do usuário

    // Se o arquivo não existir
    // cria um novo UserData

    // Se existir
    // lê o arquivo JSON

    // Converte o JSON em Map

    // Desserializa para UserData

    // Retorna o objeto
    /** Carrega os dados salvos do usuário, ou cria um novo perfil vazio se não existir. */
    public UserData loadOrCreate(String username) {
        Path path = fileFor(username);
        if (Files.exists(path)) {
            try {
                String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                if (!content.trim().isEmpty()) {
                    Object parsed = JsonParser.parse(content);
                    if (parsed instanceof Map) {
                        @SuppressWarnings("unchecked")
                        UserData ud = UserData.fromJson((Map<String, Object>) parsed);
                        if (ud.getUsername() == null || ud.getUsername().isEmpty()) {
                            ud.setUsername(username);
                        }
                        return ud;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao ler dados salvos, iniciando novo perfil: " + e.getMessage());
            }
        }
        return new UserData(username);
    }






    /*
    //////////////////////////////

    SERIALIZAÇÃO

    //////////////////////////////
     */
    /** Salva os dados do usuário em disco, em formato JSON. */
    public void save(UserData data) throws IOException {
        Path path = fileFor(data.getUsername());
        String json = JsonWriter.write(data.toJson());
        Files.write(path, json.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** Lista os nomes de usuário que já possuem dados salvos localmente. */

    public List<String> listUsers() {
        List<String> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                String n = f.getName();
                users.add(n.substring(0, n.length() - 5));
            }
        }
        return users;
    }
}
