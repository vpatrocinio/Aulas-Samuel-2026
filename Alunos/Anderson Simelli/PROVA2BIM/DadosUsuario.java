package fag;

import java.util.ArrayList;
import java.util.List;

// Esta classe representa os dados locais do usuário.
// Ela guarda o apelido e as três listas exigidas no enunciado:
// favoritos, séries assistidas e séries que deseja assistir.
public class DadosUsuario {

    private String apelido;
    private List<Serie> favoritos;
    private List<Serie> assistidas;
    private List<Serie> queroAssistir;

    public DadosUsuario() {
        this.apelido = "Usuário";
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.queroAssistir = new ArrayList<>();

        carregarDadosIniciais();
    }

    // Aqui estão os dados iniciais do sistema.
    // Quando o programa abre pela primeira vez, ele já possui algumas séries nas listas.
    private void carregarDadosIniciais() {
        favoritos.add(new Serie(
                82,
                "Game of Thrones",
                "English",
                "Drama, Adventure, Fantasy",
                8.9,
                "Ended",
                "2011-04-17",
                "2019-05-19",
                "HBO"
        ));

        assistidas.add(new Serie(
                169,
                "Breaking Bad",
                "English",
                "Drama, Crime, Thriller",
                9.2,
                "Ended",
                "2008-01-20",
                "2013-09-29",
                "AMC"
        ));

        queroAssistir.add(new Serie(
                431,
                "Friends",
                "English",
                "Comedy, Romance",
                8.5,
                "Ended",
                "1994-09-22",
                "2004-05-06",
                "NBC"
        ));
    }

    public String getApelido() {
        return apelido;
    }

    // Salva o apelido do usuário.
    // Se o campo estiver vazio, o sistema mantém "Usuário".
    public void setApelido(String apelido) {
        if (apelido == null || apelido.isBlank()) {
            this.apelido = "Usuário";
        } else {
            this.apelido = apelido.trim();
        }
    }

    // Retorna a lista de séries favoritas.
    public List<Serie> getFavoritos() {
        return favoritos;
    }

    // Retorna a lista de séries já assistidas.
    public List<Serie> getAssistidas() {
        return assistidas;
    }

    // Retorna a lista de séries que o usuário deseja assistir.
    public List<Serie> getQueroAssistir() {
        return queroAssistir;
    }

    // Método genérico para adicionar uma série em qualquer uma das três listas.
    // Ele recebe a lista e a série selecionada.
    public boolean adicionarSerie(List<Serie> lista, Serie serie) {
        if (lista == null || serie == null) {
            return false;
        }

        // Aqui o sistema impede duplicidade.
        // Se a série já estiver na lista, ela não será adicionada novamente.
        for (Serie s : lista) {
            if (s.getId() == serie.getId()) {
                return false;
            }
        }

        lista.add(serie);
        return true;
    }

    // Método genérico para remover uma série de qualquer uma das três listas.
    public boolean removerSerie(List<Serie> lista, Serie serie) {
        if (lista == null || serie == null) {
            return false;
        }

        // Remove a série comparando pelo ID.
        return lista.removeIf(s -> s.getId() == serie.getId());
    }
}