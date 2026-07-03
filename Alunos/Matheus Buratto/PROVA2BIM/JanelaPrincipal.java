import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

// Janela principal (Swing): barra de ferramentas, abas de busca/listas, tabela + painel de detalhes.

public class JanelaPrincipal extends JFrame {

    private final ApiTvMaze api;
    private final ArquivoPerfil arquivoPerfil;
    private Perfil perfil;

    private final JTextField campoBusca = new JTextField(20);
    private final JLabel lblUsuario = new JLabel();
    private final JLabel lblStatus = new JLabel(" ");
    private final JTabbedPane abas = new JTabbedPane();

    private AbaListagem abaBusca;
    private AbaListagem abaFavoritos;
    private AbaListagem abaAssistidas;
    private AbaListagem abaQueroAssistir;

    private boolean telaCheia = false;

    public JanelaPrincipal(ArquivoPerfil arquivoPerfil, ApiTvMaze api) {
        super("TeleTrack — Catálogo de séries");
        this.arquivoPerfil = arquivoPerfil;
        this.api = api;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setSize(1180, 760);
        setLocationRelativeTo(null);

        if (!carregarOuIdentificarUsuario()) {
            System.exit(0);
            return;
        }

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FUNDO);
        raiz.add(montarBarraFerramentas(), BorderLayout.NORTH);
        raiz.add(montarAbas(), BorderLayout.CENTER);
        raiz.add(montarRodape(), BorderLayout.SOUTH);
        setContentPane(raiz);

