import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

// Lê e grava o Perfil em disco no arquivo JSON local.

public class ArquivoPerfil {

    private final Path arquivo;

    public ArquivoPerfil() {
        this.arquivo = Path.of(ConfigEnv.obter("ARQUIVO_DADOS", "teletrack_dados.json"));
    }

    public boolean existe() { return Files.exists(arquivo); }

    public Perfil carregar() throws AppErro {
        if (!Files.exists(arquivo)) return null;
        try {
            String conteudo = Files.readString(arquivo, StandardCharsets.UTF_8); // lê o texto do disco
            if (conteudo.isBlank()) return null;
            Object raiz = JsonUtil.parse(conteudo); // texto -> estrutura genérica (Map/List)
            if (!(raiz instanceof Map)) throw new AppErro("Arquivo de dados em formato inesperado.");
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) raiz;
            return Perfil.deMapa(mapa); // <- aqui vira objeto Java (POJO)
        } catch (JsonParseException e) {
            throw new AppErro("O arquivo de dados está corrompido e não pôde ser lido.", e);
        } catch (IOException e) {
            throw new AppErro("Não foi possível ler o arquivo de dados: " + e.getMessage(), e);
        }
    }

    public void salvar(Perfil perfil) throws AppErro {
        if (perfil == null) return;
        try {
            String json = JsonUtil.stringify(perfil.paraMapa());
            Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
            Files.writeString(temporario, json, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException semSuporteAtomico) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new AppErro("Não foi possível salvar os dados: " + e.getMessage(), e);
        }
    }
}
