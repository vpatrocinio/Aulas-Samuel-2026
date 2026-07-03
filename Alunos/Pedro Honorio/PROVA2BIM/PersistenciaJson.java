import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class PersistenciaJson {
    private static final String ARQUIVO = "usuario.json";
    private final ObjectMapper mapper;

    public PersistenciaJson() {
        this.mapper = new ObjectMapper();

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public boolean arquivoExiste() {
        return new File(ARQUIVO).exists();
    }

    public void salvarUsuario(Usuario usuario) {
        try {
            mapper.writeValue(new File(ARQUIVO), usuario);
        } catch (IOException e) {
            System.err.println("Erro ao salvar JSON com Jackson: " + e.getMessage());
        }
    }

    public Usuario carregarUsuario() {
        try {
            File arquivo = new File(ARQUIVO);
            if (!arquivo.exists()) return null;
            return mapper.readValue(arquivo, Usuario.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar JSON com Jackson: " + e.getMessage());
            return null;
        }
    }
}
