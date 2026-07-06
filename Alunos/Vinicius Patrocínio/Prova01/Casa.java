public class Casa extends Imovel {

    private boolean quintal;

    public Casa(String endereco, double valorMensal, boolean quintal){
        super(endereco, valorMensal);
        this.quintal = quintal;
    }

    @Override
    public void mostrarInformacoes(){
        System.out.println("Tipo: Casa");
        System.out.println("Endereço: " + endereco);
        System.out.println("Valor mensal: R$" + valorMensal);

        if(quintal){
            System.out.println("Possui quintal: Sim");
        } else {
            System.out.println("Possui quintal: Não");
        }
    }
}
