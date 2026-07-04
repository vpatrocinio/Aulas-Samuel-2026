import com.google.gson.Gson;

import javax.swing.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class PersistenciaJSON {

    private static final Gson gson = new Gson();
    private static final String arquivo = "usuario.json";

    public static void salvarUsuario(Usuario usuario) {

        try (FileWriter writer = new FileWriter(arquivo)) {
            gson.toJson(usuario, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao salvar os dados.");
        }
    }

    public static Usuario carregarUsuario() {

        try (FileReader reader = new FileReader(arquivo)) {
            return gson.fromJson(reader, Usuario.class);
        } catch (IOException e) {
            return null;
        }
    }
}