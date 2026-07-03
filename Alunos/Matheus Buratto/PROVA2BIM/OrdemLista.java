import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

// Enum com os critérios de ordenação (nome, nota, estado, estreia), cada um com seu comparador.

public enum OrdemLista {

    ALFABETICA("Nome (A-Z)") {
        @Override public Comparator<SerieTV> comparador() {
            Collator c = Collator.getInstance(new Locale("pt", "BR"));c.setStrength(Collator.PRIMARY);
            return (a, b) -> c.compare(a.getNome(), b.getNome());
        }
    },
    NOTA("Nota geral") {
        @Override public Comparator<SerieTV> comparador() {
            return Comparator.comparingDouble(SerieTV::notaParaOrdenar).reversed()
                    .thenComparing(SerieTV::getNome);
        }
    },
    ESTADO("Estado da série") {
        @Override public Comparator<SerieTV> comparador() {
            return Comparator.comparingInt(SerieTV::ordemEstado).thenComparing(SerieTV::getNome);
        }
    },
    ESTREIA("Data de estreia") {
        @Override public Comparator<SerieTV> comparador() {
            return (a, b) -> {
                String da = a.getEstreia(), db = b.getEstreia();
                if (da.isBlank() && db.isBlank()) return a.getNome().compareToIgnoreCase(b.getNome());
                if (da.isBlank()) return 1;
                if (db.isBlank()) return -1;
                return db.compareTo(da);
            };
        }
    };

    private final String rotulo;

    OrdemLista(String rotulo) { this.rotulo = rotulo; }

    public abstract Comparator<SerieTV> comparador();

    @Override
    public String toString() { return rotulo; }
}
