public class Contrato {

    private Inquilino inquilino;
    private Imovel imovel;

    private String dataInicio;
    private String dataTermino;
    private boolean encerrado;
    private int quantidadeMeses;

    public Contrato(Inquilino inquilino, Imovel imovel, String dataInicio, String dataTermino, int quantidadeMeses){
        this.inquilino = inquilino;
        this.imovel = imovel;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.quantidadeMeses = quantidadeMeses;
        this.encerrado = false;
    }

    public double calcularValorTotal(){
        return quantidadeMeses * imovel.getValorMensal();
    }

    public void encerrarContrato(){
        encerrado = true;
    }

    public boolean isEncerrado(){
        return encerrado;
    }

    public Inquilino getInquilino() {
        return inquilino;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void mostrarContrato(){
        System.out.println("------ CONTRATO ------");
        inquilino.mostrarDados();
        imovel.mostrarInformacoes();

        System.out.println("Data de início: " + dataInicio);
        System.out.println("Data de término: " + dataTermino);
        System.out.println("Contrato encerrado: " + encerrado);
        System.out.println("Valor total: R$" + calcularValorTotal());
        System.out.println("----------------------");
    }
}