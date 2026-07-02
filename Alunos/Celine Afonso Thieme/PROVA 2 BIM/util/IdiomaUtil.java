package util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Classe utilitaria responsavel pela internacionalizacao (i18n) do sistema.
 * Carrega o arquivo de propriedades correspondente ao idioma selecionado e
 * disponibiliza metodos para obter textos traduzidos em qualquer parte da
 * interface grafica. Tambem permite registrar telas para serem atualizadas
 * automaticamente quando o idioma e trocado em tempo de execucao.
 */
public class IdiomaUtil {

    public static final String PT_BR = "pt_BR";
    public static final String EN = "en";
    public static final String ES = "es";

    private static ResourceBundle bundle;
    private static String idiomaAtual = PT_BR;
    private static final List<AtualizavelIdioma> ouvintes = new ArrayList<>();

    static {
        carregarIdioma(PT_BR);
    }

    private IdiomaUtil() {
        // Classe utilitaria - nao deve ser instanciada
    }

    /**
     * Carrega o ResourceBundle correspondente ao codigo de idioma informado.
     * Os arquivos .properties ficam em resources/i18n no classpath.
     */
    public static void carregarIdioma(String codigoIdioma) {
        try {
            String arquivo = "/resources/i18n/messages_" + codigoIdioma + ".properties";
            bundle = new PropertyResourceBundle(
                    new InputStreamReader(IdiomaUtil.class.getResourceAsStream(arquivo), StandardCharsets.UTF_8));
            idiomaAtual = codigoIdioma;
        } catch (Exception e) {
            // Caso o idioma nao seja encontrado, mantem portugues como padrao
            System.err.println("Falha ao carregar idioma " + codigoIdioma + ": " + e.getMessage());
            if (bundle == null) {
                idiomaAtual = PT_BR;
            }
        }
    }

    /**
     * Altera o idioma do sistema em tempo de execucao e notifica todas as
     * telas registradas para que atualizem seus textos imediatamente.
     */
    public static void setIdioma(String codigoIdioma) {
        carregarIdioma(codigoIdioma);
        notificarOuvintes();
    }

    public static String getIdiomaAtual() {
        return idiomaAtual;
    }

    /**
     * Obtem o texto traduzido correspondente a chave informada.
     * Caso a chave nao exista, retorna a propria chave entre colchetes,
     * facilitando identificar textos faltantes durante o desenvolvimento.
     */
    public static String get(String chave) {
        try {
            return bundle.getString(chave);
        } catch (Exception e) {
            return "[" + chave + "]";
        }
    }

    /**
     * Obtem um texto traduzido formatado com parametros (estilo String.format).
     */
    public static String get(String chave, Object... args) {
        try {
            return String.format(get(chave), args);
        } catch (Exception e) {
            return get(chave);
        }
    }

    /**
     * Registra uma tela para receber notificacoes de troca de idioma.
     */
    public static void registrarOuvinte(AtualizavelIdioma tela) {
        if (!ouvintes.contains(tela)) {
            ouvintes.add(tela);
        }
    }

    public static void removerOuvinte(AtualizavelIdioma tela) {
        ouvintes.remove(tela);
    }

    private static void notificarOuvintes() {
        for (AtualizavelIdioma tela : new ArrayList<>(ouvintes)) {
            tela.atualizarTextos();
        }
    }

    /**
     * Interface implementada pelas telas que precisam ser atualizadas
     * dinamicamente quando o usuario troca o idioma do sistema.
     */
    public interface AtualizavelIdioma {
        void atualizarTextos();
    }
}
