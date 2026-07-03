package telas;

import controller.SistemaController;
import model.Serie;
import service.ApiTvMazeService.ApiException;
import util.IdiomaUtil;
import util.MensagensUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de busca de series. Consome a API TVMaze com base no termo digitado
 * pelo usuario e exibe os resultados em uma JTable estilizada. O duplo clique
 * em uma linha abre os detalhes completos da serie selecionada.
 */
public class TelaBusca extends JFrame implements IdiomaUtil.AtualizavelIdioma {

    private final SistemaController controller;
    private final TelaPrincipal telaPrincipal;
    private List<Serie> seriesResultado = new ArrayList<>();

    private JLabel labelTitulo;
    private JTextField campoBusca;
    private JButton botaoBuscar;
    private JLabel labelDica;
    private JTable tabelaResultados;
    private DefaultTableModel modeloTabela;
    private JLabel labelStatus;

    public TelaBusca(SistemaController controller, TelaPrincipal telaPrincipal) {
        this.controller = controller;
        this.telaPrincipal = telaPrincipal;
        IdiomaUtil.registrarOuvinte(this);
        configurarJanela();
        montarInterface();
    }

    private void configurarJanela() {
        setTitle(IdiomaUtil.get("busca.titulo"));
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TemaUtil.centralizarJanela(this);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                IdiomaUtil.removerOuvinte(TelaBusca.this);
                telaPrincipal.atualizarContadores();
            }
        });
    }

    private void montarInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 15));
        painelPrincipal.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Cabecalho
        labelTitulo = new JLabel("🔍 " + IdiomaUtil.get("busca.titulo"));
        labelTitulo.setFont(TemaUtil.FONTE_TITULO);
        labelTitulo.setForeground(TemaUtil.TEXTO);
        painelPrincipal.add(labelTitulo, BorderLayout.NORTH);

        // Painel de busca
        JPanel painelBusca = new JPanel(new BorderLayout(10, 0));
        painelBusca.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelBusca.setBorder(new EmptyBorder(10, 0, 5, 0));

        campoBusca = new JTextField();
        campoBusca.setToolTipText(IdiomaUtil.get("busca.campo.placeholder"));
        TemaUtil.estilizarCampoTexto(campoBusca);
        campoBusca.addActionListener(e -> realizarBusca());

        botaoBuscar = TemaUtil.criarBotaoPrimario("🔍 " + IdiomaUtil.get("busca.botao.buscar"));
        botaoBuscar.setToolTipText(IdiomaUtil.get("busca.botao.buscar"));
        botaoBuscar.addActionListener(e -> realizarBusca());

        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(botaoBuscar, BorderLayout.EAST);
        painelPrincipal.add(painelBusca, BorderLayout.CENTER);

        // Tabela de resultados
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
                return false; // Impede edicao direta nas celulas
            }
        };
        tabelaResultados = new JTable(modeloTabela);
        TemaUtil.estilizarTabela(tabelaResultados);
        tabelaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaResultados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalhesSelecionado();
                }
            }
        });

        JScrollPane scroll = TemaUtil.criarScrollEstilizado(tabelaResultados);

        labelDica = new JLabel(IdiomaUtil.get("busca.dica"));
        labelDica.setFont(TemaUtil.FONTE_PADRAO.deriveFont(12f));
        labelDica.setForeground(TemaUtil.TEXTO_SECUNDARIO);

        labelStatus = new JLabel(" ");
        labelStatus.setFont(TemaUtil.FONTE_PADRAO.deriveFont(12f));
        labelStatus.setForeground(TemaUtil.TEXTO_SECUNDARIO);

        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelRodape.add(labelDica, BorderLayout.WEST);
        painelRodape.add(labelStatus, BorderLayout.EAST);

        JPanel painelTabela = new JPanel(new BorderLayout(0, 8));
        painelTabela.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelTabela.add(scroll, BorderLayout.CENTER);
        painelTabela.add(painelRodape, BorderLayout.SOUTH);

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelCentro.add(painelBusca, BorderLayout.NORTH);
        painelCentro.add(painelTabela, BorderLayout.CENTER);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);

        setContentPane(painelPrincipal);
    }

    private void realizarBusca() {
        String termo = campoBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            MensagensUtil.exibirAtencao(this, IdiomaUtil.get("msg.campoVazio"));
            return;
        }
        modeloTabela.setRowCount(0);
        seriesResultado.clear();
        labelStatus.setText(IdiomaUtil.get("busca.carregando"));
        botaoBuscar.setEnabled(false);

        SwingWorker<List<Serie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Serie> doInBackground() throws ApiException {
                return controller.buscarSeries(termo);
            }

            @Override
            protected void done() {
                botaoBuscar.setEnabled(true);
                try {
                    List<Serie> series = get();
                    seriesResultado = series;
                    if (series.isEmpty()) {
                        labelStatus.setText(IdiomaUtil.get("busca.semResultados"));
                    } else {
                        labelStatus.setText(series.size() + " resultado(s)");
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
                } catch (Exception e) {
                    labelStatus.setText("");
                    String msg = e.getCause() instanceof ApiException
                            ? e.getCause().getMessage() : IdiomaUtil.get("msg.erroInesperado");
                    MensagensUtil.exibirErro(TelaBusca.this, msg);
                    MensagensUtil.registrarErroTecnico((Exception) e.getCause());
                }
            }
        };
        worker.execute();
    }

    private void abrirDetalhesSelecionado() {
        int linha = tabelaResultados.getSelectedRow();
        if (linha < 0 || linha >= seriesResultado.size()) {
            return;
        }
        Serie serieSelecionada = seriesResultado.get(linha);
        new TelaDetalhesSerie(controller, serieSelecionada, this).setVisible(true);
    }

    @Override
    public void atualizarTextos() {
        setTitle(IdiomaUtil.get("busca.titulo"));
        labelTitulo.setText("🔍 " + IdiomaUtil.get("busca.titulo"));
        botaoBuscar.setText("🔍 " + IdiomaUtil.get("busca.botao.buscar"));
        campoBusca.setToolTipText(IdiomaUtil.get("busca.campo.placeholder"));
        labelDica.setText(IdiomaUtil.get("busca.dica"));

        // Atualiza cabecalhos da tabela
        String[] colunas = {
                IdiomaUtil.get("busca.coluna.nome"),
                IdiomaUtil.get("busca.coluna.idioma"),
                IdiomaUtil.get("busca.coluna.nota"),
                IdiomaUtil.get("busca.coluna.status"),
                IdiomaUtil.get("busca.coluna.estreia")
        };
        for (int i = 0; i < colunas.length; i++) {
            modeloTabela.getColumnName(i);
            tabelaResultados.getColumnModel().getColumn(i).setHeaderValue(colunas[i]);
        }
        tabelaResultados.getTableHeader().repaint();
    }
}
