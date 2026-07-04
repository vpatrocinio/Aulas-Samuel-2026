import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import org.json.*;

public class TelaPrincipal extends JFrame {

    private Usuario usuario;
    private DefaultListModel<Serie> modeloBusca = new DefaultListModel<>();
    private DefaultListModel<Serie> modeloFavoritos = new DefaultListModel<>();
    private DefaultListModel<Serie> modeloAssistidas = new DefaultListModel<>();
    private DefaultListModel<Serie> modeloDeseja = new DefaultListModel<>();
    private JList<Serie> listaBusca, listaFavoritos, listaAssistidas, listaDeseja;
    private JTextField campoBusca, campoNome;
    private JTextArea areaDetalhes;
    private JComboBox<String> comboOrdenar;
    private JTabbedPane abas;

    private static final Color CINZA_FUNDO = new Color(20, 20, 20);
    private static final Color CINZA_CLARO = new Color(45, 45, 45);
    private static final Color CINZA_MEDIO = new Color(30, 30, 30);
    private static final Color BOTAO_FUNDO = new Color(221, 121, 241);
    private static final Color BOTAO_HOVER = new Color(218, 112, 214);
    private static final Color DETALHE_ROXO = new Color(230, 180, 250);
    private static final Color ROSA_TEXTO = new Color(221, 160, 221);
    private static final Color BRANCO = new Color(255, 255, 255);
    private static final Color PRETO = new Color(0, 0, 0);
    private static final String ARQUIVO_DADOS = "dados.json";

    // CONSTRUTOR: inicia a tela, carrega dados e configura a UI
    public TelaPrincipal() {
        super("Pobreflix");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CINZA_FUNDO);

