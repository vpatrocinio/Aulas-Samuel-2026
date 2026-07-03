package com.tvtracker.ui;

import com.tvtracker.model.UserData;
import com.tvtracker.service.StorageService;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * LOGIN DO SISTEMA
 * Tela inicial onde o usuário coloca o nome/apelido
 * e o sistema carrega ou cria os dados dele.
 */
public class LoginFrame extends JFrame {

    private final JTextField nameField;
    private final StorageService storageService;

    public LoginFrame() {
        super("TV Tracker - Login");

        // SERVIÇO QUE SALVA E CARREGA OS DADOS DO USUÁRIO (JSON)
        storageService = new StorageService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 230);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // TITULO DA TELA
        JLabel title = new JLabel("Bem-vindo(a) ao TV Tracker!");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(title, c);

        // TEXTO DESCRITIVO
        JLabel subtitle = new JLabel("Acompanhe suas séries favoritas usando a API do TVMaze.");
        c.gridy = 1;
        panel.add(subtitle, c);

        // LABEL DO CAMPO DE NOME
        JLabel label = new JLabel("Digite seu nome ou apelido:");
        c.gridy = 2;
        panel.add(label, c);

        // CAMPO ONDE O USUÁRIO DIGITA O NOME
        nameField = new JTextField();
        c.gridy = 3;
        panel.add(nameField, c);



        /*
        ////////////////////
        
             BOTAO ENTRAR
                LOGIN

        /////////////////// 
        */
        JButton enterButton = new JButton("Entrar");
        c.gridy = 4;
        c.gridwidth = 2;
        panel.add(enterButton, c);

        // AÇÃO DO BOTÃO E ENTER DO TECLADO
        enterButton.addActionListener(this::onEnter);
        nameField.addActionListener(this::onEnter);

        setContentPane(panel);
    }

    /**
     * LOGIN DO USUÁRIO (AÇÃO PRINCIPAL DA TELA)
     */
    private void onEnter(ActionEvent e) {

        try {

            String name = nameField.getText();




            /*
            //////////////////////////
            
                    VALIDAÇÕES          

            //////////////////////////
            */
            // VALIDAÇÃO: campo vazio
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor, digite um nome válido.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            name = name.trim();

            // VALIDAÇÃO: tamanho máximo do nome
            if (name.length() > 30) {
                JOptionPane.showMessageDialog(
                        this,
                        "O nome pode ter no máximo 30 caracteres.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VALIDAÇÃO: caracteres permitidos
            if (!name.matches("[a-zA-ZÀ-ÿ0-9 _-]+")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Use apenas letras, números, espaço, '_' ou '-'.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // CARREGA OU CRIA OS DADOS DO USUÁRIO (JSON)
            UserData userData = storageService.loadOrCreate(name);

            // ABRE A TELA PRINCIPAL DO SISTEMA
            MainFrame mainFrame = new MainFrame(userData, storageService);
            mainFrame.setVisible(true);

            // FECHA A TELA DE LOGIN
            dispose();

        } catch (Exception ex) {





            /*
            ///////////////
            
                 ERRO

            ///////////////
            */
            // ERRO GERAL AO ABRIR O SISTEMA
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao iniciar sessão: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}