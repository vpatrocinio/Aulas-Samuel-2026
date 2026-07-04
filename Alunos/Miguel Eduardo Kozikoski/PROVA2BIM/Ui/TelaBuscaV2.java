package Ui;

import Modelos.Serie;
import Modelos.Usuario;

import Service.ApiService;
import Service.JsonService;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TelaBuscaV2 extends JFrame {

    private JTextField txtPesquisa;
    private JLabel lblImagem;
    private JLabel lblNome, lblIdioma, lblNota, lblGeneros, lblStatus, lblEstreia, lblTermino, lblEmissora;

    private JList<String> listaResultados;
    private DefaultListModel<String> modeloResultados;
    private ArrayList<String> jsonResultados;

    private Serie serieAtual;
    private Usuario usuario;

    public TelaBuscaV2(Usuario usuario) {
        this.usuario = usuario;
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("NETFLIX EM JAVA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        txtPesquisa = new JTextField();
        JButton btnPesquisar = new JButton("Pesquisar");

        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.add(titulo, BorderLayout.NORTH);
        painelTopo.add(txtPesquisa, BorderLayout.CENTER);
        painelTopo.add(btnPesquisar, BorderLayout.EAST);

        lblImagem = new JLabel("Imagem da série", SwingConstants.CENTER);
        lblImagem.setPreferredSize(new Dimension(250, 350));

        lblNome = new JLabel("Nome: ");
        lblIdioma = new JLabel("Idioma: ");
        lblNota = new JLabel("Nota: ");
        lblGeneros = new JLabel("Gêneros: ");
        lblStatus = new JLabel("Status: ");
        lblEstreia = new JLabel("Estreia: ");
        lblTermino = new JLabel("Término: ");
        lblEmissora = new JLabel("Emissora: ");

        Font fonte = new Font("Arial", Font.BOLD, 15);

        lblNome.setFont(fonte);
        lblIdioma.setFont(fonte);
        lblNota.setFont(fonte);
        lblGeneros.setFont(fonte);
        lblStatus.setFont(fonte);
        lblEstreia.setFont(fonte);
        lblTermino.setFont(fonte);
        lblEmissora.setFont(fonte);

        JPanel painelInfo = new JPanel(new GridLayout(8, 1, 5, 5));
        painelInfo.add(lblNome);
        painelInfo.add(lblIdioma);
        painelInfo.add(lblNota);
        painelInfo.add(lblGeneros);
        painelInfo.add(lblStatus);
        painelInfo.add(lblEstreia);
        painelInfo.add(lblTermino);
        painelInfo.add(lblEmissora);

        modeloResultados = new DefaultListModel<>();
        listaResultados = new JList<>(modeloResultados);
        jsonResultados = new ArrayList<>();

        JPanel painelResultados = new JPanel(new BorderLayout());
        painelResultados.setPreferredSize(new Dimension(220, 0));
        painelResultados.add(new JLabel("Escolha uma série:"), BorderLayout.NORTH);
        painelResultados.add(new JScrollPane(listaResultados), BorderLayout.CENTER);

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.add(lblImagem, BorderLayout.WEST);
        painelCentro.add(painelInfo, BorderLayout.CENTER);
        painelCentro.add(painelResultados, BorderLayout.EAST);

        JButton btnFavorito = new JButton("Favoritos");
        JButton btnAssistida = new JButton("Assistidas");
        JButton btnDesejo = new JButton("Desejo Assistir");
        JButton btnVerListas = new JButton("Ver Minhas Listas");

        JPanel painelBotoes = new JPanel(new GridLayout(2, 2, 10, 10));
        painelBotoes.add(btnFavorito);
        painelBotoes.add(btnAssistida);
        painelBotoes.add(btnDesejo);
        painelBotoes.add(btnVerListas);

        getContentPane().setBackground(TemasColors.FUNDO);

        painelTopo.setBackground(TemasColors.PAINEL);
        painelCentro.setBackground(TemasColors.PAINEL);
        painelInfo.setBackground(TemasColors.PAINEL);
        painelResultados.setBackground(TemasColors.PAINEL);
        painelBotoes.setBackground(TemasColors.PAINEL);

        titulo.setForeground(TemasColors.TEXTO);

        lblNome.setForeground(TemasColors.TEXTO);
        lblIdioma.setForeground(TemasColors.TEXTO);
        lblNota.setForeground(TemasColors.TEXTO);
        lblGeneros.setForeground(TemasColors.TEXTO);
        lblStatus.setForeground(TemasColors.TEXTO);
        lblEstreia.setForeground(TemasColors.TEXTO);
        lblTermino.setForeground(TemasColors.TEXTO);
        lblEmissora.setForeground(TemasColors.TEXTO);

        txtPesquisa.setBackground(TemasColors.CAMPO);
        txtPesquisa.setForeground(Color.BLACK);

        listaResultados.setBackground(TemasColors.PAINEL);
        listaResultados.setForeground(TemasColors.TEXTO);
        listaResultados.setSelectionBackground(TemasColors.VERMELHO);
        listaResultados.setSelectionForeground(TemasColors.TEXTO);

        Tema.aplicar(btnPesquisar);
        Tema.aplicar(btnFavorito);
        Tema.aplicar(btnAssistida);
        Tema.aplicar(btnDesejo);
        Tema.aplicar(btnVerListas);

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        btnPesquisar.addActionListener(e -> pesquisarSerie());

        listaResultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = listaResultados.getSelectedIndex();

                if (index >= 0) {
                    carregarSerieNaTela(jsonResultados.get(index));
                }
            }
        });

        //função das mensagem que dos botões de aivsos
        btnFavorito.addActionListener(e -> {
            if (serieAtual != null) {
                usuario.getFavoritos().add(serieAtual);
                JsonService.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Série adicionada aos favoritos!");
            }
        });

        btnAssistida.addActionListener(e -> {
            if (serieAtual != null) {
                usuario.getAssistidas().add(serieAtual);
                JsonService.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Série adicionada às assistidas!");
            }
        });

        btnDesejo.addActionListener(e -> {
            if (serieAtual != null) {
                usuario.getDesejoAssistir().add(serieAtual);
                JsonService.salvarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Série adicionada ao desejo assistir!");
            }
        });

        btnVerListas.addActionListener(e -> {
            TelaListas telaListas = new TelaListas(usuario);
            telaListas.setVisible(true);
        });
    }

    private void carregarSerieNaTela(String json) {
        serieAtual = new Serie();

        serieAtual.setNome(JsonService.pegarValor(json, "name"));
        serieAtual.setIdioma(JsonService.pegarValor(json, "language"));
        serieAtual.setStatus(JsonService.pegarValor(json, "status"));
        serieAtual.setEstreia(JsonService.pegarValor(json, "premiered"));
        serieAtual.setTermino(JsonService.pegarValor(json, "ended"));
        serieAtual.setImagem(JsonService.pegarValor(json, "medium"));
        serieAtual.setGeneros(JsonService.pegarArray(json, "genres"));
        serieAtual.setEmissora(JsonService.pegarEmissora(json));

        try {
            String notaTexto = JsonService.pegarValor(json, "average");

            if (notaTexto.equals("null") || notaTexto.isEmpty()) {
                serieAtual.setNota(0.0);
            } else {
                serieAtual.setNota(Double.parseDouble(notaTexto));
            }

        } catch (Exception e) {
            serieAtual.setNota(0.0);
        }

        lblNome.setText("Nome: " + serieAtual.getNome());
        lblIdioma.setText("Idioma: " + serieAtual.getIdioma());
        lblNota.setText("Nota: " + serieAtual.getNota());
        lblGeneros.setText("Gêneros: " + serieAtual.getGeneros());
        lblStatus.setText("Status: " + serieAtual.getStatus());
        lblEstreia.setText("Estreia: " + serieAtual.getEstreia());
        lblTermino.setText("Término: " + serieAtual.getTermino());
        lblEmissora.setText("Emissora: " + serieAtual.getEmissora());

        try {
            ImageIcon imagemOriginal = new ImageIcon(new java.net.URL(serieAtual.getImagem()));

            Image imagemRedimensionada = imagemOriginal.getImage()
                    .getScaledInstance(220, 320, Image.SCALE_SMOOTH);

            lblImagem.setIcon(new ImageIcon(imagemRedimensionada));
            lblImagem.setText("");

        } catch (Exception e) {
            lblImagem.setText("Imagem não encontrada");
            lblImagem.setIcon(null);
        }
    }

    //integrção da class para buscar o Json para trazar a API
    private void pesquisarSerie() {
        try {
            ApiService api = new ApiService();
            String json = api.buscar(txtPesquisa.getText());

            jsonResultados = JsonService.separarResultados(json);

            modeloResultados.clear();

            for (String item : jsonResultados) {
                String nome = JsonService.pegarValor(item, "name");
                modeloResultados.addElement(nome);
            }

            if (!jsonResultados.isEmpty()) {
                listaResultados.setSelectedIndex(0);
                carregarSerieNaTela(jsonResultados.get(0));
            }

        } catch (Exception erro) {
            erro.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar série.");
        }
    }
}