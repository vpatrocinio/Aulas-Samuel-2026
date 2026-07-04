package Ui;

import Modelos.Serie;
import Modelos.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TelaListas extends JFrame {

    private Usuario usuario;

    public TelaListas(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Minhas Listas");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 3));

        add(criarPainelLista("Favoritos", usuario.getFavoritos()));
        add(criarPainelLista("Já Assistidas", usuario.getAssistidas()));
        add(criarPainelLista("Desejo Assistir", usuario.getDesejoAssistir()));
    }

    private JPanel criarPainelLista(String titulo, ArrayList<Serie> lista) {
        JPanel painel = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);

        DefaultListModel<Serie> modelo = new DefaultListModel<>();
        atualizarModelo(modelo, lista);

        JList<Serie> jList = new JList<>(modelo);

        JButton btnRemover = new JButton("Remover");
        JButton btnNome = new JButton("Ordenar Nome");
        JButton btnStatus = new JButton("Ordenar Status");
        JButton btnEstreia = new JButton("Ordenar Estreia");

        JPanel painelBotoes = new JPanel(new GridLayout(4, 1));
        painelBotoes.add(btnNome);
        painelBotoes.add(btnStatus);
        painelBotoes.add(btnEstreia);
        painelBotoes.add(btnRemover);

        //botãos de separação na lista
        btnNome.addActionListener(e -> {
            lista.sort((s1, s2) -> s1.getNome().compareToIgnoreCase(s2.getNome()));
            atualizarModelo(modelo, lista);
        });

        btnNome.addActionListener(e -> {
            lista.sort((s1, s2)  ->Double.compare (s2.getNota(),s1.getNota()));
            atualizarModelo(modelo, lista);
        });

        btnStatus.addActionListener(e -> {
            lista.sort((s1, s2) -> s1.getStatus().compareToIgnoreCase(s2.getStatus()));
            atualizarModelo(modelo, lista);
        });

        btnEstreia.addActionListener(e -> {
            lista.sort((s1, s2) -> s1.getEstreia().compareToIgnoreCase(s2.getEstreia()));
            atualizarModelo(modelo, lista);
        });

        btnRemover.addActionListener(e -> {
            Serie selecionada = jList.getSelectedValue();

            if (selecionada != null) {
                lista.remove(selecionada);
                modelo.removeElement(selecionada);
                JOptionPane.showMessageDialog(this, "Série removida!");
                Service.JsonService.salvarUsuario(usuario);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma série para remover.");
            }
        });

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(jList), BorderLayout.CENTER);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }private void atualizarModelo(DefaultListModel<Serie> modelo, ArrayList<Serie> lista) {
        modelo.clear();

        for (Serie s : lista) {
            modelo.addElement(s);
        }
    }
}