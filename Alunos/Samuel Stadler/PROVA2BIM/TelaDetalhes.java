import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

/**
 * Janela (dialogo) que mostra todos os detalhes de uma serie:
 * nome, idioma, generos, nota, estado, datas e emissora.
 */
public class TelaDetalhes extends JDialog {

    public TelaDetalhes(Window owner, Serie serie) {
        super(owner, "Detalhes da Serie", ModalityType.APPLICATION_MODAL);
        montarTela(serie);
    }

    private void montarTela(Serie serie) {
        setSize(420, 380);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        JPanel painel = new JPanel(new GridLayout(0, 1, 5, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel labelNome = new JLabel("Nome: " + nvl(serie.getNome()));
        labelNome.setFont(new Font("Arial", Font.BOLD, 14));

        painel.add(labelNome);
        painel.add(new JLabel("Idioma: " + nvl(serie.getIdioma())));
        painel.add(new JLabel("Generos: " + serie.getGenerosTexto()));
        painel.add(new JLabel("Nota geral: " + (serie.getNota() > 0 ? serie.getNota() : "Sem avaliacao")));
        painel.add(new JLabel("Estado: " + nvl(serie.getEstado())));
        painel.add(new JLabel("Data de estreia: " + nvl(serie.getDataEstreia())));
        painel.add(new JLabel("Data de termino: " + nvl(serie.getDataTermino())));
        painel.add(new JLabel("Emissora: " + nvl(serie.getEmissora())));

        JButton botaoFechar = new JButton("Fechar");
        botaoFechar.addActionListener(e -> dispose());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(botaoFechar);

        add(new JScrollPane(painel), BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    private String nvl(String texto) {
        return (texto == null || texto.isEmpty()) ? "Nao informado" : texto;
    }
}
