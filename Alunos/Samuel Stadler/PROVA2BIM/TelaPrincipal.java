import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela principal do sistema. Tem 4 abas: Buscar, Favoritos,
 * Series Assistidas e Quero Assistir.
 */
public class TelaPrincipal extends JFrame {

    private final Usuario usuario;
    private final TVMazeAPI api;
    private final DadosManager dadosManager;

    // ----- aba de busca -----
    private JTextField campoBusca;
    private DefaultListModel<Serie> modeloBusca;
    private JList<Serie> listaBusca;

    // ----- aba favoritos -----
    private DefaultListModel<Serie> modeloFavoritos;
    private JList<Serie> listaFavoritos;
    private JComboBox<String> comboOrdemFavoritos;

    // ----- aba assistidas -----
    private DefaultListModel<Serie> modeloAssistidas;
    private JList<Serie> listaAssistidas;
    private JComboBox<String> comboOrdemAssistidas;

    // ----- aba quero assistir -----
    private DefaultListModel<Serie> modeloQueroAssistir;
    private JList<Serie> listaQueroAssistir;
    private JComboBox<String> comboOrdemQueroAssistir;

    private static final String[] OPCOES_ORDEM = {
            "Ordem alfabetica", "Nota geral", "Estado", "Data de estreia"
    };

