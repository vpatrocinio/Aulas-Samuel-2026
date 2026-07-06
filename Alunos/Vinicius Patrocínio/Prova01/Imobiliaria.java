public class Imobiliaria {

    private Contrato[] contratos = new Contrato[10];
    private int quantidade;

    public void adicContrato(Contrato contrato){
        if(quantidade < contratos.length){
            contratos[quantidade] = contrato;
            quantidade++;
        } else {
            System.out.println("Não há espaço disponível para novos contratos.");
        }
    }

    public void listarContratosAtivos(){
        for (int i=0; i < quantidade; i++){
            Contrato contrato = contratos[i];
            if (!contrato.isEncerrado()){
                contrato.mostrarContrato();
            }
        }
    }

    public void encerrarContrato(int posicao){
        if (posicao >= 0 && posicao < quantidade){
            contratos[posicao].encerrarContrato();
        } else {
            System.out.println("Contrato selecionado inválido.");
        }
    }

    public void listarTodosContratos() {
        if (quantidade == 0) {
            System.out.println("Nenhum contrato cadastrado.");
            return;
        }
        System.out.println("\n===== TODOS OS CONTRATOS =====");

        for (int i = 0; i < quantidade; i++) {
            System.out.println("\nÍndice: " + i);
            contratos[i].mostrarContrato();

            if (contratos[i].isEncerrado()) {
                System.out.println("Situação: Encerrado");
            } else {
                System.out.println("Situação: Ativo");
            }
            System.out.println("------------------------------------");
        }
    }

    // Verifica se um inquilino já tem contrato ativo
    public boolean inquilinoTemContratoAtivo(Inquilino inquilino) {
        for (int i = 0; i < quantidade; i++) {
            if (!contratos[i].isEncerrado()) {
                if (contratos[i].getInquilino().getNome().equals(inquilino.getNome())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Verifica se um imóvel já tem contrato ativo
    public boolean imovelTemContratoAtivo(Imovel imovel) {
        for (int i = 0; i < quantidade; i++) {
            if (!contratos[i].isEncerrado()) {
                if (contratos[i].getImovel().getEndereco().equals(imovel.getEndereco())) {
                    return true;
                }
            }
        }
        return false;
    }
}