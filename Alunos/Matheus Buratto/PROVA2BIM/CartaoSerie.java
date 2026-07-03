import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

// Ficha resumida (poster + nome + nota/estado) exibida no topo dos detalhes.

public class CartaoSerie extends JPanel {

    private static final int LARGURA_POSTER = 84;
    private static final int ALTURA_POSTER = 118;

    private final MiniPoster miniPoster = new MiniPoster();

    public CartaoSerie() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        miniPoster.setPreferredSize(new Dimension(LARGURA_POSTER, ALTURA_POSTER));
        add(miniPoster, BorderLayout.WEST);
    }

    public void exibir(SerieTV serie) {
        miniPoster.iniciais = iniciais(serie.getNome());
        miniPoster.definir(null);

        Image jaTem = GerenciadorImagens.doCache(serie.getPoster());
        if (jaTem != null) {
            miniPoster.definir(jaTem);
        } else if (!serie.getPoster().isBlank()) {
            new SwingWorker<Image, Void>() {
                @Override protected Image doInBackground() {
                    return GerenciadorImagens.baixar(serie.getPoster(), LARGURA_POSTER, ALTURA_POSTER);
                }
                @Override protected void done() {
                    try {
                        Image img = get();
                        if (img != null) miniPoster.definir(img);
                    } catch (Exception ignorado) {
                        // mantém o placeholder com as iniciais
                    }
                }
            }.execute();
        }
    }

    private static String iniciais(String nome) {
        StringBuilder sb = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (!parte.isEmpty()) sb.append(Character.toUpperCase(parte.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.isEmpty() ? "?" : sb.toString();
    }

    private static class MiniPoster extends JPanel {
        private Image imagem;
        private String iniciais = "?";

        MiniPoster() {
            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(Estilo.BORDA, 1));
        }

        void definir(Image img) { this.imagem = img; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (imagem != null) {
                g2.drawImage(imagem, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2.setColor(Estilo.SUPERFICIE_ALT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Estilo.DESTAQUE);
                g2.setFont(new Font("Serif", Font.BOLD, 26));
                int largura = g2.getFontMetrics().stringWidth(iniciais);
                g2.drawString(iniciais, (getWidth() - largura) / 2, getHeight() / 2 + 8);
            }
            g2.dispose();
        }
    }
}
