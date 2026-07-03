import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

// Painel embutido que mostra os detalhes completos de uma série selecionada.

public class JanelaDetalhes extends JPanel {

    private static final String CARTAO_VAZIO = "vazio";
    private static final String CARTAO_CONTEUDO = "conteudo";

    private final Perfil perfil;
    private final Runnable aoAtualizar;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel raiz = new JPanel(cardLayout);
    private final CartaoSerie ficha = new CartaoSerie();
    private final JPanel infoCorpo = new JPanel();
    private final JTextArea sinopseArea = new JTextArea();
    private final JPanel chipsGeneros = new JPanel(new LayoutQuebraLinha(FlowLayout.LEFT, 6, 6));
    private final JPanel barraBotoes = new JPanel(new GridLayout(1, 3, 8, 0));

    private SerieTV serieAtual;

    public JanelaDetalhes(Perfil perfil, Runnable aoAtualizar) {
        this.perfil = perfil;
        this.aoAtualizar = aoAtualizar;

        setLayout(new BorderLayout());
        setOpaque(false);

        raiz.add(montarVazio(), CARTAO_VAZIO);
        raiz.add(montarConteudo(), CARTAO_CONTEUDO);
        add(raiz, BorderLayout.CENTER);

        cardLayout.show(raiz, CARTAO_VAZIO);
    }

    private JPanel montarVazio() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel msg = new JLabel("Selecione uma série na tabela para ver os detalhes.", JLabel.CENTER);
        msg.setForeground(Estilo.TEXTO_FRACO);
        msg.setFont(Estilo.NORMAL);
        p.add(msg, BorderLayout.CENTER);
        return p;
    }

    private JPanel montarConteudo() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setOpaque(false);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        ficha.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        cabecalho.add(ficha, BorderLayout.NORTH);
        cabecalho.add(chipsGeneros, BorderLayout.SOUTH);
        p.add(cabecalho, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(Estilo.NEGRITO);
        abas.addTab("Informações", montarAbaInformacoes());
        abas.addTab("Sinopse", montarAbaSinopse());
        p.add(abas, BorderLayout.CENTER);

        p.add(barraBotoes, BorderLayout.SOUTH);
        return p;
    }

    private JScrollPane montarAbaInformacoes() {
        infoCorpo.setLayout(new BoxLayout(infoCorpo, BoxLayout.Y_AXIS));
        infoCorpo.setOpaque(false);
        infoCorpo.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
        JScrollPane sp = new JScrollPane(infoCorpo);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        return sp;
    }

    private JScrollPane montarAbaSinopse() {
        sinopseArea.setEditable(false);
        sinopseArea.setLineWrap(true);
        sinopseArea.setWrapStyleWord(true);
        sinopseArea.setOpaque(false);
        sinopseArea.setForeground(Estilo.TEXTO);
        sinopseArea.setFont(Estilo.NORMAL);
        sinopseArea.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
        JScrollPane sp = new JScrollPane(sinopseArea);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        return sp;
    }

    public void exibir(SerieTV serie) {
        this.serieAtual = serie;
        if (serie == null) {
            cardLayout.show(raiz, CARTAO_VAZIO);
            return;
        }
        ficha.exibir(serie);
        preencherChips(serie);
        preencherInformacoes(serie);
        sinopseArea.setText(serie.getSinopse().isBlank() ? "Esta série não possui sinopse cadastrada." : serie.getSinopse());
        sinopseArea.setCaretPosition(0);
        montarBotoesLista();
        cardLayout.show(raiz, CARTAO_CONTEUDO);
    }

    private void preencherChips(SerieTV serie) {
        chipsGeneros.removeAll();
        if (serie.getGeneros().isEmpty()) {
            chipsGeneros.add(chip("Sem gênero informado"));
        } else {
            for (String g : serie.getGeneros()) chipsGeneros.add(chip(g));
        }
        chipsGeneros.revalidate();
        chipsGeneros.repaint();
    }

    private JLabel chip(String texto) {
        JLabel l = new JLabel(texto);
        l.setOpaque(true);
        l.setBackground(Estilo.SUPERFICIE_ALT);
        l.setForeground(Estilo.DESTAQUE_ESC);
        l.setFont(Estilo.PEQUENA);
        l.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return l;
    }

    private void preencherInformacoes(SerieTV serie) {
        infoCorpo.removeAll();
        infoCorpo.add(linha("Nome", serie.getNome()));
        infoCorpo.add(linha("Idioma original", serie.getIdioma().isBlank() ? "Não informado" : serie.getIdioma()));
        infoCorpo.add(linha("Gêneros", serie.generosFormatados()));
        infoCorpo.add(linha("Nota geral", serie.notaFormatada()));
        infoCorpo.add(linha("Estado", serie.estadoLegivel()));
        infoCorpo.add(linha("Data de estreia", serie.getEstreia().isBlank() ? "Desconhecida" : serie.getEstreia()));
        infoCorpo.add(linha("Data de término", serie.getTermino().isBlank() ? "Em exibição / em aberto" : serie.getTermino()));
        infoCorpo.add(linha("Emissora", serie.getEmissora().isBlank() ? "Não informada" : serie.getEmissora()));
        infoCorpo.revalidate();
        infoCorpo.repaint();
    }

    private JPanel linha(String rotulo, String valor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.setAlignmentX(0f);

        JLabel r = new JLabel(rotulo);
        r.setForeground(Estilo.DESTAQUE_ESC);
        r.setFont(Estilo.NEGRITO);
        r.setPreferredSize(new Dimension(140, 20));

        JLabel v = new JLabel(valor);
        v.setForeground(Estilo.TEXTO);
        v.setFont(Estilo.NORMAL);

        p.add(r, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void montarBotoesLista() {
        barraBotoes.removeAll();
        for (CategoriaLista cat : CategoriaLista.values()) {
            boolean jaEsta = perfil.contem(cat, serieAtual);
            String texto = (jaEsta ? "Remover de " : cat.getSimbolo() + " Adicionar a ") + cat.getRotulo();
            JButton btn = jaEsta ? Componentes.botaoSecundario(texto) : Componentes.botaoPrimario(texto);
            btn.addActionListener(e -> alternar(cat));
            barraBotoes.add(btn);
        }
        barraBotoes.revalidate();
        barraBotoes.repaint();
    }

    private void alternar(CategoriaLista cat) {
        if (perfil.contem(cat, serieAtual)) perfil.remover(cat, serieAtual);
        else perfil.adicionar(cat, serieAtual);
        montarBotoesLista();
        if (aoAtualizar != null) aoAtualizar.run();
    }
}