        registrarAtalhos(raiz);
        registrarFechamento();
        atualizarNomeUsuario();
        exibirBusca(List.of());
    }

    private boolean carregarOuIdentificarUsuario() {
        try {
            perfil = arquivoPerfil.carregar();
        } catch (AppErro e) {
            boolean recomecar = Componentes.confirmar(this,
                    e.getMessage() + "\n\nDeseja começar com dados novos?", "Arquivo de dados");
            if (!recomecar) return false;
        }
        if (perfil != null && !perfil.getApelido().isBlank()) return true;
        return pedirApelido();
    }

    private boolean pedirApelido() {
        JDialog dlg = new JDialog((JFrame) null, "Bem-vindo ao TeleTrack", true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(null);
        dlg.setResizable(false);

        JPanel conteudo = new JPanel();
        conteudo.setBackground(Estilo.SUPERFICIE);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 28, 24, 28));

        JLabel titulo = new JLabel("TeleTrack");
        titulo.setFont(Estilo.LOGO);
        titulo.setForeground(Estilo.DESTAQUE);
        titulo.setAlignmentX(0.5f);

        JLabel sub = new JLabel("Como devemos te chamar?");
        sub.setFont(Estilo.NORMAL);
        sub.setForeground(Estilo.TEXTO_FRACO);
        sub.setAlignmentX(0.5f);

        JTextField campo = Componentes.campoTexto(16);
        campo.setMaximumSize(new Dimension(260, 36));
        campo.setAlignmentX(0.5f);

        boolean[] confirmado = {false};
        JButton entrar = Componentes.botaoPrimario("Entrar");
        entrar.setAlignmentX(0.5f);
        Runnable confirmar = () -> {
            if (campo.getText().trim().isEmpty()) {
                Componentes.aviso(dlg, "Informe um nome ou apelido para continuar.");
                return;
            }
            confirmado[0] = true;
            dlg.dispose();
        };
        entrar.addActionListener(e -> confirmar.run());
        campo.addActionListener(e -> confirmar.run());

        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(6));
        conteudo.add(sub);
        conteudo.add(Box.createVerticalStrut(16));
        conteudo.add(campo);
        conteudo.add(Box.createVerticalStrut(16));
        conteudo.add(entrar);

        dlg.setContentPane(conteudo);
        dlg.setVisible(true); // bloqueia até dispose()

        if (!confirmado[0]) return false;
        perfil = (perfil == null) ? new Perfil(campo.getText()) : perfil;
        perfil.setApelido(campo.getText());
        salvar();
        return true;
    }

    private JPanel montarBarraFerramentas() {
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setBackground(Estilo.BARRA_TOPO);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel logo = new JLabel("TeleTrack");
        logo.setFont(Estilo.LOGO);
        logo.setForeground(Estilo.TEXTO_CLARO);
        barra.add(logo, BorderLayout.WEST);

        JPanel meio = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        meio.setOpaque(false);
        campoBusca.setFont(Estilo.NORMAL);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA), BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        campoBusca.addActionListener(e -> executarBusca());
        JButton pesquisar = Componentes.botaoPrimario("Pesquisar");
        pesquisar.addActionListener(e -> executarBusca());
        meio.add(campoBusca);
        meio.add(pesquisar);
        barra.add(meio, BorderLayout.CENTER);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setOpaque(false);
        lblUsuario.setForeground(Estilo.TEXTO_CLARO);
        lblUsuario.setFont(Estilo.NEGRITO);
        JButton trocar = Componentes.botaoSecundario("Trocar apelido");
        trocar.addActionListener(e -> trocarApelido());
        JButton fs = Componentes.botaoSecundario("Tela cheia (F11)");
        fs.addActionListener(e -> alternarTelaCheia());
        direita.add(lblUsuario);
        direita.add(trocar);
        direita.add(fs);
        barra.add(direita, BorderLayout.EAST);

        return barra;
    }

    private JTabbedPane montarAbas() {
        abas.setFont(Estilo.SUBTITULO);
        abas.setBackground(Estilo.FUNDO);

        abaBusca = new AbaListagem(null, perfil, this::aoMudarPerfil);
        abaFavoritos = new AbaListagem(CategoriaLista.FAVORITOS, perfil, this::aoMudarPerfil);
        abaAssistidas = new AbaListagem(CategoriaLista.ASSISTIDAS, perfil, this::aoMudarPerfil);
        abaQueroAssistir = new AbaListagem(CategoriaLista.QUERO_ASSISTIR, perfil, this::aoMudarPerfil);

        abas.addTab("Buscar", abaBusca);
        abas.addTab(CategoriaLista.FAVORITOS.getSimbolo() + " " + CategoriaLista.FAVORITOS.getRotulo(), abaFavoritos);
        abas.addTab(CategoriaLista.ASSISTIDAS.getSimbolo() + " " + CategoriaLista.ASSISTIDAS.getRotulo(), abaAssistidas);
        abas.addTab(CategoriaLista.QUERO_ASSISTIR.getSimbolo() + " " + CategoriaLista.QUERO_ASSISTIR.getRotulo(), abaQueroAssistir);

        abas.addChangeListener(e -> {
            AbaListagem atual = (AbaListagem) abas.getSelectedComponent();
            if (atual != abaBusca) atual.recarregarDaLista();
            atualizarStatus(atual);
        });
        return abas;
    }

    private JPanel montarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        rodape.setBackground(Estilo.FUNDO);
        lblStatus.setForeground(Estilo.TEXTO_FRACO);
        lblStatus.setFont(Estilo.PEQUENA);
        rodape.add(lblStatus);
        return rodape;
    }

    private void registrarAtalhos(JComponent raiz) {
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "telaCheia");
        raiz.getActionMap().put("telaCheia", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { alternarTelaCheia(); }
        });
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "sairTelaCheia");
        raiz.getActionMap().put("sairTelaCheia", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { if (telaCheia) alternarTelaCheia(); }
        });
    }

    private void registrarFechamento() {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                salvar();
                dispose();
                System.exit(0);
            }
        });
    }

    private void executarBusca() {
        String termo = campoBusca.getText();
        if (termo == null || termo.isBlank()) {
            Componentes.aviso(this, "Digite o nome de uma série para pesquisar.");
            return;
        }
        abas.setSelectedComponent(abaBusca);
        lblStatus.setText("Buscando \"" + termo.trim() + "\"...");

        new SwingWorker<List<SerieTV>, Void>() {
            @Override protected List<SerieTV> doInBackground() throws AppErro {
                return api.buscarPorNome(termo);
            }
            @Override protected void done() {
                try {
                    List<SerieTV> resultado = get();
                    exibirBusca(resultado);
                    lblStatus.setText(resultado.isEmpty()
                            ? "Nenhuma série encontrada para \"" + termo.trim() + "\"."
                            : resultado.size() + " série(s) encontrada(s).");
                } catch (java.util.concurrent.ExecutionException ex) {
                    String msg = (ex.getCause() instanceof AppErro) ? ex.getCause().getMessage()
                            : "Ocorreu um erro inesperado durante a busca.";
                    lblStatus.setText("Falha na busca.");
                    Componentes.erro(JanelaPrincipal.this, msg);
                } catch (Exception ex) {
                    lblStatus.setText("Falha na busca.");
                    Componentes.erro(JanelaPrincipal.this, "Ocorreu um erro inesperado durante a busca.");
                }
            }
        }.execute();
    }

    private void exibirBusca(List<SerieTV> resultado) {
        abaBusca.definirDados(resultado);
    }

    private void aoMudarPerfil() {
        salvar();
        for (AbaListagem aba : List.of(abaFavoritos, abaAssistidas, abaQueroAssistir)) {
            if (aba == abas.getSelectedComponent()) aba.recarregarDaLista();
        }
        atualizarStatus((AbaListagem) abas.getSelectedComponent());
    }

    private void atualizarStatus(AbaListagem aba) {
        lblStatus.setText(aba.getNomeAba() + ": " + aba.getQuantidade() + " série(s).");
    }

    private void trocarApelido() {
        String atual = perfil.getApelido();
        String novo = JOptionPane.showInputDialog(this, "Como devemos te chamar?", atual);
        if (novo == null) return;
        if (novo.trim().isEmpty()) {
            Componentes.aviso(this, "O apelido não pode ficar em branco.");
            return;
        }
        perfil.setApelido(novo);
        atualizarNomeUsuario();
        salvar();
    }

    private void atualizarNomeUsuario() {
        String nome = perfil.getApelido();
        lblUsuario.setText("Olá, " + (nome.isBlank() ? "visitante" : nome));
    }

    private void alternarTelaCheia() {
        telaCheia = !telaCheia;
        dispose();
        setUndecorated(telaCheia);
        if (telaCheia) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
            setSize(1180, 760);
            setLocationRelativeTo(null);
        }
        setVisible(true);
    }

    private void salvar() {
        try {
            arquivoPerfil.salvar(perfil);
        } catch (AppErro e) {
            Componentes.erro(this, e.getMessage());
        }
    }

    private static class AbaListagem extends JPanel {
        private final CategoriaLista categoria; // null = aba de busca
        private final Perfil perfil;
        private final TabelaSeriesModel modelo = new TabelaSeriesModel();
        private final JTable tabela = new JTable(modelo);
        private final JanelaDetalhes detalhes;
        private List<SerieTV> dados = new ArrayList<>();

        AbaListagem(CategoriaLista categoria, Perfil perfil, Runnable aoAtualizarPerfil) {
            this.categoria = categoria;
            this.perfil = perfil;
            this.detalhes = new JanelaDetalhes(perfil, aoAtualizarPerfil);
            montarLayout();
            if (categoria != null) recarregarDaLista();
        }

        String getNomeAba() { return categoria == null ? "Busca" : categoria.getRotulo(); }

        int getQuantidade() { return dados.size(); }

        void recarregarDaLista() {
            if (categoria != null) definirDados(perfil.getLista(categoria));
        }

        void definirDados(List<SerieTV> lista) {
            this.dados = new ArrayList<>(lista);
            modelo.definir(dados);
            detalhes.exibir(null);
        }

        private void montarLayout() {
            setLayout(new BorderLayout(0, 10));
            setBackground(Estilo.FUNDO);
            setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

            JPanel topoAba = new JPanel(new BorderLayout());
            topoAba.setOpaque(false);
            JLabel lblOrdenar = new JLabel("Ordenar por:");
            lblOrdenar.setForeground(Estilo.TEXTO_FRACO);
            lblOrdenar.setFont(Estilo.PEQUENA);
            JComboBox<OrdemLista> comboOrdem = new JComboBox<>(OrdemLista.values());
            comboOrdem.setFont(Estilo.PEQUENA);
            comboOrdem.addActionListener(e -> {
                OrdemLista criterio = (OrdemLista) comboOrdem.getSelectedItem();
                if (criterio != null) {
                    dados.sort(criterio.comparador());
                    modelo.definir(dados);
                }
            });
            JPanel direitaTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            direitaTopo.setOpaque(false);
            direitaTopo.add(lblOrdenar);
            direitaTopo.add(comboOrdem);
            topoAba.add(direitaTopo, BorderLayout.EAST);
            add(topoAba, BorderLayout.NORTH);

            tabela.setFont(Estilo.NORMAL);
            tabela.setRowHeight(28);
            tabela.setBackground(Estilo.SUPERFICIE);
            tabela.setForeground(Estilo.TEXTO);
            tabela.setGridColor(Estilo.BORDA);
            tabela.setSelectionBackground(Estilo.SUPERFICIE_ALT);
            tabela.setSelectionForeground(Estilo.TEXTO);
            tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabela.getTableHeader().setFont(Estilo.NEGRITO);
            tabela.getSelectionModel().addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) return;
                int linha = tabela.getSelectedRow();
                detalhes.exibir(linha >= 0 && linha < dados.size() ? dados.get(linha) : null);
            });

            JScrollPane tabelaScroll = new JScrollPane(tabela);
            tabelaScroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));

            JPanel painelDetalhes = new JPanel(new BorderLayout());
            painelDetalhes.setBackground(Estilo.SUPERFICIE);
            painelDetalhes.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.BORDA),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)));
            painelDetalhes.add(detalhes, BorderLayout.CENTER);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabelaScroll, painelDetalhes);
            split.setResizeWeight(0.55);
            split.setDividerSize(6);
            split.setBorder(null);
            split.setOpaque(false);
            add(split, BorderLayout.CENTER);
        }
    }

    private static class TabelaSeriesModel extends AbstractTableModel {
        private static final String[] COLUNAS = {"Nome", "Estado", "Nota", "Estreia", "Gêneros"};
        private List<SerieTV> linhas = new ArrayList<>();

        void definir(List<SerieTV> novasLinhas) {
            this.linhas = novasLinhas;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return linhas.size(); }
        @Override public int getColumnCount() { return COLUNAS.length; }
        @Override public String getColumnName(int col) { return COLUNAS[col]; }

        @Override
        public Object getValueAt(int linha, int coluna) {
            SerieTV s = linhas.get(linha);
            return switch (coluna) {
                case 0 -> s.getNome();
                case 1 -> s.estadoLegivel();
                case 2 -> "★ " + s.notaFormatada();
                case 3 -> s.getEstreia().isBlank() ? "—" : s.getEstreia();
                case 4 -> s.generosFormatados();
                default -> "";
            };
        }
    }
}
