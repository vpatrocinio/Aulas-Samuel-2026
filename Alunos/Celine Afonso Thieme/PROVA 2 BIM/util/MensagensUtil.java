package util;

import javax.swing.*;
import java.awt.*;

/**
 * Classe utilitaria responsavel por exibir mensagens ao usuario atraves de
 * JOptionPane, ja com os textos traduzidos pelo IdiomaUtil. Centraliza todas
 * as caixas de dialogo do sistema, evitando repeticao de codigo.
 */
public class MensagensUtil {

    private MensagensUtil() {
        // Classe utilitaria - nao deve ser instanciada
    }

    /**
     * Exibe uma mensagem informativa de sucesso.
     */
    public static void exibirSucesso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, IdiomaUtil.get("msg.sucesso"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibe uma mensagem de erro amigavel. Detalhes tecnicos devem ser
     * registrados no console separadamente (ver registrarErroTecnico).
     */
    public static void exibirErro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, IdiomaUtil.get("msg.erro"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibe uma mensagem de atencao/alerta.
     */
    public static void exibirAtencao(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, IdiomaUtil.get("msg.atencao"),
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Exibe uma caixa de confirmacao (sim/nao) e retorna true se o usuario confirmou.
     */
    public static boolean confirmar(Component pai, String mensagem) {
        int opcao = JOptionPane.showConfirmDialog(pai, mensagem, IdiomaUtil.get("msg.confirmacao"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcao == JOptionPane.YES_OPTION;
    }

    /**
     * Registra detalhes tecnicos de uma excecao apenas no console,
     * nunca exibindo stacktrace diretamente ao usuario final.
     */
    public static void registrarErroTecnico(Exception e) {
        System.err.println("[ERRO TECNICO] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace();
    }
}
