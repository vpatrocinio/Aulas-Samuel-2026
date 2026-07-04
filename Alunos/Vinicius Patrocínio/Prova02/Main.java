import javax.swing.*;
public class Main {
    public static void main(String[] args) {

        Usuario usuario = PersistenciaJSON.carregarUsuario();

        // Se não existir usuário salvo, pergunta o nome
        if (usuario == null) {
            String nome = JOptionPane.showInputDialog(
                    null,
                    "Digite seu nome:",
                    "Bem-vindo ao SeriesMax",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (nome == null || nome.isBlank()) {
                nome = "Usuário";
            }
            usuario = new Usuario(nome);
            PersistenciaJSON.salvarUsuario(usuario);
        }

        final Usuario usuarioFinal = usuario;
        SwingUtilities.invokeLater(() -> new Tela(usuarioFinal));
    }
}