package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Classe utilitaria responsavel por centralizar a identidade visual do
 * sistema (cores, fontes e estilizacao de componentes Swing), seguindo
 * a paleta de cores definida para o projeto (tema escuro estilo
 * Discord/Notion/Spotify).
 */
public class TemaUtil {

    public static final Color FUNDO_PRINCIPAL = Color.decode("#18122B");
    public static final Color PAINEL = Color.decode("#241B3A");
    public static final Color MENU_LATERAL = Color.decode("#1F1534");
    public static final Color BOTAO = Color.decode("#7C3AED");
    public static final Color BOTAO_HOVER = Color.decode("#8B5CF6");
    public static final Color BOTAO_SECUNDARIO = Color.decode("#5B21B6");
    public static final Color TEXTO = Color.WHITE;
    public static final Color TEXTO_SECUNDARIO = Color.decode("#D1D5DB");
    public static final Color LINHA = Color.decode("#3B2F5C");
    public static final Color CARD = Color.decode("#2D2148");

    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 14);

    private TemaUtil() {
        // Classe utilitaria - nao deve ser instanciada
    }

    /**
     * Aplica o tema escuro padrao em um painel.
     */
    public static void aplicarFundoPainel(JComponent componente) {
        componente.setBackground(PAINEL);
        componente.setOpaque(true);
    }

    /**
     * Cria um botao primario estilizado, com efeito hover.
     */
    public static JButton criarBotaoPrimario(String texto) {
        return criarBotaoBase(texto, BOTAO, BOTAO_HOVER);
    }

    /**
     * Cria um botao secundario estilizado, com efeito hover.
     */
    public static JButton criarBotaoSecundario(String texto) {
        return criarBotaoBase(texto, BOTAO_SECUNDARIO, BOTAO_HOVER);
    }

    private static JButton criarBotaoBase(String texto, Color corBase, Color corHover) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(TEXTO);
        botao.setBackground(corBase);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setBorderPainted(false);

        // Efeito hover simples - muda a cor de fundo ao passar o mouse
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(corBase);
            }
        });
        return botao;
    }

    /**
     * Estiliza um campo de texto seguindo o tema escuro.
     */
    public static void estilizarCampoTexto(JTextField campo) {
        campo.setBackground(CARD);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setFont(FONTE_PADRAO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINHA, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    /**
     * Estiliza uma JTable seguindo o tema escuro, com linhas alternadas.
     */
    public static void estilizarTabela(JTable tabela) {
        tabela.setBackground(PAINEL);
        tabela.setForeground(TEXTO);
        tabela.setFont(FONTE_PADRAO);
        tabela.setRowHeight(28);
        tabela.setGridColor(LINHA);
        tabela.setSelectionBackground(BOTAO_SECUNDARIO);
        tabela.setSelectionForeground(TEXTO);
        tabela.setShowGrid(true);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(MENU_LATERAL);
        header.setForeground(TEXTO);
        header.setFont(FONTE_SUBTITULO.deriveFont(Font.BOLD, 13f));
        header.setReorderingAllowed(false);

        tabela.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? PAINEL : CARD);
                    c.setForeground(TEXTO);
                }
                return c;
            }
        });
    }

    /**
     * Cria um JScrollPane estilizado para combinar com o tema escuro.
     */
    public static JScrollPane criarScrollEstilizado(Component view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(BorderFactory.createLineBorder(LINHA));
        scroll.getViewport().setBackground(PAINEL);
        return scroll;
    }

    /**
     * Centraliza uma janela na tela.
     */
    public static void centralizarJanela(Window janela) {
        janela.setLocationRelativeTo(null);
    }
}
