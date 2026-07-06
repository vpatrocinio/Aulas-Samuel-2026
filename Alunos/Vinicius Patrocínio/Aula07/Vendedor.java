public class Vendedor extends Funcionario {

    public Vendedor(String nome, int idade, Endereco endereco, Loja loja, double salarioBase,
                    double[] salarioRecebido) {

        super(nome, idade, endereco, loja, salarioBase, salarioRecebido);
    }
    public double calcularBonus() {
        return salarioBase * 0.20;
    }
}