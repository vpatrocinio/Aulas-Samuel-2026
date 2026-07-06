public class Apartamento extends Imovel {

    private int andar;

    public Apartamento(String endereco, double valorMensal, int andar){
        super(endereco, valorMensal);
        this.andar = andar;
    }

    @Override
    public void mostrarInformacoes(){
        System.out.println("Tipo: Apartamento");
        System.out.println("Endereço: " + endereco);
        System.out.println("Valor mensal: R$" + valorMensal);
        System.out.println("Andar: " + andar);
    }
}