        // Ao fechar a janela, salva os dados (SERIALIZACAO) antes de sair
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                salvarDados();   // <-- AQUI: SERIALIZACAO ao fechar o app
                dispose();
            }
        });

        carregarDados();   // <-- AQUI: DESSERIALIZACAO ao abrir o app
        if (usuario == null) {
            String nome = JOptionPane.showInputDialog(this, "Qual seu nome ou apelido?", "Bem-vindo!", JOptionPane.QUESTION_MESSAGE);
            if (nome == null || nome.trim().isEmpty()) nome = "Usuario";
            usuario = new Usuario(nome);
        }

        initUI();
        atualizarTodasListas();
    }

    // initUI(): monta toda a interface grafica
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(CINZA_FUNDO);

        // --- PAINEL SUPERIOR (NORTH) ---
        JPanel topo = new JPanel(new BorderLayout(10, 5));
        topo.setBackground(CINZA_FUNDO);
        topo.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel lblTitulo = new JLabel("Pobreflix", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        lblTitulo.setForeground(ROSA_TEXTO);
        topo.add(lblTitulo, BorderLayout.NORTH);

        // Painel do usuario (nome editavel)
        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelUsuario.setBackground(CINZA_FUNDO);
        JLabel lblUser = new JLabel("Usuario: ");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        lblUser.setForeground(BRANCO);
        campoNome = new JTextField(usuario.getNome(), 15);
        campoNome.setFont(new Font("Arial", Font.PLAIN, 14));
        campoNome.setBackground(CINZA_CLARO);
        campoNome.setForeground(BRANCO);
        campoNome.setCaretColor(BRANCO);
        campoNome.setBorder(BorderFactory.createLineBorder(DETALHE_ROXO, 2));
        JButton btnSalvarNome = criarBotao("Salvar Nome");
        btnSalvarNome.addActionListener(e -> {
            usuario.setNome(campoNome.getText().trim());
            salvarDados();   // <-- AQUI: SERIALIZACAO ao salvar nome
            JOptionPane.showMessageDialog(this, "Nome salvo!");
        });
        painelUsuario.add(lblUser);
        painelUsuario.add(campoNome);
        painelUsuario.add(btnSalvarNome);
        topo.add(painelUsuario, BorderLayout.CENTER);

        // BOTAO DE BUSCA + CAMPO DE TEXTO + INTERACAO DO USUARIO
        // Este painel contem o campo de busca e o botao "Buscar Serie".
        // Quando o usuario digita algo e clica no botao (ou aperta Enter),
        // o metodo buscarSerie() eh chamado.
        // Esse metodo faz a REQUISICAO DE API para a TVMaze.
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBusca.setBackground(CINZA_FUNDO);

        campoBusca = new JTextField(25);   // <-- CAMPO DE TEXTO onde o usuario digita o nome da serie
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.setBackground(CINZA_CLARO);
        campoBusca.setForeground(BRANCO);
        campoBusca.setCaretColor(BRANCO);
        campoBusca.setBorder(BorderFactory.createLineBorder(DETALHE_ROXO, 2));

        JButton btnBuscar = criarBotao("Buscar Serie");   // <-- BOTAO DE BUSCA
        // Quando clica no botao, chama buscarSerie() --> faz REQUISICAO DE API
        btnBuscar.addActionListener(e -> buscarSerie());
        // Quando aperta ENTER no campo de texto, tambem chama buscarSerie()
        campoBusca.addActionListener(e -> buscarSerie());

        painelBusca.add(criarLabel("Buscar: "));
        painelBusca.add(campoBusca);
        painelBusca.add(btnBuscar);
        topo.add(painelBusca, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);

        // --- ABAS (CENTER) ---
        abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.BOLD, 13));
        abas.setBackground(CINZA_FUNDO);
        abas.setForeground(ROSA_TEXTO);
        abas.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = CINZA_CLARO;
                lightHighlight = CINZA_CLARO;
                shadow = CINZA_MEDIO;
                darkShadow = CINZA_MEDIO;
            }
        });

        JPanel abaBusca = criarPainelAba(modeloBusca, "busca");
        listaBusca = (JList<Serie>) ((JScrollPane) abaBusca.getComponent(0)).getViewport().getView();
        abas.addTab("Busca", abaBusca);

        JPanel abaFav = criarPainelAba(modeloFavoritos, "favoritos");
        listaFavoritos = (JList<Serie>) ((JScrollPane) abaFav.getComponent(0)).getViewport().getView();
        abas.addTab("Favoritos", abaFav);

        JPanel abaAss = criarPainelAba(modeloAssistidas, "assistidas");
        listaAssistidas = (JList<Serie>) ((JScrollPane) abaAss.getComponent(0)).getViewport().getView();
        abas.addTab("Ja Assistidas", abaAss);

        JPanel abaDes = criarPainelAba(modeloDeseja, "deseja");
        listaDeseja = (JList<Serie>) ((JScrollPane) abaDes.getComponent(0)).getViewport().getView();
        abas.addTab("Quero Assistir", abaDes);

        add(abas, BorderLayout.CENTER);

        // --- PAINEL INFERIOR (SOUTH): Ordenacao + Detalhes ---
        JPanel painelSul = new JPanel(new BorderLayout(10, 5));
        painelSul.setBackground(CINZA_FUNDO);
        painelSul.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel painelOrdenar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelOrdenar.setBackground(CINZA_FUNDO);

        comboOrdenar = new JComboBox<>(new String[]{"Alfabetica", "Nota", "Estado", "Data de Estreia"});
        comboOrdenar.setFont(new Font("Arial", Font.PLAIN, 13));
        comboOrdenar.setBackground(CINZA_CLARO);
        comboOrdenar.setForeground(BRANCO);

        comboOrdenar.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(DETALHE_ROXO);
                    setForeground(BRANCO);
                } else {
                    setBackground(CINZA_CLARO);
                    setForeground(BRANCO);
                }
                return this;
            }
        });

        final JTextField editorField = new JTextField();
        editorField.setBackground(CINZA_CLARO);
        editorField.setForeground(BRANCO);
        editorField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        editorField.setFont(new Font("Arial", Font.PLAIN, 13));
        editorField.setEditable(false);

        comboOrdenar.setEditor(new ComboBoxEditor() {
            @Override
            public Component getEditorComponent() { return editorField; }
            @Override
            public void setItem(Object item) { editorField.setText(item != null ? item.toString() : ""); }
            @Override
            public Object getItem() { return editorField.getText(); }
            @Override
            public void selectAll() { editorField.selectAll(); }
            @Override
            public void addActionListener(ActionListener l) { editorField.addActionListener(l); }
            @Override
            public void removeActionListener(ActionListener l) { editorField.removeActionListener(l); }
        });
        comboOrdenar.setEditable(true);

        JButton btnOrdenar = criarBotao("Ordenar Lista Atual");
        btnOrdenar.addActionListener(e -> ordenarListaAtual());
        painelOrdenar.add(criarLabel("Ordenar por: "));
        painelOrdenar.add(comboOrdenar);
        painelOrdenar.add(btnOrdenar);
        painelSul.add(painelOrdenar, BorderLayout.NORTH);

        areaDetalhes = new JTextArea(6, 50);
        areaDetalhes.setFont(new Font("Arial", Font.PLAIN, 13));
        areaDetalhes.setBackground(CINZA_MEDIO);
        areaDetalhes.setForeground(BRANCO);
        areaDetalhes.setCaretColor(BRANCO);
        areaDetalhes.setEditable(false);
        areaDetalhes.setLineWrap(true);
        areaDetalhes.setWrapStyleWord(true);
        areaDetalhes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DETALHE_ROXO, 2), "Detalhes da Serie",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13), ROSA_TEXTO));
        painelSul.add(new JScrollPane(areaDetalhes), BorderLayout.CENTER);

        add(painelSul, BorderLayout.SOUTH);
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(BRANCO);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        return lbl;
    }

    private JPanel criarPainelAba(DefaultListModel<Serie> modelo, String tipo) {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBackground(CINZA_FUNDO);
        painel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JList<Serie> lista = new JList<>(modelo);
        lista.setFont(new Font("Arial", Font.PLAIN, 13));
        lista.setBackground(CINZA_MEDIO);
        lista.setForeground(BRANCO);
        lista.setSelectionBackground(DETALHE_ROXO);
        lista.setSelectionForeground(Color.WHITE);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(3, 8, 3, 8));
                if (!isSelected) setBackground(CINZA_MEDIO);
                return this;
            }
        });
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lista.getSelectedValue() != null) {
                mostrarDetalhes(lista.getSelectedValue());
            }
        });

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createLineBorder(DETALHE_ROXO, 2));
        scroll.getViewport().setBackground(CINZA_MEDIO);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        botoes.setBackground(CINZA_FUNDO);

        if (tipo.equals("busca")) {
            JButton btnFav = criarBotao("Add Favoritos");
            btnFav.addActionListener(e -> addSerieLista(lista, "favoritos"));
            JButton btnAss = criarBotao("Add Assistidas");
            btnAss.addActionListener(e -> addSerieLista(lista, "assistidas"));
            JButton btnDes = criarBotao("Add Quero Ver");
            btnDes.addActionListener(e -> addSerieLista(lista, "deseja"));
            botoes.add(btnFav);
            botoes.add(btnAss);
            botoes.add(btnDes);
        } else {
            JButton btnRem = criarBotao("Remover");
            btnRem.addActionListener(e -> removerSerieLista(lista, tipo));
            botoes.add(btnRem);
        }

        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(BOTAO_FUNDO);
        btn.setForeground(PRETO);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BOTAO_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(BOTAO_FUNDO); }
        });
        return btn;
    }

    // REQUISICAO DE API - METODO PRINCIPAL DE BUSCA
    // Este metodo eh chamado quando o usuario clica no botao "Buscar Serie"
    // ou aperta ENTER no campo de texto. Ele:
    // 1. Pega o texto digitado pelo usuario
    // 2. Monta a URL da API TVMaze com o termo de busca
    // 3. Chama fazerRequisicao() para fazer o HTTP GET
    // 4. Converte o JSON retornado em objetos Serie
    // 5. Mostra os resultados na lista de busca
    private void buscarSerie() {
        String query = campoBusca.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome da serie!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Monta a URL da API TVMaze com o termo de busca codificado
            String url = "https://api.tvmaze.com/search/shows?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            // FAZ A REQUISICAO HTTP GET para a API
            String json = fazerRequisicao(url);   // <-- AQUI: REQUISICAO DE API

            // Converte a resposta JSON em array de resultados
            JSONArray resultados = new JSONArray(json);
            modeloBusca.clear();
            for (int i = 0; i < resultados.length(); i++) {
                JSONObject showObj = resultados.getJSONObject(i).getJSONObject("show");
                Serie s = parseSerie(showObj);
                modeloBusca.addElement(s);
            }
            if (modeloBusca.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma serie encontrada!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converte um objeto JSON da API em um objeto Serie do nosso sistema
    private Serie parseSerie(JSONObject show) {
        int id = show.optInt("id", 0);
        String nome = show.optString("name", "Desconhecido");
        String idioma = show.optString("language", "Desconhecido");

        StringBuilder generos = new StringBuilder();
        JSONArray genArr = show.optJSONArray("genres");
        if (genArr != null) {
            for (int i = 0; i < genArr.length(); i++) {
                if (i > 0) generos.append(", ");
                generos.append(genArr.getString(i));
            }
        }

        double nota = -1;
        JSONObject rating = show.optJSONObject("rating");
        if (rating != null && !rating.isNull("average")) {
            nota = rating.optDouble("average", -1);
        }

        String estado = show.optString("status", "Desconhecido");
        String dataEstreia = show.optString("premiered", "N/A");
        String dataTermino = show.optString("ended", "N/A");

        String emissora = "N/A";
        JSONObject network = show.optJSONObject("network");
        if (network != null) {
            emissora = network.optString("name", "N/A");
        } else {
            JSONObject webChannel = show.optJSONObject("webChannel");
            if (webChannel != null) emissora = webChannel.optString("name", "N/A");
        }

        String imagemUrl = "";
        JSONObject image = show.optJSONObject("image");
        if (image != null) imagemUrl = image.optString("medium", "");

        return new Serie(id, nome, idioma, generos.toString(), nota, estado, dataEstreia, dataTermino, emissora, imagemUrl);
    }

    // REQUISICAO HTTP GET - CONEXAO COM A API EXTERNA
    // Este metodo abre uma conexao HTTP com a API TVMaze,
    // envia um GET, le a resposta e retorna o JSON como String.
    // Eh aqui que a comunicacao com a internet acontece.
    private String fazerRequisicao(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");   // Metodo HTTP GET
        conn.setRequestProperty("Accept", "application/json");   // Aceita JSON
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("HTTP " + status);
        }

        // Le a resposta da API linha por linha
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) sb.append(linha);
        reader.close();
        conn.disconnect();
        return sb.toString();   // Retorna o JSON completo como String
    }

    private void addSerieLista(JList<Serie> lista, String tipo) {
        Serie s = lista.getSelectedValue();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma serie!");
            return;
        }
        switch (tipo) {
            case "favoritos": usuario.adicionarFavorito(s); break;
            case "assistidas": usuario.adicionarAssistida(s); break;
            case "deseja": usuario.adicionarDesejaAssistir(s); break;
        }
        salvarDados();   // <-- AQUI: SERIALIZACAO ao adicionar serie
        atualizarTodasListas();
        JOptionPane.showMessageDialog(this, "Serie adicionada!");
    }

    private void removerSerieLista(JList<Serie> lista, String tipo) {
        Serie s = lista.getSelectedValue();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma serie!");
            return;
        }
        switch (tipo) {
            case "favoritos": usuario.removerFavorito(s); break;
            case "assistidas": usuario.removerAssistida(s); break;
            case "deseja": usuario.removerDesejaAssistir(s); break;
        }
        salvarDados();   // <-- AQUI: SERIALIZACAO ao remover serie
        atualizarTodasListas();
        JOptionPane.showMessageDialog(this, "Serie removida!");
    }

    private void mostrarDetalhes(Serie s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(s.getNome()).append("\n");
        sb.append("Idioma: ").append(s.getIdioma()).append("\n");
        sb.append("Generos: ").append(s.getGeneros()).append("\n");
        sb.append("Nota: ").append(s.getNota() < 0 ? "N/A" : s.getNota()).append("\n");
        sb.append("Estado: ").append(s.getEstado()).append("\n");
        sb.append("Estreia: ").append(s.getDataEstreia()).append("\n");
        sb.append("Termino: ").append(s.getDataTermino()).append("\n");
        sb.append("Emissora: ").append(s.getEmissora()).append("\n");
        areaDetalhes.setText(sb.toString());
    }

    private void ordenarListaAtual() {
        int aba = abas.getSelectedIndex();
        List<Serie> lista = new ArrayList<>();
        String criterio = (String) comboOrdenar.getSelectedItem();

        switch (aba) {
            case 1: lista.addAll(usuario.getFavoritos()); break;
            case 2: lista.addAll(usuario.getAssistidas()); break;
            case 3: lista.addAll(usuario.getDesejaAssistir()); break;
            default: JOptionPane.showMessageDialog(this, "Selecione uma lista para ordenar!"); return;
        }

        Comparator<Serie> comp;
        switch (criterio) {
            case "Alfabetica":
                comp = Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Nota":
                comp = Comparator.comparingDouble(Serie::getNota).reversed();
                break;
            case "Estado":
                comp = Comparator.comparing(Serie::getEstado, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Data de Estreia":
                comp = (a, b) -> compareDatas(a.getDataEstreia(), b.getDataEstreia());
                break;
            default:
                comp = Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER);
        }
        lista.sort(comp);

        switch (aba) {
            case 1: usuario.getFavoritos().clear(); usuario.getFavoritos().addAll(lista); break;
            case 2: usuario.getAssistidas().clear(); usuario.getAssistidas().addAll(lista); break;
            case 3: usuario.getDesejaAssistir().clear(); usuario.getDesejaAssistir().addAll(lista); break;
        }
        salvarDados();   // <-- AQUI: SERIALIZACAO ao ordenar lista
        atualizarTodasListas();
    }

    private int compareDatas(String a, String b) {
        if (a.equals("N/A") && b.equals("N/A")) return 0;
        if (a.equals("N/A")) return 1;
        if (b.equals("N/A")) return -1;
        return a.compareTo(b);
    }

    private void atualizarTodasListas() {
        atualizarModelo(modeloFavoritos, usuario.getFavoritos());
        atualizarModelo(modeloAssistidas, usuario.getAssistidas());
        atualizarModelo(modeloDeseja, usuario.getDesejaAssistir());
    }

    private void atualizarModelo(DefaultListModel<Serie> modelo, List<Serie> lista) {
        modelo.clear();
        for (Serie s : lista) modelo.addElement(s);
    }

    // SERIALIZACAO: SALVAR DADOS NO ARQUIVO JSON
    // Este metodo converte o objeto Usuario (e suas listas de series)
    // em um JSON e salva no arquivo "dados.json".
    // Eh chamado sempre que o usuario faz uma alteracao (add, remove,
    // ordena, muda nome, ou fecha o app).
    private void salvarDados() {
        try (FileWriter fw = new FileWriter(ARQUIVO_DADOS)) {
            fw.write(usuario.toJSON().toString(2));   // <-- AQUI: SERIALIZACAO em JSON
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    // DESSERIALIZACAO: CARREGAR DADOS DO ARQUIVO JSON
    // Este metodo le o arquivo "dados.json" e converte de volta
    // para um objeto Usuario com suas listas de series.
    // Eh chamado ao abrir o aplicativo.
    private void carregarDados() {
        File f = new File(ARQUIVO_DADOS);
        if (!f.exists()) return;
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
            br.close();
            usuario = Usuario.fromJSON(new JSONObject(sb.toString()));   // <-- AQUI: DESSERIALIZACAO
        } catch (Exception e) {
            System.err.println("Erro ao carregar: " + e.getMessage());
            usuario = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TelaPrincipal().setVisible(true);
        });
    }
}