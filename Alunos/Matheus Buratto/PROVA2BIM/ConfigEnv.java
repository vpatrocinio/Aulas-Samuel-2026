import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// Lê configurações opcionais de um arquivo .env (ex: URL da API, nome do arquivo de dados).

public final class ConfigEnv {

    private static Map<String, String> valores;

    private ConfigEnv() {}

    public static String obter(String chave, String padrao) {
        String v = mapa().get(chave);
        return (v == null || v.isBlank()) ? padrao : v;
    }

    private static synchronized Map<String, String> mapa() {
        if (valores != null) return valores;
        valores = new HashMap<>();
        for (Path candidato : new Path[]{Path.of(".env"), Path.of("SeriesApp", ".env")}) {
            if (Files.exists(candidato)) {
                lerArquivo(candidato);
                break;
            }
        }
        return valores;
    }

    private static void lerArquivo(Path caminho) {
        try {
            for (String linha : Files.readAllLines(caminho)) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#") || !linha.contains("=")) continue;
                int eq = linha.indexOf('=');
                String chave = linha.substring(0, eq).trim();
                String valor = linha.substring(eq + 1).trim().replaceAll("^['\"]|['\"]$", "");
                valores.put(chave, valor);
            }
        } catch (IOException ignorado) {

        }
    }
}
