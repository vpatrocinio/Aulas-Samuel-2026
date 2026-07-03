package controller;

import model.Serie;
import model.Usuario;
import repository.UsuarioRepository;
import service.ApiTvMazeService;
import service.ApiTvMazeService.ApiException;
import service.PersistenciaService.PersistenciaException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe controladora central do sistema. Faz a ponte entre as telas
 * (camada View) e as camadas de servico/repositorio, concentrando as
 * regras de negocio (adicionar/remover series das listas, busca,
 * ordenacao, etc). As telas nunca acessam diretamente o repositorio
 * ou os servicos - sempre passam pelo controller.
 */
public class SistemaController {

    private final UsuarioRepository usuarioRepository;
    private final ApiTvMazeService apiTvMazeService;
    private Usuario usuarioAtual;

    public SistemaController() {
        this.usuarioRepository = new UsuarioRepository();
        this.apiTvMazeService = new ApiTvMazeService();
    }

    /**
     * Inicializa o sistema carregando os dados persistidos em disco.
     */
    public void inicializar() throws PersistenciaException {
        usuarioRepository.carregar();
    }

    /**
     * Realiza o login local: busca o usuario existente ou cria um novo.
     */
    public Usuario login(String nome) {
        this.usuarioAtual = usuarioRepository.buscarOuCriar(nome);
        return usuarioAtual;
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    /**
     * Altera o idioma preferido do usuario atual e persiste a alteracao.
     */
    public void alterarIdiomaUsuario(String codigoIdioma) {
        if (usuarioAtual != null) {
            usuarioAtual.setIdioma(codigoIdioma);
            salvarSilenciosamente();
        }
    }

    /**
     * Busca series na API TVMaze pelo termo informado.
     */
    public List<Serie> buscarSeries(String termo) throws ApiException {
        return apiTvMazeService.buscarSeries(termo);
    }

    // ---------------- Favoritos ----------------

    public boolean adicionarFavorito(Serie serie) throws PersistenciaException {
        boolean adicionou = usuarioAtual.adicionarFavorito(serie);
        if (adicionou) {
            usuarioRepository.salvar();
        }
        return adicionou;
    }

    public void removerFavorito(Serie serie) throws PersistenciaException {
        usuarioAtual.removerFavorito(serie);
        usuarioRepository.salvar();
    }

    public List<Serie> getFavoritos() {
        return new ArrayList<>(usuarioAtual.getFavoritos());
    }

    // ---------------- Assistidas ----------------

    public boolean adicionarAssistida(Serie serie) throws PersistenciaException {
        boolean adicionou = usuarioAtual.adicionarAssistida(serie);
        if (adicionou) {
            usuarioRepository.salvar();
        }
        return adicionou;
    }

    public void removerAssistida(Serie serie) throws PersistenciaException {
        usuarioAtual.removerAssistida(serie);
        usuarioRepository.salvar();
    }

    public List<Serie> getAssistidas() {
        return new ArrayList<>(usuarioAtual.getAssistidas());
    }

    // ---------------- Desejo Assistir ----------------

    public boolean adicionarDesejoAssistir(Serie serie) throws PersistenciaException {
        boolean adicionou = usuarioAtual.adicionarDesejoAssistir(serie);
        if (adicionou) {
            usuarioRepository.salvar();
        }
        return adicionou;
    }

    public void removerDesejoAssistir(Serie serie) throws PersistenciaException {
        usuarioAtual.removerDesejoAssistir(serie);
        usuarioRepository.salvar();
    }

    public List<Serie> getDesejoAssistir() {
        return new ArrayList<>(usuarioAtual.getDesejoAssistir());
    }

    // ---------------- Verificacoes de duplicidade ----------------

    public boolean isFavorito(Serie serie) {
        return usuarioAtual.isFavorito(serie);
    }

    public boolean isAssistida(Serie serie) {
        return usuarioAtual.isAssistida(serie);
    }

    public boolean isDesejoAssistir(Serie serie) {
        return usuarioAtual.isDesejoAssistir(serie);
    }

    // ---------------- Ordenacao ----------------

    /**
     * Tipos de ordenacao disponiveis para as listas de series.
     */
    public enum TipoOrdenacao {
        NOME_AZ, MELHOR_NOTA, PIOR_NOTA, STATUS, DATA_ESTREIA
    }

    /**
     * Ordena uma lista de series de acordo com o tipo de ordenacao escolhido,
     * utilizando Comparator conforme exigido pelos requisitos do sistema.
     */
    public void ordenar(List<Serie> series, TipoOrdenacao tipo) {
        Comparator<Serie> comparator;
        switch (tipo) {
            case MELHOR_NOTA:
                comparator = Comparator.comparing(Serie::getNota,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case PIOR_NOTA:
                comparator = Comparator.comparing(Serie::getNota,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case STATUS:
                comparator = Comparator.comparing(s -> ordemStatus(s.getStatus()));
                break;
            case DATA_ESTREIA:
                comparator = Comparator.comparing(Serie::getDataEstreia,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case NOME_AZ:
            default:
                comparator = Comparator.comparing(Serie::getNome, String.CASE_INSENSITIVE_ORDER);
                break;
        }
        series.sort(comparator);
    }

    /**
     * Define a ordem de prioridade dos status para ordenacao:
     * Running (em exibicao) primeiro, depois Ended, depois Canceled.
     */
    private int ordemStatus(String status) {
        if (status == null) return 3;
        switch (status) {
            case "Running": return 0;
            case "Ended": return 1;
            case "To Be Determined": return 2;
            case "Canceled": return 4;
            default: return 5;
        }
    }

    /**
     * Salva os dados sem lancar excecao para o chamador (usado em pontos
     * onde a falha de persistencia nao deve interromper o fluxo principal).
     */
    private void salvarSilenciosamente() {
        try {
            usuarioRepository.salvar();
        } catch (PersistenciaException e) {
            System.err.println("Falha ao salvar dados: " + e.getMessage());
        }
    }
}
