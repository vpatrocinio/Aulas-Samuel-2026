package fag;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

// Classe responsável por toda a interface gráfica usando Java Swing.
public class AppFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private DadosUsuario dados;
    private TvMazeService tvMazeService;
    private JsonStorage jsonStorage;

    private JTextField campoApelido;
    private JTextField campoBusca;

    private DefaultListModel<Serie> modeloResultados;
    private DefaultListModel<Serie> modeloFavoritos;
    private DefaultListModel<Serie> modeloAssistidas;
    private DefaultListModel<Serie> modeloQueroAssistir;

    private JList<Serie> listaResultados;
    private JList<Serie> listaFavoritos;
    private JList<Serie> listaAssistidas;
    private JList<Serie> listaQueroAssistir;

    private JTextArea areaDetalhes;

    public AppFrame() {
        tvMazeService = new TvMazeService();
        jsonStorage = new JsonStorage();

        // Carrega os dados salvos no JSON ao abrir o sistema.
        try {
            dados = jsonStorage.carregar();
        } catch (AppException e) {
            dados = new DadosUsuario();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        configurarJanela();
        montarTela();
        atualizarTodasAsListas();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("SeriesHub - Acompanhamento de Séries");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(950, 620));
    }

    private void montarTela() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelPrincipal.setBackground(new Color(30, 32, 38));

        painelPrincipal.add(criarTopo(), BorderLayout.NORTH);
        painelPrincipal.add(criarCentro(), BorderLayout.CENTER);
        painelPrincipal.add(criarRodape(), BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JPanel criarTopo() {
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(new Color(30, 32, 38));

        JLabel titulo = new JLabel("SeriesHub", JLabel.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel subtitulo = new JLabel("Sistema local para buscar, organizar e acompanhar séries de TV", JLabel.CENTER);
        subtitulo.setForeground(new Color(190, 190, 190));
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel painelTitulo = new JPanel(new GridLayout(2, 1));
        painelTitulo.setBackground(new Color(30, 32, 38));
        painelTitulo.add(titulo);
        painelTitulo.add(subtitulo);

        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelUsuario.setBackground(new Color(42, 45, 54));
        painelUsuario.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel labelUsuario = new JLabel("Apelido do usuário:");
        labelUsuario.setForeground(Color.WHITE);

        campoApelido = new JTextField(dados.getApelido(), 18);

        // BOTÃO: salva o apelido do usuário no JSON.
        JButton botaoSalvarUsuario = new JButton("Salvar usuário");
        botaoSalvarUsuario.addActionListener(e -> salvarUsuario());

        painelUsuario.add(labelUsuario);
        painelUsuario.add(campoApelido);
        painelUsuario.add(botaoSalvarUsuario);

        painelTopo.add(painelTitulo, BorderLayout.NORTH);
        painelTopo.add(painelUsuario, BorderLayout.SOUTH);

        return painelTopo;
    }

    private JPanel criarCentro() {
        JPanel painelCentro = new JPanel(new BorderLayout(15, 15));
        painelCentro.setBackground(new Color(30, 32, 38));

        painelCentro.add(criarPainelBusca(), BorderLayout.NORTH);
        painelCentro.add(criarPainelListas(), BorderLayout.CENTER);
        painelCentro.add(criarPainelDetalhes(), BorderLayout.EAST);

        return painelCentro;
    }

    private JPanel criarPainelBusca() {
        JPanel painelBusca = new JPanel(new BorderLayout(10, 10));
        painelBusca.setBackground(new Color(42, 45, 54));
        painelBusca.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel labelBusca = new JLabel("Buscar série pelo nome:");
        labelBusca.setForeground(Color.WHITE);

        campoBusca = new JTextField();

        // BOTÃO: faz a busca na API TVMaze.
        JButton botaoBuscar = new JButton("Buscar na TVMaze");
        botaoBuscar.addActionListener(e -> buscarSeries());

        // BOTÃO: limpa o campo de busca e os resultados.
        JButton botaoLimparBusca = new JButton("Limpar");
        botaoLimparBusca.addActionListener(e -> limparBusca());

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 8, 8));
        painelBotoes.setBackground(new Color(42, 45, 54));
        painelBotoes.add(botaoBuscar);
        painelBotoes.add(botaoLimparBusca);

        painelBusca.add(labelBusca, BorderLayout.WEST);
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(painelBotoes, BorderLayout.EAST);

        return painelBusca;
    }

    private JPanel criarPainelListas() {
        JPanel painelListas = new JPanel(new GridLayout(1, 2, 15, 15));
        painelListas.setBackground(new Color(30, 32, 38));

        painelListas.add(criarPainelResultados());
        painelListas.add(criarPainelMinhasListas());

        return painelListas;
    }

    private JPanel criarPainelResultados() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBackground(new Color(42, 45, 54));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Resultados da busca");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        modeloResultados = new DefaultListModel<>();
        listaResultados = new JList<>(modeloResultados);
        listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ao selecionar uma série nos resultados, os detalhes aparecem à direita.
        listaResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhes(listaResultados.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(listaResultados);

        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 8, 8));
        painelBotoes.setBackground(new Color(42, 45, 54));

        // BOTÕES: adicionam a série selecionada em cada lista.
        JButton botaoFavorito = new JButton("Adicionar aos favoritos");
        JButton botaoAssistida = new JButton("Adicionar às assistidas");
        JButton botaoQuero = new JButton("Adicionar ao quero assistir");

        botaoFavorito.addActionListener(e -> adicionarNaLista(dados.getFavoritos(), listaResultados.getSelectedValue(), "favoritos"));
        botaoAssistida.addActionListener(e -> adicionarNaLista(dados.getAssistidas(), listaResultados.getSelectedValue(), "assistidas"));
        botaoQuero.addActionListener(e -> adicionarNaLista(dados.getQueroAssistir(), listaResultados.getSelectedValue(), "quero assistir"));

        painelBotoes.add(botaoFavorito);
        painelBotoes.add(botaoAssistida);
        painelBotoes.add(botaoQuero);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelMinhasListas() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBackground(new Color(42, 45, 54));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Minhas listas");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        modeloFavoritos = new DefaultListModel<>();
        modeloAssistidas = new DefaultListModel<>();
        modeloQueroAssistir = new DefaultListModel<>();

        listaFavoritos = new JList<>(modeloFavoritos);
        listaAssistidas = new JList<>(modeloAssistidas);
        listaQueroAssistir = new JList<>(modeloQueroAssistir);

        configurarCliqueDetalhes(listaFavoritos);
        configurarCliqueDetalhes(listaAssistidas);
        configurarCliqueDetalhes(listaQueroAssistir);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Favoritos", new JScrollPane(listaFavoritos));
        abas.addTab("Assistidas", new JScrollPane(listaAssistidas));
        abas.addTab("Quero assistir", new JScrollPane(listaQueroAssistir));

        JPanel painelBotoes = new JPanel(new GridLayout(2, 3, 8, 8));
        painelBotoes.setBackground(new Color(42, 45, 54));

        // BOTÕES: removem a série selecionada da lista correspondente.
        JButton removerFavorito = new JButton("Remover favorito");
        JButton removerAssistida = new JButton("Remover assistida");
        JButton removerQuero = new JButton("Remover quero");

        removerFavorito.addActionListener(e -> removerDaLista(dados.getFavoritos(), listaFavoritos.getSelectedValue(), "favoritos"));
        removerAssistida.addActionListener(e -> removerDaLista(dados.getAssistidas(), listaAssistidas.getSelectedValue(), "assistidas"));
        removerQuero.addActionListener(e -> removerDaLista(dados.getQueroAssistir(), listaQueroAssistir.getSelectedValue(), "quero assistir"));

        // BOTÕES: ordenam as três listas.
        JButton ordenarNome = new JButton("Ordenar por nome");
        JButton ordenarNota = new JButton("Ordenar por nota");
        JButton ordenarEstado = new JButton("Ordenar por estado");
        JButton ordenarEstreia = new JButton("Ordenar por estreia");

        ordenarNome.addActionListener(e -> ordenarTodasPorNome());
        ordenarNota.addActionListener(e -> ordenarTodasPorNota());
        ordenarEstado.addActionListener(e -> ordenarTodasPorEstado());
        ordenarEstreia.addActionListener(e -> ordenarTodasPorEstreia());

        painelBotoes.add(removerFavorito);
        painelBotoes.add(removerAssistida);
        painelBotoes.add(removerQuero);
        painelBotoes.add(ordenarNome);
        painelBotoes.add(ordenarNota);
        painelBotoes.add(ordenarEstado);

        JPanel painelExtra = new JPanel(new BorderLayout());
        painelExtra.setBackground(new Color(42, 45, 54));
        painelExtra.add(painelBotoes, BorderLayout.CENTER);
        painelExtra.add(ordenarEstreia, BorderLayout.SOUTH);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(abas, BorderLayout.CENTER);
        painel.add(painelExtra, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelDetalhes() {
        JPanel painelDetalhes = new JPanel(new BorderLayout(8, 8));
        painelDetalhes.setPreferredSize(new Dimension(300, 0));
        painelDetalhes.setBackground(new Color(42, 45, 54));
        painelDetalhes.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Detalhes da série");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        areaDetalhes = new JTextArea();
        areaDetalhes.setEditable(false);
        areaDetalhes.setLineWrap(true);
        areaDetalhes.setWrapStyleWord(true);
        areaDetalhes.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaDetalhes.setText("Selecione uma série para visualizar os detalhes.");

        JScrollPane scroll = new JScrollPane(areaDetalhes);

        painelDetalhes.add(titulo, BorderLayout.NORTH);
        painelDetalhes.add(scroll, BorderLayout.CENTER);

        return painelDetalhes;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(new Color(30, 32, 38));

        JLabel texto = new JLabel("Dados salvos localmente em JSON | API utilizada: TVMaze", JLabel.CENTER);
        texto.setForeground(new Color(180, 180, 180));

        rodape.add(texto, BorderLayout.CENTER);

        return rodape;
    }

    private void salvarUsuario() {
        dados.setApelido(campoApelido.getText());
        salvarDados();
        JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!");
    }

    /*
     * Este método é chamado pelo botão "Buscar na TVMaze".
     * Ele chama TvMazeService, que é onde está a requisição da API.
     */
    private void buscarSeries() {
        String termo = campoBusca.getText();

        if (termo == null || termo.trim().isBlank()) {
            JOptionPane.showMessageDialog(this, "Digite o nome de uma série para buscar.");
            return;
        }

        modeloResultados.clear();
        areaDetalhes.setText("Buscando séries na API TVMaze...");

        SwingWorker<List<Serie>, Void> worker = new SwingWorker<>() {

            protected List<Serie> doInBackground() throws Exception {
                return tvMazeService.buscarSeries(termo);
            }

            protected void done() {
                try {
                    List<Serie> resultados = get();

                    if (resultados.isEmpty()) {
                        areaDetalhes.setText("Nenhuma série encontrada para: " + termo);
                        JOptionPane.showMessageDialog(AppFrame.this, "Nenhuma série encontrada.");
                        return;
                    }

                    for (Serie serie : resultados) {
                        modeloResultados.addElement(serie);
                    }

                    areaDetalhes.setText("Busca finalizada. Selecione uma série para ver os detalhes.");

                } catch (Exception e) {
                    areaDetalhes.setText("Erro ao buscar séries.");
                    JOptionPane.showMessageDialog(AppFrame.this, "Erro na busca: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void limparBusca() {
        campoBusca.setText("");
        modeloResultados.clear();
        areaDetalhes.setText("Selecione uma série para visualizar os detalhes.");
    }

    private void adicionarNaLista(List<Serie> lista, Serie serie, String nomeLista) {
        if (serie == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série nos resultados da busca.");
            return;
        }

        boolean adicionou = dados.adicionarSerie(lista, serie);

        if (!adicionou) {
            JOptionPane.showMessageDialog(this, "Essa série já está na lista de " + nomeLista + ".");
            return;
        }

        atualizarTodasAsListas();
        salvarDados();
        JOptionPane.showMessageDialog(this, "Série adicionada em " + nomeLista + "!");
    }

    private void removerDaLista(List<Serie> lista, Serie serie, String nomeLista) {
        if (serie == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma série na lista de " + nomeLista + ".");
            return;
        }

        boolean removeu = dados.removerSerie(lista, serie);

        if (!removeu) {
            JOptionPane.showMessageDialog(this, "Não foi possível remover a série.");
            return;
        }

        atualizarTodasAsListas();
        salvarDados();
        areaDetalhes.setText("Série removida da lista de " + nomeLista + ".");
    }

    private void configurarCliqueDetalhes(JList<Serie> lista) {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalhes(lista.getSelectedValue());
            }
        });
    }

    private void mostrarDetalhes(Serie serie) {
        if (serie == null) {
            return;
        }

        areaDetalhes.setText(serie.detalhes());
    }

    private void atualizarTodasAsListas() {
        preencherModelo(modeloFavoritos, dados.getFavoritos());
        preencherModelo(modeloAssistidas, dados.getAssistidas());
        preencherModelo(modeloQueroAssistir, dados.getQueroAssistir());
    }

    private void preencherModelo(DefaultListModel<Serie> modelo, List<Serie> lista) {
        if (modelo == null) {
            return;
        }

        modelo.clear();

        for (Serie serie : lista) {
            modelo.addElement(serie);
        }
    }

    private void ordenarTodasPorNome() {
        ordenarLista(dados.getFavoritos(), Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getAssistidas(), Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getQueroAssistir(), Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER));
        atualizarTodasAsListas();
        salvarDados();
    }

    private void ordenarTodasPorNota() {
        ordenarLista(dados.getFavoritos(), Comparator.comparingDouble(Serie::getNota).reversed());
        ordenarLista(dados.getAssistidas(), Comparator.comparingDouble(Serie::getNota).reversed());
        ordenarLista(dados.getQueroAssistir(), Comparator.comparingDouble(Serie::getNota).reversed());
        atualizarTodasAsListas();
        salvarDados();
    }

    private void ordenarTodasPorEstado() {
        ordenarLista(dados.getFavoritos(), Comparator.comparing(Serie::getEstado, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getAssistidas(), Comparator.comparing(Serie::getEstado, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getQueroAssistir(), Comparator.comparing(Serie::getEstado, String.CASE_INSENSITIVE_ORDER));
        atualizarTodasAsListas();
        salvarDados();
    }

    private void ordenarTodasPorEstreia() {
        ordenarLista(dados.getFavoritos(), Comparator.comparing(Serie::getDataEstreia, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getAssistidas(), Comparator.comparing(Serie::getDataEstreia, String.CASE_INSENSITIVE_ORDER));
        ordenarLista(dados.getQueroAssistir(), Comparator.comparing(Serie::getDataEstreia, String.CASE_INSENSITIVE_ORDER));
        atualizarTodasAsListas();
        salvarDados();
    }

    private void ordenarLista(List<Serie> lista, Comparator<Serie> comparator) {
        try {
            lista.sort(comparator);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao ordenar lista.");
        }
    }

    // Toda vez que adiciona, remove, ordena ou salva usuário, grava no JSON.
    private void salvarDados() {
        try {
            jsonStorage.salvar(dados);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}