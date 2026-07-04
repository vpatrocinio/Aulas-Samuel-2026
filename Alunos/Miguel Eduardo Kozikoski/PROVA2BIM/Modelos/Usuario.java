package Modelos;

import java.util.ArrayList;

public class Usuario {

    private String nome;
    private ArrayList<Serie> favoritos;
    private ArrayList<Serie> assistidas;
    private ArrayList<Serie> desejoAssistir;

    public Usuario(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.assistidas = new ArrayList<>();
        this.desejoAssistir = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Serie> getFavoritos() {
        return favoritos;
    }

    public ArrayList<Serie> getAssistidas() {
        return assistidas;
    }

    public ArrayList<Serie> getDesejoAssistir() {
        return desejoAssistir;
    }
}