    public TelaPrincipal(Usuario usuario) {
        super("Series Samuel - Usuario: " + usuario.getNome());
        this.usuario = usuario;
        this.api = new TVMazeAPI();
        this.dadosManager = new DadosManager();

        montarTela();
        atualizarTodasAsListas();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDados();
                dispose();
                System.exit(0);
            }
        });

        JLabel labelUsuario = new JLabel("Usuario logado: " + usuario.getNome(), SwingConstants.LEFT);
        labelUsuario.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        labelUsuario.setFont(new Font("Arial", Font.BOLD, 13));

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Buscar Series", criarAbaBusca());
        abas.addTab("Favoritos", criarAbaFavoritos());
        abas.addTab("Series Assistidas", criarAbaAssistidas());
        abas.addTab("Quero Assistir", criarAbaQueroAssistir());

        setLayout(new BorderLayout());
        add(labelUsuario, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
    }

    // ===================== ABA: BUSCAR =====================

    private JPanel criarAbaBusca() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoBusca = new JTextField(25);
        JButton botaoBuscar = new JButton("Buscar");
        painelTopo.add(new JLabel("Nome da serie:"));
        painelTopo.add(campoBusca);
        painelTopo.add(botaoBuscar);

        modeloBusca = new DefaultListModel<>();
        listaBusca = new JList<>(modeloBusca);
        listaBusca.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adicionarDuploCliqueDetalhes(listaBusca);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoDetalhes = new JButton("Ver Detalhes");
        JButton botaoFavorito = new JButton("Adicionar aos Favoritos");
        JButton botaoAssistida = new JButton("Marcar como Assistida");
        JButton botaoQuero = new JButton("Adicionar a Quero Assistir");
        painelBotoes.add(botaoDetalhes);
        painelBotoes.add(botaoFavorito);
        painelBotoes.add(botaoAssistida);
        painelBotoes.add(botaoQuero);

        botaoBuscar.addActionListener(e -> realizarBusca());
        campoBusca.addActionListener(e -> realizarBusca());

        botaoDetalhes.addActionListener(e -> mostrarDetalhesSelecionado(listaBusca));

        botaoFavorito.addActionListener(e -> {
            Serie selecionada = listaBusca.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            if (usuario.adicionarFavorito(selecionada)) {
                salvarDados();
                atualizarListaFavoritos();
                avisar("Serie adicionada aos favoritos!");
            } else {
                avisar("Essa serie ja esta nos favoritos.");
            }
        });

        botaoAssistida.addActionListener(e -> {
            Serie selecionada = listaBusca.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            if (usuario.adicionarAssistida(selecionada)) {
                salvarDados();
                atualizarListaAssistidas();
                avisar("Serie adicionada como assistida!");
            } else {
                avisar("Essa serie ja esta na lista de assistidas.");
            }
        });

        botaoQuero.addActionListener(e -> {
            Serie selecionada = listaBusca.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            if (usuario.adicionarQueroAssistir(selecionada)) {
                salvarDados();
                atualizarListaQueroAssistir();
                avisar("Serie adicionada a lista 'quero assistir'!");
            } else {
                avisar("Essa serie ja esta na lista 'quero assistir'.");
            }
        });

        painel.add(painelTopo, BorderLayout.NORTH);
        painel.add(new JScrollPane(listaBusca), BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private void realizarBusca() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            avisar("Digite o nome de uma serie para buscar.");
            return;
        }

        modeloBusca.clear();

        // Faz a busca em outra thread para nao travar a tela
        new Thread(() -> {
            try {
                List<Serie> resultado = api.buscarSeriesPorNome(termo);

                SwingUtilities.invokeLater(() -> {
                    modeloBusca.clear();
                    if (resultado.isEmpty()) {
                        avisar("Nenhuma serie encontrada com esse nome.");
                    }
                    for (Serie s : resultado) {
                        modeloBusca.addElement(s);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(TelaPrincipal.this,
                                "Erro ao buscar series. Verifique sua conexao com a internet.\n\nDetalhes: " + ex.getMessage(),
                                "Erro na busca", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // ===================== ABA: FAVORITOS =====================

    private JPanel criarAbaFavoritos() {
        modeloFavoritos = new DefaultListModel<>();
        listaFavoritos = new JList<>(modeloFavoritos);
        adicionarDuploCliqueDetalhes(listaFavoritos);

        comboOrdemFavoritos = new JComboBox<>(OPCOES_ORDEM);

        JButton botaoDetalhes = new JButton("Ver Detalhes");
        botaoDetalhes.addActionListener(e -> mostrarDetalhesSelecionado(listaFavoritos));

        JButton botaoRemover = new JButton("Remover dos Favoritos");
        botaoRemover.addActionListener(e -> {
            Serie selecionada = listaFavoritos.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            usuario.removerFavorito(selecionada);
            salvarDados();
            atualizarListaFavoritos();
        });

        comboOrdemFavoritos.addActionListener(e -> atualizarListaFavoritos());

        return montarPainelDeLista(listaFavoritos, comboOrdemFavoritos, botaoDetalhes, botaoRemover);
    }

    // ===================== ABA: ASSISTIDAS =====================

    private JPanel criarAbaAssistidas() {
        modeloAssistidas = new DefaultListModel<>();
        listaAssistidas = new JList<>(modeloAssistidas);
        adicionarDuploCliqueDetalhes(listaAssistidas);

        comboOrdemAssistidas = new JComboBox<>(OPCOES_ORDEM);

        JButton botaoDetalhes = new JButton("Ver Detalhes");
        botaoDetalhes.addActionListener(e -> mostrarDetalhesSelecionado(listaAssistidas));

        JButton botaoRemover = new JButton("Remover das Assistidas");
        botaoRemover.addActionListener(e -> {
            Serie selecionada = listaAssistidas.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            usuario.removerAssistida(selecionada);
            salvarDados();
            atualizarListaAssistidas();
        });

        comboOrdemAssistidas.addActionListener(e -> atualizarListaAssistidas());

        return montarPainelDeLista(listaAssistidas, comboOrdemAssistidas, botaoDetalhes, botaoRemover);
    }

    // ===================== ABA: QUERO ASSISTIR =====================

    private JPanel criarAbaQueroAssistir() {
        modeloQueroAssistir = new DefaultListModel<>();
        listaQueroAssistir = new JList<>(modeloQueroAssistir);
        adicionarDuploCliqueDetalhes(listaQueroAssistir);

        comboOrdemQueroAssistir = new JComboBox<>(OPCOES_ORDEM);

        JButton botaoDetalhes = new JButton("Ver Detalhes");
        botaoDetalhes.addActionListener(e -> mostrarDetalhesSelecionado(listaQueroAssistir));

        JButton botaoRemover = new JButton("Remover da Lista");
        botaoRemover.addActionListener(e -> {
            Serie selecionada = listaQueroAssistir.getSelectedValue();
            if (selecionada == null) {
                avisar("Selecione uma serie na lista primeiro.");
                return;
            }
            usuario.removerQueroAssistir(selecionada);
            salvarDados();
            atualizarListaQueroAssistir();
        });

        comboOrdemQueroAssistir.addActionListener(e -> atualizarListaQueroAssistir());

        return montarPainelDeLista(listaQueroAssistir, comboOrdemQueroAssistir, botaoDetalhes, botaoRemover);
    }

    // ===================== PAINEL PADRAO PARA AS LISTAS =====================

    private JPanel montarPainelDeLista(JList<Serie> lista, JComboBox<String> comboOrdem,
                                        JButton botaoDetalhes, JButton botaoRemover) {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.add(new JLabel("Ordenar por:"));
        painelTopo.add(comboOrdem);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.add(botaoDetalhes);
        painelBotoes.add(botaoRemover);

        painel.add(painelTopo, BorderLayout.NORTH);
        painel.add(new JScrollPane(lista), BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    // ===================== METODOS DE ATUALIZACAO DAS LISTAS =====================

    private void atualizarTodasAsListas() {
        atualizarListaFavoritos();
        atualizarListaAssistidas();
        atualizarListaQueroAssistir();
    }

    private void atualizarListaFavoritos() {
        List<Serie> lista = new ArrayList<>(usuario.getFavoritos());
        ordenarConformeCombo(lista, comboOrdemFavoritos);
        recarregarModelo(modeloFavoritos, lista);
    }

    private void atualizarListaAssistidas() {
        List<Serie> lista = new ArrayList<>(usuario.getAssistidas());
        ordenarConformeCombo(lista, comboOrdemAssistidas);
        recarregarModelo(modeloAssistidas, lista);
    }

    private void atualizarListaQueroAssistir() {
        List<Serie> lista = new ArrayList<>(usuario.getQueroAssistir());
        ordenarConformeCombo(lista, comboOrdemQueroAssistir);
        recarregarModelo(modeloQueroAssistir, lista);
    }

    private void ordenarConformeCombo(List<Serie> lista, JComboBox<String> combo) {
        if (combo == null) {
            return;
        }
        String opcao = (String) combo.getSelectedItem();
        if (opcao == null) {
            return;
        }
        switch (opcao) {
            case "Ordem alfabetica":
                OrdenadorSeries.ordenarPorNome(lista);
                break;
            case "Nota geral":
                OrdenadorSeries.ordenarPorNota(lista);
                break;
            case "Estado":
                OrdenadorSeries.ordenarPorEstado(lista);
                break;
            case "Data de estreia":
                OrdenadorSeries.ordenarPorDataEstreia(lista);
                break;
            default:
                break;
        }
    }

    private void recarregarModelo(DefaultListModel<Serie> modelo, List<Serie> lista) {
        modelo.clear();
        for (Serie s : lista) {
            modelo.addElement(s);
        }
    }

    // ===================== METODOS AUXILIARES =====================

    private void adicionarDuploCliqueDetalhes(JList<Serie> lista) {
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetalhesSelecionado(lista);
                }
            }
        });
    }

    private void mostrarDetalhesSelecionado(JList<Serie> lista) {
        Serie selecionada = lista.getSelectedValue();
        if (selecionada == null) {
            avisar("Selecione uma serie na lista primeiro.");
            return;
        }
        TelaDetalhes tela = new TelaDetalhes(this, selecionada);
        tela.setVisible(true);
    }

    private void salvarDados() {
        try {
            dadosManager.salvar(usuario);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Nao foi possivel salvar os dados:\n" + ex.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void avisar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
}
