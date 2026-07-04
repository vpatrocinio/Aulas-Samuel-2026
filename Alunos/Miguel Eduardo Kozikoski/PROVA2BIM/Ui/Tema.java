package Ui;

import javax.swing.*;

public class Tema {

    public static void aplicar(JButton botao) {
        botao.setBackground(TemasColors.VERMELHO);
        botao.setForeground(TemasColors.TEXTO);
        botao.setFocusPainted(false);
    }
}