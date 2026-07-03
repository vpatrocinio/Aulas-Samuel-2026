import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// Fábrica de botões, campos e diálogos padronizados do Swing.

public final class Componentes {

    private Componentes() {}

    public static JButton botao(String texto, Color fundo, Color hover, Color frente) {
        JButton b = new JButton(texto);
        b.setFont(Estilo.NEGRITO);
        b.setForeground(frente);
        b.setBackground(fundo);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addChangeListener(e -> {
            ButtonModel m = b.getModel();
            b.setBackground(m.isRollover() ? hover : fundo);
        });
        return b;
    }

    public static JButton botaoPrimario(String texto) {
        return botao(texto, Estilo.DESTAQUE, Estilo.DESTAQUE_ESC, Estilo.TEXTO_CLARO);
    }

    public static JButton botaoSecundario(String texto) {
        return botao(texto, Estilo.SUPERFICIE, Estilo.SUPERFICIE_ALT, Estilo.TEXTO);
    }

    public static JButton botaoPerigo(String texto) {
        return botao(texto, Estilo.ALERTA, Estilo.ALERTA.darker(), Estilo.TEXTO_CLARO);
    }

    public static JTextField campoTexto(int colunas) {
        JTextField campo = new JTextField(colunas);
        campo.setFont(Estilo.NORMAL);
        campo.setBackground(Estilo.SUPERFICIE);
        campo.setForeground(Estilo.TEXTO);
        campo.setCaretColor(Estilo.TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return campo;
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Não foi possível continuar", JOptionPane.ERROR_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem, String titulo) {
        int opcao = JOptionPane.showConfirmDialog(pai, mensagem, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcao == JOptionPane.YES_OPTION;
    }
}
