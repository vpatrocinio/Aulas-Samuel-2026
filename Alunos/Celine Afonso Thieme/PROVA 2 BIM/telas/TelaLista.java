package telas;

import controller.SistemaController;
import controller.SistemaController.TipoOrdenacao;
import model.Serie;
import service.PersistenciaService.PersistenciaException;
import util.IdiomaUtil;
import util.MensagensUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela generica para exibir e gerenciar as listas do usuario (Favoritos,
 * Assistidas e Desejo Assistir). Reutiliza a mesma estrutura para os tres
 * casos, evitando duplicacao de codigo (Open/Closed Principle).
 */
public class TelaLista extends JFrame implements IdiomaUtil.AtualizavelIdioma {

    /**
     * Enum que identifica qual lista esta sendo exibida nesta tela.
     */
    public enum TipoLista {
        FAVORITOS, ASSISTIDAS, DESEJO
    }

    private final SistemaController controller;
    private final TelaPrincipal telaPrincipal;
    private final TipoLista tipoLista;

    private List<Serie> seriesOriginais = new ArrayList<>();
    private List<Serie> seriesFiltradas = new ArrayList<>();

    private JLabel labelTitulo;
    private JTextField campoPesquisa;
    private JComboBox<String> comboOrdenacao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnRemover;
    private JLabel labelVazia;

    public TelaLista(SistemaController controller, TelaPrincipal telaPrincipal, TipoLista tipoLista) {
        this.controller = controller;
        this.telaPrincipal = telaPrincipal;
        this.tipoLista = tipoLista;
        IdiomaUtil.registrarOuvinte(this);
        configurarJanela();
        montarInterface();
        carregarLista();
    }

