package repository;

import model.Usuario;
import service.PersistenciaService;
import service.PersistenciaService.PersistenciaException;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsavel por gerenciar o acesso aos dados de Usuario,
 * funcionando como uma camada de repositorio entre o controller e o
 * servico de persistencia (PersistenciaService). Mantem os usuarios em
 * memoria durante a execucao e sincroniza com o arquivo JSON em disco.
 */
public class UsuarioRepository {

    private final PersistenciaService persistenciaService;
    private Map<String, Usuario> usuarios;

    public UsuarioRepository() {
        this.persistenciaService = new PersistenciaService();
        this.usuarios = new HashMap<>();
    }

    /**
     * Carrega todos os usuarios do arquivo JSON para a memoria.
     * Deve ser chamado uma vez ao iniciar o sistema.
     */
    public void carregar() throws PersistenciaException {
        this.usuarios = persistenciaService.carregarUsuarios();
    }

    /**
     * Busca um usuario pelo nome (case-insensitive). Caso nao exista,
     * cria automaticamente um novo usuario com aquele nome.
     */
    public Usuario buscarOuCriar(String nome) {
        String chave = nome.trim().toLowerCase();
        Usuario usuario = usuarios.get(chave);
        if (usuario == null) {
            usuario = new Usuario(nome.trim());
            usuarios.put(chave, usuario);
        }
        return usuario;
    }

    /**
     * Persiste o estado atual de todos os usuarios no arquivo JSON.
     * Deve ser chamado sempre que houver alteracao nas listas do usuario.
     */
    public void salvar() throws PersistenciaException {
        persistenciaService.salvarUsuarios(usuarios);
    }
}
