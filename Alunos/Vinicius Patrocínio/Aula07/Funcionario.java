public class Funcionario extends Pessoa {

    protected Loja loja;
    protected double salarioBase;
    protected double[] salarioRecebido;

    public Funcionario(String nome, int idade, Endereco endereco, Loja loja, double salarioBase,
                       double[] salarioRecebido) {
        super(nome, idade, endereco);
        this.loja = loja;
        this.salarioBase = salarioBase;
        this.salarioRecebido = salarioRecebido;
    }

    public double calcularMedia() {
        double soma = 0;
        for (double salario : salarioRecebido) {
            soma += salario;
        }
        return soma / salarioRecebido.length;
    }

    public Loja getLoja() {
        return loja;
    }
    public double getSalarioBase() {
        return salarioBase;
    }
    public double[] getSalarioRecebido() {
        return salarioRecebido;
    }
    public void setLoja(Loja loja) {
        this.loja = loja;
    }
    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }
    public void setSalarioRecebido(double[] salarioRecebido) {
        this.salarioRecebido = salarioRecebido;
    }
}