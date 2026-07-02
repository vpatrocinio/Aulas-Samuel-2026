package telas;

import controller.SistemaController;
import model.Usuario;
import util.IdiomaUtil;
import util.MensagensUtil;
import util.TemaUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Tela de login do sistema. Solicita nome/apelido do usuario e o idioma
 * preferido. Caso o usuario nao exista, ele e criado automaticamente;
 * caso exista, seus dados sao carregados normalmente.
 */
public class TelaLogin extends JFrame implements IdiomaUtil.AtualizavelIdioma {

    private final SistemaController controller;

    private JLabel labelTitulo;
    private JLabel labelSubtitulo;
    private JLabel labelCampoNome;
    private JLabel labelIdioma;
    private JTextField campoNome;
    private JComboBox<String> comboIdioma;
    private JButton botaoEntrar;
    private boolean idiomaAlteradoManualmente = false;

    public TelaLogin(SistemaController controller) {
        this.controller = controller;
        IdiomaUtil.registrarOuvinte(this);
        configurarJanela();
        montarInterface();
    }

    private void configurarJanela() {
        setTitle(IdiomaUtil.get("app.titulo"));
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        TemaUtil.centralizarJanela(this);
        getContentPane().setBackground(TemaUtil.FUNDO_PRINCIPAL);
    }

    private void montarInterface() {
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(TemaUtil.FUNDO_PRINCIPAL);
        painelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        labelTitulo = new JLabel(IdiomaUtil.get("login.titulo"));
        labelTitulo.setFont(TemaUtil.FONTE_TITULO);
        labelTitulo.setForeground(TemaUtil.TEXTO);
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        painelPrincipal.add(labelTitulo, gbc);

        labelSubtitulo = new JLabel(IdiomaUtil.get("login.subtitulo"));
        labelSubtitulo.setFont(TemaUtil.FONTE_PADRAO);
        labelSubtitulo.setForeground(TemaUtil.TEXTO_SECUNDARIO);
        labelSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        painelPrincipal.add(labelSubtitulo, gbc);

        labelCampoNome = new JLabel(IdiomaUtil.get("login.campo.nome"));
        labelCampoNome.setFont(TemaUtil.FONTE_PADRAO);
        labelCampoNome.setForeground(TemaUtil.TEXTO);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        painelPrincipal.add(labelCampoNome, gbc);

        campoNome = new JTextField();
        TemaUtil.estilizarCampoTexto(campoNome);
        campoNome.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        painelPrincipal.add(campoNome, gbc);

        labelIdioma = new JLabel(IdiomaUtil.get("login.idioma"));
        labelIdioma.setFont(TemaUtil.FONTE_PADRAO);
        labelIdioma.setForeground(TemaUtil.TEXTO);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 5, 0);
        painelPrincipal.add(labelIdioma, gbc);

        comboIdioma = new JComboBox<>(new String[]{
                "Portugues (Brasil)", "English", "Espanol"
        });
        comboIdioma.setFont(TemaUtil.FONTE_PADRAO);
        comboIdioma.setBackground(TemaUtil.CARD);
        comboIdioma.setForeground(TemaUtil.TEXTO);
        comboIdioma.addActionListener(this::trocarIdiomaSelecionado);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 25, 0);
        painelPrincipal.add(comboIdioma, gbc);

        botaoEntrar = TemaUtil.criarBotaoPrimario(IdiomaUtil.get("login.botao.entrar"));
        botaoEntrar.addActionListener(this::realizarLogin);
        gbc.gridy = 6;
        painelPrincipal.add(botaoEntrar, gbc);

        getRootPane().setDefaultButton(botaoEntrar);
        setContentPane(painelPrincipal);
    }

    private void trocarIdiomaSelecionado(ActionEvent e) {
        idiomaAlteradoManualmente = true;
        int indice = comboIdioma.getSelectedIndex();
        switch (indice) {
            case 1:
                IdiomaUtil.setIdioma(IdiomaUtil.EN);
                break;
            case 2:
                IdiomaUtil.setIdioma(IdiomaUtil.ES);
                break;
            default:
                IdiomaUtil.setIdioma(IdiomaUtil.PT_BR);
                break;
        }
    }

    private void realizarLogin(ActionEvent e) {
        String nome = campoNome.getText();
        if (nome == null || nome.trim().isEmpty()) {
            MensagensUtil.exibirAtencao(this, IdiomaUtil.get("login.erro.nomeVazio"));
            return;
        }
        Usuario usuario = controller.login(nome);
        // Se o usuario trocou o idioma manualmente no combo, essa escolha prevalece.
        // Caso contrario, aplica o idioma salvo do usuario (se ja existir).
        if (idiomaAlteradoManualmente) {
            usuario.setIdioma(IdiomaUtil.getIdiomaAtual());
        } else if (usuario.getIdioma() != null) {
            IdiomaUtil.setIdioma(usuario.getIdioma());
        } else {
            usuario.setIdioma(IdiomaUtil.getIdiomaAtual());
        }
        controller.alterarIdiomaUsuario(IdiomaUtil.getIdiomaAtual());

        IdiomaUtil.removerOuvinte(this);
        dispose();
        SwingUtilities.invokeLater(() -> new TelaPrincipal(controller).setVisible(true));
    }

    @Override
    public void atualizarTextos() {
        setTitle(IdiomaUtil.get("app.titulo"));
        labelTitulo.setText(IdiomaUtil.get("login.titulo"));
        labelSubtitulo.setText(IdiomaUtil.get("login.subtitulo"));
        labelCampoNome.setText(IdiomaUtil.get("login.campo.nome"));
        labelIdioma.setText(IdiomaUtil.get("login.idioma"));
        botaoEntrar.setText(IdiomaUtil.get("login.botao.entrar"));
    }
}
