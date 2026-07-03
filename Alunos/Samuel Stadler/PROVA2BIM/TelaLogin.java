import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
public class TelaLogin extends JFrame {

    private JTextField campoNome;

    public TelaLogin() {
        super("Series Samuel - Login");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 180);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JLabel titulo = new JLabel("Bem-vindo a Series Samuel!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel painelCentro = new JPanel(new FlowLayout());
        painelCentro.add(new JLabel("Digite seu nome ou apelido:"));
        campoNome = new JTextField(18);
        painelCentro.add(campoNome);

        JButton botaoEntrar = new JButton("Entrar");
        botaoEntrar.addActionListener(this::onEntrar);
        campoNome.addActionListener(this::onEntrar);

        JPanel painelBotao = new JPanel(new FlowLayout());
        painelBotao.add(botaoEntrar);

        add(titulo, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    private void onEntrar(ActionEvent e) {
        String nome = campoNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, digite um nome.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DadosManager dadosManager = new DadosManager();
            Usuario usuario = dadosManager.carregar(nome);

            TelaPrincipal telaPrincipal = new TelaPrincipal(usuario);
            telaPrincipal.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            // O sistema nao pode fechar inesperadamente, entao qualquer erro
            // ao ler o arquivo de dados e mostrado numa caixa de dialogo.
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar os dados do usuario:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