    private void configurarJanela() {
        setTitle(obterTituloLista());
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TemaUtil.centralizarJanela(this);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                IdiomaUtil.removerOuvinte(TelaLista.this);
                telaPrincipal.atualizarContadores();
            }
        });
    }

    private void montarInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 15));
        painelPrincipal.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Cabecalho
        labelTitulo = new JLabel(obterIcone() + " " + obterTituloLista());
        labelTitulo.setFont(TemaUtil.FONTE_TITULO);
        labelTitulo.setForeground(TemaUtil.TEXTO);

        // Controles de filtro e ordenacao
        JPanel painelControles = new JPanel(new BorderLayout(10, 0));
        painelControles.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelControles.setBorder(new EmptyBorder(10, 0, 5, 0));

        campoPesquisa = new JTextField();
        campoPesquisa.setToolTipText(IdiomaUtil.get("lista.pesquisar"));
        TemaUtil.estilizarCampoTexto(campoPesquisa);
        campoPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarLista(); }
            public void removeUpdate(DocumentEvent e) { filtrarLista(); }
            public void changedUpdate(DocumentEvent e) { filtrarLista(); }
        });

        JPanel painelOrdenacao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelOrdenacao.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        JLabel lblOrdenar = new JLabel(IdiomaUtil.get("lista.ordenar"));
        lblOrdenar.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        lblOrdenar.setFont(TemaUtil.FONTE_PADRAO);

        comboOrdenacao = new JComboBox<>(new String[]{
                IdiomaUtil.get("lista.ordenar.nome"),
                IdiomaUtil.get("lista.ordenar.melhorNota"),
                IdiomaUtil.get("lista.ordenar.piorNota"),
                IdiomaUtil.get("lista.ordenar.status"),
                IdiomaUtil.get("lista.ordenar.data")
        });
        comboOrdenacao.setFont(TemaUtil.FONTE_PADRAO);
        comboOrdenacao.setBackground(TemaUtil.CARD);
        comboOrdenacao.setForeground(TemaUtil.TEXTO);
        comboOrdenacao.addActionListener(e -> ordenarLista());

        painelOrdenacao.add(lblOrdenar);
        painelOrdenacao.add(comboOrdenacao);

        painelControles.add(campoPesquisa, BorderLayout.CENTER);
        painelControles.add(painelOrdenacao, BorderLayout.EAST);

        // Tabela
        String[] colunas = {
                IdiomaUtil.get("busca.coluna.nome"),
                IdiomaUtil.get("busca.coluna.idioma"),
                IdiomaUtil.get("busca.coluna.nota"),
                IdiomaUtil.get("busca.coluna.status"),
                IdiomaUtil.get("busca.coluna.estreia")
        };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        TemaUtil.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalhesSelecionado();
                }
            }
        });

        JScrollPane scroll = TemaUtil.criarScrollEstilizado(tabela);

        labelVazia = new JLabel(IdiomaUtil.get("lista.vazia"), SwingConstants.CENTER);
        labelVazia.setFont(TemaUtil.FONTE_PADRAO);
        labelVazia.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        labelVazia.setVisible(false);

        btnRemover = TemaUtil.criarBotaoSecundario("🗑 " + IdiomaUtil.get("lista.botao.remover"));
        btnRemover.setToolTipText(IdiomaUtil.get("lista.botao.remover"));
        btnRemover.addActionListener(e -> removerSelecionada());

        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelRodape.add(labelVazia, BorderLayout.CENTER);
        painelRodape.add(btnRemover, BorderLayout.EAST);

        JPanel painelCentro = new JPanel(new BorderLayout(0, 10));
        painelCentro.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelCentro.add(labelTitulo, BorderLayout.NORTH);
        painelCentro.add(painelControles, BorderLayout.CENTER);

        painelPrincipal.add(painelCentro, BorderLayout.NORTH);
        painelPrincipal.add(scroll, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private void carregarLista() {
        seriesOriginais = obterSeriesDaLista();
        seriesFiltradas = new ArrayList<>(seriesOriginais);
        preencherTabela(seriesFiltradas);
        labelVazia.setVisible(seriesOriginais.isEmpty());
    }

    private void preencherTabela(List<Serie> series) {
        modeloTabela.setRowCount(0);
        for (Serie s : series) {
            modeloTabela.addRow(new Object[]{
                    s.getNome(),
                    s.getIdioma(),
                    s.getNotaFormatada(),
                    s.getStatus(),
                    s.getDataEstreia() != null ? s.getDataEstreia() : "N/A"
            });
        }
    }

    private void filtrarLista() {
        String filtro = campoPesquisa.getText().toLowerCase().trim();
        seriesFiltradas = new ArrayList<>();
        for (Serie s : seriesOriginais) {
            if (s.getNome().toLowerCase().contains(filtro)) {
                seriesFiltradas.add(s);
            }
        }
        preencherTabela(seriesFiltradas);
        labelVazia.setVisible(seriesFiltradas.isEmpty());
    }

    private void ordenarLista() {
        TipoOrdenacao tipo;
        switch (comboOrdenacao.getSelectedIndex()) {
            case 1: tipo = TipoOrdenacao.MELHOR_NOTA; break;
            case 2: tipo = TipoOrdenacao.PIOR_NOTA; break;
            case 3: tipo = TipoOrdenacao.STATUS; break;
            case 4: tipo = TipoOrdenacao.DATA_ESTREIA; break;
            default: tipo = TipoOrdenacao.NOME_AZ; break;
        }
        controller.ordenar(seriesOriginais, tipo);
        filtrarLista(); // Reaplica o filtro com a nova ordem
    }

    private void abrirDetalhesSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= seriesFiltradas.size()) return;
        Serie serie = seriesFiltradas.get(linha);
        new TelaDetalhesSerie(controller, serie, this).setVisible(true);
    }

    private void removerSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= seriesFiltradas.size()) {
            MensagensUtil.exibirAtencao(this, "Selecione uma serie para remover.");
            return;
        }
        if (!MensagensUtil.confirmar(this, IdiomaUtil.get("msg.confirmarRemocao"))) {
            return;
        }
        Serie serie = seriesFiltradas.get(linha);
        try {
            switch (tipoLista) {
                case FAVORITOS: controller.removerFavorito(serie); break;
                case ASSISTIDAS: controller.removerAssistida(serie); break;
                case DESEJO: controller.removerDesejoAssistir(serie); break;
            }
            MensagensUtil.exibirSucesso(this, IdiomaUtil.get("msg.removida"));
            carregarLista();
        } catch (PersistenciaException e) {
            MensagensUtil.exibirErro(this, IdiomaUtil.get("msg.erroInesperado"));
            MensagensUtil.registrarErroTecnico(e);
        }
    }

    private List<Serie> obterSeriesDaLista() {
        switch (tipoLista) {
            case FAVORITOS: return controller.getFavoritos();
            case ASSISTIDAS: return controller.getAssistidas();
            case DESEJO: return controller.getDesejoAssistir();
            default: return new ArrayList<>();
        }
    }

    private String obterTituloLista() {
        switch (tipoLista) {
            case FAVORITOS: return IdiomaUtil.get("lista.titulo.favoritos");
            case ASSISTIDAS: return IdiomaUtil.get("lista.titulo.assistidas");
            case DESEJO: return IdiomaUtil.get("lista.titulo.desejo");
            default: return "";
        }
    }

    private String obterIcone() {
        switch (tipoLista) {
            case FAVORITOS: return "⭐";
            case ASSISTIDAS: return "✅";
            case DESEJO: return "📌";
            default: return "";
        }
    }

    @Override
    public void atualizarTextos() {
        setTitle(obterTituloLista());
        labelTitulo.setText(obterIcone() + " " + obterTituloLista());
        btnRemover.setText("🗑 " + IdiomaUtil.get("lista.botao.remover"));
        labelVazia.setText(IdiomaUtil.get("lista.vazia"));
        campoPesquisa.setToolTipText(IdiomaUtil.get("lista.pesquisar"));
    }
}
