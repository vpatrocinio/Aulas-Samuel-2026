package telas;

import controller.SistemaController;
import util.IdiomaUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela principal do sistema. Apresenta o menu lateral fixo e a area de
 * conteudo central onde as demais telas sao exibidas. O dashboard inicial
 * mostra cards com os totais de favoritos, assistidas e desejo assistir.
 */
public class TelaPrincipal extends JFrame implements IdiomaUtil.AtualizavelIdioma {

    private final SistemaController controller;

    private JLabel labelBoasVindas;
    private JLabel labelSubtitulo;
    private JButton btnBuscar;
    private JButton btnFavoritos;
    private JButton btnAssistidas;
    private JButton btnDesejo;
    private JButton btnSair;

    private JLabel cardTotalFavoritos;
    private JLabel cardTotalAssistidas;
    private JLabel cardTotalDesejo;

    private JPanel painelCards;
    private JPanel painelMenu;

    public TelaPrincipal(SistemaController controller) {
        this.controller = controller;
        IdiomaUtil.registrarOuvinte(this);
        configurarJanela();
        montarInterface();
    }

    private void configurarJanela() {
        setTitle(IdiomaUtil.get("app.titulo"));
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
        TemaUtil.centralizarJanela(this);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());
    }

    private void montarInterface() {
        add(criarMenuLateral(), BorderLayout.WEST);
        add(criarAreaPrincipal(), BorderLayout.CENTER);
    }

    private JPanel criarMenuLateral() {
        painelMenu = new JPanel();
        painelMenu.setLayout(new BoxLayout(painelMenu, BoxLayout.Y_AXIS));
        painelMenu.setBackground(TemaUtil.MENU_LATERAL);
        painelMenu.setPreferredSize(new Dimension(200, 0));
        painelMenu.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel labelApp = new JLabel("🎬 MySeries");
        labelApp.setFont(TemaUtil.FONTE_SUBTITULO);
        labelApp.setForeground(TemaUtil.TEXTO);
        labelApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelApp.setBorder(new EmptyBorder(0, 0, 25, 0));
        painelMenu.add(labelApp);

        btnBuscar = criarBotaoMenu(IdiomaUtil.get("menu.buscar"));
        btnFavoritos = criarBotaoMenu( IdiomaUtil.get("menu.favoritos"));
        btnAssistidas = criarBotaoMenu(IdiomaUtil.get("menu.assistidas"));
        btnDesejo = criarBotaoMenu(IdiomaUtil.get("menu.desejo"));

        btnBuscar.addActionListener(e -> abrirBusca());
        btnFavoritos.addActionListener(e -> abrirFavoritos());
        btnAssistidas.addActionListener(e -> abrirAssistidas());
        btnDesejo.addActionListener(e -> abrirDesejo());

        painelMenu.add(btnBuscar);
        painelMenu.add(Box.createVerticalStrut(8));
        painelMenu.add(btnFavoritos);
        painelMenu.add(Box.createVerticalStrut(8));
        painelMenu.add(btnAssistidas);
        painelMenu.add(Box.createVerticalStrut(8));
        painelMenu.add(btnDesejo);
        painelMenu.add(Box.createVerticalGlue());

        btnSair = criarBotaoMenu("🚪 " + IdiomaUtil.get("menu.sair"));
        btnSair.setBackground(TemaUtil.BOTAO_SECUNDARIO);
        btnSair.addActionListener(e -> System.exit(0));
        painelMenu.add(btnSair);

        return painelMenu;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = TemaUtil.criarBotaoPrimario(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        area.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Cabecalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(TemaUtil.FUNDO_PRINCIPAL);

        labelBoasVindas = new JLabel(
                IdiomaUtil.get("dashboard.boasVindas", controller.getUsuarioAtual().getNome()));
        labelBoasVindas.setFont(TemaUtil.FONTE_TITULO);
        labelBoasVindas.setForeground(TemaUtil.TEXTO);

        labelSubtitulo = new JLabel(IdiomaUtil.get("dashboard.subtitulo"));
        labelSubtitulo.setFont(TemaUtil.FONTE_PADRAO);
        labelSubtitulo.setForeground(TemaUtil.TEXTO_SECUNDARIO);

        JPanel textosCabecalho = new JPanel(new GridLayout(2, 1, 0, 4));
        textosCabecalho.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        textosCabecalho.add(labelBoasVindas);
        textosCabecalho.add(labelSubtitulo);
        cabecalho.add(textosCabecalho, BorderLayout.WEST);

        area.add(cabecalho, BorderLayout.NORTH);

        // Cards de estatisticas
        painelCards = new JPanel(new GridLayout(1, 3, 20, 0));
        painelCards.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelCards.setBorder(new EmptyBorder(30, 0, 0, 0));

        cardTotalFavoritos = criarCard(IdiomaUtil.get("dashboard.card.favoritos"),
                controller.getFavoritos().size());
        cardTotalAssistidas = criarCard(IdiomaUtil.get("dashboard.card.assistidas"),
                controller.getAssistidas().size());
        cardTotalDesejo = criarCard(IdiomaUtil.get("dashboard.card.desejo"),
                controller.getDesejoAssistir().size());

        painelCards.add(cardTotalFavoritos.getParent());
        painelCards.add(cardTotalAssistidas.getParent());
        painelCards.add(cardTotalDesejo.getParent());

        area.add(painelCards, BorderLayout.CENTER);

        // Rodape
        JLabel rodape = new JLabel(IdiomaUtil.get("rodape.texto"), SwingConstants.CENTER);
        rodape.setFont(TemaUtil.FONTE_PADRAO.deriveFont(11f));
        rodape.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        rodape.setBorder(new EmptyBorder(20, 0, 0, 0));
        area.add(rodape, BorderLayout.SOUTH);

        return area;
    }

    private JLabel criarCard(String titulo, int valor) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5));
        card.setBackground(TemaUtil.CARD);
        card.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TemaUtil.FONTE_PADRAO);
        lblTitulo.setForeground(TemaUtil.TEXTO_SECUNDARIO);

        JLabel lblValor = new JLabel(String.valueOf(valor));
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(TemaUtil.BOTAO_HOVER);

        card.add(lblTitulo);
        card.add(lblValor);
        card.add(new JLabel(""));

        return lblValor;
    }

    /** Atualiza os contadores dos cards do dashboard. */
    public void atualizarContadores() {
        cardTotalFavoritos.setText(String.valueOf(controller.getFavoritos().size()));
        cardTotalAssistidas.setText(String.valueOf(controller.getAssistidas().size()));
        cardTotalDesejo.setText(String.valueOf(controller.getDesejoAssistir().size()));
    }

    private void abrirBusca() {
        new TelaBusca(controller, this).setVisible(true);
    }

    private void abrirFavoritos() {
        new TelaLista(controller, this, TelaLista.TipoLista.FAVORITOS).setVisible(true);
    }

    private void abrirAssistidas() {
        new TelaLista(controller, this, TelaLista.TipoLista.ASSISTIDAS).setVisible(true);
    }

    private void abrirDesejo() {
        new TelaLista(controller, this, TelaLista.TipoLista.DESEJO).setVisible(true);
    }

    @Override
    public void atualizarTextos() {
        setTitle(IdiomaUtil.get("app.titulo"));
        labelBoasVindas.setText(
                IdiomaUtil.get("dashboard.boasVindas", controller.getUsuarioAtual().getNome()));
        labelSubtitulo.setText(IdiomaUtil.get("dashboard.subtitulo"));
        btnBuscar.setText(IdiomaUtil.get("menu.buscar"));
        btnFavoritos.setText(IdiomaUtil.get("menu.favoritos"));
        btnAssistidas.setText(IdiomaUtil.get("menu.assistidas"));
        btnDesejo.setText(IdiomaUtil.get("menu.desejo"));
        btnSair.setText(IdiomaUtil.get("menu.sair"));
    }
}
