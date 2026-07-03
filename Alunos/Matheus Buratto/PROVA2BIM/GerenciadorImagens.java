import java.awt.Image;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

// Baixa e mantém em cache os posters das séries.

public final class GerenciadorImagens {

    private static final Map<String, Image> cache = new ConcurrentHashMap<>();

    private GerenciadorImagens() {}

    public static Image doCache(String url) {
        return url == null ? null : cache.get(url);
    }

    public static Image baixar(String url, int largura, int altura) {
        if (url == null || url.isBlank()) return null;
        Image existente = cache.get(url);
        if (existente != null) return existente;
        try {
            Image original = ImageIO.read(URI.create(url).toURL());
            if (original == null) return null;
            Image escalada = original.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            cache.put(url, escalada);
            return escalada;
        } catch (Exception falha) {
            return null;
        }
    }
}
