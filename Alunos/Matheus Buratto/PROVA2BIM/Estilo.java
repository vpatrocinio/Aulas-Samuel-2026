import java.awt.Color;
import java.awt.Font;

// Constantes de cores e fontes usadas em toda a interface.

public final class Estilo {

    public static final Color FUNDO          = new Color(0xF7, 0xF2, 0xEA);
    public static final Color BARRA_TOPO     = new Color(0x21, 0x2B, 0x3D);
    public static final Color SUPERFICIE     = new Color(0xFF, 0xFD, 0xF9);
    public static final Color SUPERFICIE_ALT = new Color(0xF0, 0xE3, 0xD3);
    public static final Color DESTAQUE       = new Color(0xC1, 0x50, 0x2E);
    public static final Color DESTAQUE_ESC   = new Color(0xA1, 0x3F, 0x22);
    public static final Color ALERTA         = new Color(0xB3, 0x26, 0x1E);
    public static final Color TEXTO          = new Color(0x26, 0x22, 0x1C);
    public static final Color TEXTO_CLARO    = new Color(0xF2, 0xEC, 0xE2);
    public static final Color TEXTO_FRACO    = new Color(0x8A, 0x7E, 0x6E);
    public static final Color BORDA          = new Color(0xDD, 0xD0, 0xBC);

    public static final Font LOGO      = new Font("Serif", Font.BOLD, 24);
    public static final Font TITULO    = new Font("Serif", Font.BOLD, 20);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL    = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font PEQUENA   = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font NEGRITO   = new Font("SansSerif", Font.BOLD, 13);

    private Estilo() {}
}
