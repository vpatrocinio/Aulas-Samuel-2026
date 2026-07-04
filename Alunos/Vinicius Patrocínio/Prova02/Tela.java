import javax.swing.*;
import java.awt.*;

public class Tela extends JFrame {
    private Usuario usuario;
    private JLabel lblUsuario;
    private TvMaze api;
    private Serie serieAtual;

    private final Color FUNDO = new Color(18,18,18);
    private final Color PAINEL = new Color(30,30,30);
    private final Color AZUL = new Color(29,161,242);
    private final Color TEXTO = Color.WHITE;

    private final Font TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 15);

    private JTextField txtPesquisa;
    private JButton btnBuscar;

    private JLabel lblImagem;

    private JLabel lblNome;
    private JLabel lblIdioma;
    private JLabel lblGenero;
    private JLabel lblNota;
    private JLabel lblStatus;
    private JLabel lblEstreia;
    private JLabel lblTermino;
    private JLabel lblEmissora;

    private JButton btnFavoritar;
    private JButton btnAssistida;
    private JButton btnDesejada;

    private JComboBox<String> comboLista;
    private JComboBox<String> comboOrdenacao;
    private DefaultListModel<String> modeloLista;
    private JList<String> lista;

    public Tela(Usuario usuario){
        this.usuario = usuario;
        api = new TvMaze();

        configurarJanela();
        criarComponentes();
        organizarTela();
        adicionarEventos();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("🎬 SeriesMax");
        lblUsuario = new JLabel("Olá, " + usuario.getNome() + "!");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void criarComponentes() {

        txtPesquisa = new JTextField(30);
        btnBuscar = new JButton("🔎 Buscar");

        lblImagem = new JLabel();
        lblImagem.setPreferredSize(new Dimension(180,250));
        lblNome = new JLabel("Nome:");
        lblIdioma = new JLabel("Idioma:");
        lblGenero = new JLabel("Gêneros:");
        lblNota = new JLabel("Nota:");
        lblStatus = new JLabel("Status:");
        lblEstreia = new JLabel("Estreia:");
        lblTermino = new JLabel("Término:");
        lblEmissora = new JLabel("Emissora:");

        btnFavoritar = new JButton("⭐ Favoritar");
        btnAssistida = new JButton("✔ Assistida");
        btnDesejada = new JButton("📌 Desejo assistir");

        comboLista = new JComboBox<>();
        comboLista.addItem("Favoritos");
        comboLista.addItem("Assistidas");
        comboLista.addItem("Desejadas");

        comboOrdenacao = new JComboBox<>();
        comboOrdenacao.addItem("Nome");
        comboOrdenacao.addItem("Nota");
        comboOrdenacao.addItem("Status");
        comboOrdenacao.addItem("Estreia");

        modeloLista = new DefaultListModel<>();
        lista = new JList<>(modeloLista);
    }

    private void organizarTela() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBackground(FUNDO);
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.setBackground(PAINEL);
        topo.add(lblUsuario, BorderLayout.CENTER);

        JLabel titulo = new JLabel("🎬 SeriesMax");
        titulo.setForeground(TEXTO);
        titulo.setFont(TITULO);

        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pesquisa.setBackground(PAINEL);

        pesquisa.add(txtPesquisa);
        pesquisa.add(btnBuscar);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(pesquisa, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout(20, 20));
        centro.setBackground(PAINEL);

        JPanel painelImagem = new JPanel();
        painelImagem.setBackground(PAINEL);
        painelImagem.add(lblImagem);

        JPanel painelInfo = new JPanel(new GridLayout(8,1,5,5));
        painelInfo.setBackground(PAINEL);

        painelInfo.add(lblNome);
        painelInfo.add(lblIdioma);
        painelInfo.add(lblGenero);
        painelInfo.add(lblNota);
        painelInfo.add(lblStatus);
        painelInfo.add(lblEstreia);
        painelInfo.add(lblTermino);
        painelInfo.add(lblEmissora);

        centro.add(painelImagem, BorderLayout.WEST);
        centro.add(painelInfo, BorderLayout.CENTER);

        JPanel sul = new JPanel(new FlowLayout());
        sul.setBackground(PAINEL);
        sul.add(btnFavoritar);
        sul.add(btnAssistida);
        sul.add(btnDesejada);

        JPanel direita = new JPanel(new BorderLayout(10,10));
        direita.setBackground(PAINEL);

        JPanel filtros = new JPanel(new GridLayout(2,1,5,5));
        filtros.setBackground(PAINEL);

        filtros.add(comboLista);
        filtros.add(comboOrdenacao);

        JScrollPane scroll = new JScrollPane(lista);

        direita.add(filtros, BorderLayout.NORTH);
        direita.add(scroll, BorderLayout.CENTER);

        principal.add(topo, BorderLayout.NORTH);
        principal.add(centro, BorderLayout.CENTER);
        principal.add(direita, BorderLayout.EAST);
        principal.add(sul, BorderLayout.SOUTH);

        add(principal);
    }

    private void adicionarEventos() {

        btnBuscar.addActionListener(e -> buscarSerie());

        btnFavoritar.addActionListener(e -> {
            if (serieAtual == null) {
                JOptionPane.showMessageDialog(this,
                        "Pesquise uma série primeiro.");
                return;
            }
            if (usuario.adicionarFavoritos(serieAtual)) {
                PersistenciaJSON.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this,
                        "Série adicionada aos favoritos!");
                atualizarLista();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Essa série já está nos favoritos.");
            }
        });

        btnAssistida.addActionListener(e -> {
            if (serieAtual != null) {
                usuario.adicionarAssistidas(serieAtual);
                PersistenciaJSON.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this,
                        "Série adicionada às assistidas!");
                atualizarLista();
            }
        });

        btnDesejada.addActionListener(e -> {
            if (serieAtual != null) {
                usuario.adicionarDesejoAssistir(serieAtual);
                PersistenciaJSON.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this,
                        "Série adicionada à lista de desejo!");
                atualizarLista();
            }
        });
        comboLista.addActionListener(e -> atualizarLista());
    }

    private void carregarImagem() {
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(serieAtual.getImagem()));
            Image img = icon.getImage().getScaledInstance(
                    180,
                    250,
                    Image.SCALE_SMOOTH
            );
            lblImagem.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblImagem.setIcon(null);
        }
    }

    private void atualizarInformacoes() {
        lblNome.setText("Nome: " + serieAtual.getNome());
        lblNome.setForeground(TEXTO);
        lblNome.setFont(NORMAL);
        lblIdioma.setText("Idioma: " + serieAtual.getIdioma());
        lblIdioma.setForeground(TEXTO);
        lblIdioma.setFont(NORMAL);
        lblGenero.setText("Gêneros: " + String.join(", ", serieAtual.getGeneros()));
        lblGenero.setForeground(TEXTO);
        lblGenero.setFont(NORMAL);
        lblNota.setText("Nota: ⭐ " + serieAtual.getNota());
        lblNota.setForeground(TEXTO);
        lblNota.setFont(NORMAL);
        lblStatus.setText("Status: " + serieAtual.getStatus());
        lblStatus.setForeground(TEXTO);
        lblStatus.setFont(NORMAL);
        lblEstreia.setText("Estreia: " + serieAtual.getEstreia());
        lblEstreia.setForeground(TEXTO);
        lblEstreia.setFont(NORMAL);
        lblTermino.setText("Término: " + serieAtual.getTermino());
        lblTermino.setForeground(TEXTO);
        lblTermino.setFont(NORMAL);
        lblEmissora.setText("Emissora: " + serieAtual.getEmissora());
        lblEmissora.setForeground(TEXTO);
        lblEmissora.setFont(NORMAL);
        carregarImagem();
    }

    private void atualizarLista() {
        modeloLista.clear();
        if (comboLista.getSelectedItem().equals("Favoritos")) {
            for (Serie s : usuario.getFavoritos()) {
                modeloLista.addElement(s.getNome());
            }
        }
        else if (comboLista.getSelectedItem().equals("Assistidas")) {
            for (Serie s : usuario.getAssistidas()) {
                modeloLista.addElement(s.getNome());
            }
        }
        else {
            for (Serie s : usuario.getDesejoAssistir()) {
                modeloLista.addElement(s.getNome());
            }
        }
    }

    private void buscarSerie() {
        String nome = txtPesquisa.getText();
        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Digite o nome de uma série.");
            return;
        }
        serieAtual = api.buscarSerie(nome);
        if (serieAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Série não encontrada.");
            return;
        }
        atualizarInformacoes();
    }
}