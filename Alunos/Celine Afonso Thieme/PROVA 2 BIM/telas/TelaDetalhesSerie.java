package telas;

import controller.SistemaController;
import model.Serie;
import service.PersistenciaService.PersistenciaException;
import util.IdiomaUtil;
import util.MensagensUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Tela de detalhes de uma serie. Exibe todas as informacoes disponiveis
 * e oferece botoes para adicionar/remover a serie das listas do usuario.
 * A imagem e carregada de forma assincrona para nao travar a interface.
 */
public class TelaDetalhesSerie extends JFrame implements IdiomaUtil.AtualizavelIdioma {

    private final SistemaController controller;
    private final Serie serie;
    private final JFrame janelaPai;

    private JButton btnFavoritar;
    private JButton btnAssistida;
    private JButton btnDesejo;
    private JButton btnRemover;
    private JButton btnVoltar;
    private JLabel labelTituloJanela;

    public TelaDetalhesSerie(SistemaController controller, Serie serie, JFrame janelaPai) {
        this.controller = controller;
        this.serie = serie;
        this.janelaPai = janelaPai;
        IdiomaUtil.registrarOuvinte(this);
        configurarJanela();
        montarInterface();
    }

    private void configurarJanela() {
        setTitle(serie.getNome());
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TemaUtil.centralizarJanela(this);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                IdiomaUtil.removerOuvinte(TelaDetalhesSerie.this);
            }
        });
    }

    private void montarInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 0));
        painelPrincipal.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel esquerdo: imagem
        painelPrincipal.add(criarPainelImagem(), BorderLayout.WEST);

        // Painel direito: informacoes + botoes
        JPanel painelDireito = new JPanel(new BorderLayout(0, 15));
        painelDireito.setBackground(TemaUtil.FUNDO_PRINCIPAL);

        labelTituloJanela = new JLabel(serie.getNome());
        labelTituloJanela.setFont(TemaUtil.FONTE_TITULO);
        labelTituloJanela.setForeground(TemaUtil.TEXTO);
        painelDireito.add(labelTituloJanela, BorderLayout.NORTH);

        painelDireito.add(criarPainelInformacoes(), BorderLayout.CENTER);
        painelDireito.add(criarPainelBotoes(), BorderLayout.SOUTH);

        painelPrincipal.add(painelDireito, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelImagem() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(TemaUtil.CARD);
        painel.setPreferredSize(new Dimension(180, 260));

        JLabel labelImagem = new JLabel(IdiomaUtil.get("detalhes.semImagem"), SwingConstants.CENTER);
        labelImagem.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        labelImagem.setFont(TemaUtil.FONTE_PADRAO.deriveFont(11f));
        painel.add(labelImagem, BorderLayout.CENTER);

        // Carrega a imagem de forma assincrona para nao travar a UI
        if (serie.getImagemUrl() != null && !serie.getImagemUrl().isEmpty()) {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() {
                    try {
                        URL url = new URL(serie.getImagemUrl());
                        BufferedImage img = ImageIO.read(url);
                        if (img != null) {
                            Image scaled = img.getScaledInstance(180, 260, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaled);
                        }
                    } catch (IOException e) {
                        System.err.println("Nao foi possivel carregar a imagem: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icone = get();
                        if (icone != null) {
                            labelImagem.setText("");
                            labelImagem.setIcon(icone);
                        }
                    } catch (Exception e) {
                        // Mantém o texto "Sem imagem" se falhar
                    }
                }
            };
            worker.execute();
        }
        return painel;
    }

    private JScrollPane criarPainelInformacoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(TemaUtil.PAINEL);
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        adicionarLinha(painel, IdiomaUtil.get("detalhes.idioma"), serie.getIdioma());
        adicionarLinha(painel, IdiomaUtil.get("detalhes.generos"), serie.getGenerosFormatados());
        adicionarLinha(painel, IdiomaUtil.get("detalhes.nota"), serie.getNotaFormatada());
        adicionarLinha(painel, IdiomaUtil.get("detalhes.status"), serie.getStatus());
        adicionarLinha(painel, IdiomaUtil.get("detalhes.estreia"),
                serie.getDataEstreia() != null ? serie.getDataEstreia() : "N/A");
        adicionarLinha(painel, IdiomaUtil.get("detalhes.termino"),
                serie.getDataTermino() != null ? serie.getDataTermino() : "N/A");
        adicionarLinha(painel, IdiomaUtil.get("detalhes.emissora"), serie.getEmissora());

        if (serie.getResumo() != null && !serie.getResumo().isEmpty()) {
            JLabel lblResumoTitulo = new JLabel(IdiomaUtil.get("detalhes.resumo"));
            lblResumoTitulo.setFont(TemaUtil.FONTE_PADRAO.deriveFont(Font.BOLD));
            lblResumoTitulo.setForeground(TemaUtil.TEXTO_SECUNDARIO);
            lblResumoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(Box.createVerticalStrut(8));
            painel.add(lblResumoTitulo);

            JTextArea areaResumo = new JTextArea(serie.getResumo());
            areaResumo.setWrapStyleWord(true);
            areaResumo.setLineWrap(true);
            areaResumo.setEditable(false);
            areaResumo.setBackground(TemaUtil.PAINEL);
            areaResumo.setForeground(TemaUtil.TEXTO);
            areaResumo.setFont(TemaUtil.FONTE_PADRAO.deriveFont(13f));
            areaResumo.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(areaResumo);
        }

        JScrollPane scroll = TemaUtil.criarScrollEstilizado(painel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private void adicionarLinha(JPanel painel, String rotulo, String valor) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        linha.setBackground(TemaUtil.PAINEL);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRotulo = new JLabel(rotulo + " ");
        lblRotulo.setFont(TemaUtil.FONTE_PADRAO.deriveFont(Font.BOLD));
        lblRotulo.setForeground(TemaUtil.TEXTO_SECUNDARIO);

        JLabel lblValor = new JLabel(valor != null ? valor : "N/A");
        lblValor.setFont(TemaUtil.FONTE_PADRAO);
        lblValor.setForeground(TemaUtil.TEXTO);

        linha.add(lblRotulo);
        linha.add(lblValor);
        painel.add(linha);
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new GridLayout(2, 2, 8, 8));
        painel.setBackground(TemaUtil.FUNDO_PRINCIPAL);

        btnFavoritar = TemaUtil.criarBotaoPrimario("⭐ " + IdiomaUtil.get("detalhes.botao.favoritar"));
        btnFavoritar.setToolTipText(IdiomaUtil.get("detalhes.botao.favoritar"));
        btnFavoritar.addActionListener(e -> adicionarFavorito());

        btnAssistida = TemaUtil.criarBotaoPrimario("✅ " + IdiomaUtil.get("detalhes.botao.assistida"));
        btnAssistida.setToolTipText(IdiomaUtil.get("detalhes.botao.assistida"));
        btnAssistida.addActionListener(e -> adicionarAssistida());

        btnDesejo = TemaUtil.criarBotaoPrimario("📌 " + IdiomaUtil.get("detalhes.botao.desejo"));
        btnDesejo.setToolTipText(IdiomaUtil.get("detalhes.botao.desejo"));
        btnDesejo.addActionListener(e -> adicionarDesejo());

        btnRemover = TemaUtil.criarBotaoSecundario("🗑 " + IdiomaUtil.get("detalhes.botao.remover"));
        btnRemover.setToolTipText(IdiomaUtil.get("detalhes.botao.remover"));
        btnRemover.addActionListener(e -> removerDasListas());

        btnVoltar = TemaUtil.criarBotaoSecundario("← " + IdiomaUtil.get("detalhes.botao.voltar"));
        btnVoltar.addActionListener(e -> {
            IdiomaUtil.removerOuvinte(this);
            dispose();
        });

        painel.add(btnFavoritar);
        painel.add(btnAssistida);
        painel.add(btnDesejo);
        painel.add(btnRemover);

        JPanel painelComVoltar = new JPanel(new BorderLayout(0, 8));
        painelComVoltar.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelComVoltar.add(painel, BorderLayout.CENTER);
        painelComVoltar.add(btnVoltar, BorderLayout.SOUTH);

        return painelComVoltar;
    }

    private void adicionarFavorito() {
        try {
            if (!controller.adicionarFavorito(serie)) {
                MensagensUtil.exibirAtencao(this, IdiomaUtil.get("msg.jaExisteLista"));
            } else {
                MensagensUtil.exibirSucesso(this, IdiomaUtil.get("msg.adicionadoFavoritos"));
            }
        } catch (PersistenciaException e) {
            MensagensUtil.exibirErro(this, IdiomaUtil.get("msg.erroInesperado"));
            MensagensUtil.registrarErroTecnico(e);
        }
    }

    private void adicionarAssistida() {
        try {
            if (!controller.adicionarAssistida(serie)) {
                MensagensUtil.exibirAtencao(this, IdiomaUtil.get("msg.jaExisteLista"));
            } else {
                MensagensUtil.exibirSucesso(this, IdiomaUtil.get("msg.adicionadoAssistidas"));
            }
        } catch (PersistenciaException e) {
            MensagensUtil.exibirErro(this, IdiomaUtil.get("msg.erroInesperado"));
            MensagensUtil.registrarErroTecnico(e);
        }
    }

    private void adicionarDesejo() {
        try {
            if (!controller.adicionarDesejoAssistir(serie)) {
                MensagensUtil.exibirAtencao(this, IdiomaUtil.get("msg.jaExisteLista"));
            } else {
                MensagensUtil.exibirSucesso(this, IdiomaUtil.get("msg.adicionadoDesejo"));
            }
        } catch (PersistenciaException e) {
            MensagensUtil.exibirErro(this, IdiomaUtil.get("msg.erroInesperado"));
            MensagensUtil.registrarErroTecnico(e);
        }
    }

    private void removerDasListas() {
        try {
            boolean removeu = false;
            if (controller.isFavorito(serie)) {
                controller.removerFavorito(serie);
                removeu = true;
            }
            if (controller.isAssistida(serie)) {
                controller.removerAssistida(serie);
                removeu = true;
            }
            if (controller.isDesejoAssistir(serie)) {
                controller.removerDesejoAssistir(serie);
                removeu = true;
            }
            if (removeu) {
                MensagensUtil.exibirSucesso(this, IdiomaUtil.get("msg.removida"));
            } else {
                MensagensUtil.exibirAtencao(this, "Esta serie nao esta em nenhuma lista.");
            }
        } catch (PersistenciaException e) {
            MensagensUtil.exibirErro(this, IdiomaUtil.get("msg.erroInesperado"));
            MensagensUtil.registrarErroTecnico(e);
        }
    }

    @Override
    public void atualizarTextos() {
        btnFavoritar.setText("⭐ " + IdiomaUtil.get("detalhes.botao.favoritar"));
        btnAssistida.setText("✅ " + IdiomaUtil.get("detalhes.botao.assistida"));
        btnDesejo.setText("📌 " + IdiomaUtil.get("detalhes.botao.desejo"));
        btnRemover.setText("🗑 " + IdiomaUtil.get("detalhes.botao.remover"));
        btnVoltar.setText("← " + IdiomaUtil.get("detalhes.botao.voltar"));
    }
}
