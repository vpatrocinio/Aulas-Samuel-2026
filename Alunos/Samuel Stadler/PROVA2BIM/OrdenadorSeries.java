import java.util.Comparator;
import java.util.List;
public class OrdenadorSeries {

    public static void ordenarPorNome(List<Serie> lista) {
        lista.sort(Comparator.comparing(s -> s.getNome() == null ? "" : s.getNome().toLowerCase()));
    }

    public static void ordenarPorNota(List<Serie> lista) {
        // maior nota primeiro
        lista.sort((a, b) -> Double.compare(b.getNota(), a.getNota()));
    }

    public static void ordenarPorEstado(List<Serie> lista) {
        lista.sort(Comparator.comparing(s -> s.getEstado() == null ? "" : s.getEstado()));
    }

    public static void ordenarPorDataEstreia(List<Serie> lista) {
        lista.sort(Comparator.comparing(s -> s.getDataEstreia() == null ? "" : s.getDataEstreia()));
    }
}
