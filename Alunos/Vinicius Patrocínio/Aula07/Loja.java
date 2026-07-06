public class Loja {

    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private Endereco endereco;
    private Vendedor[] vendedores;
    private Cliente[] clientes;

    public Loja(String nomeFantasia, String razaoSocial, String cnpj, Endereco endereco, Vendedor[] vendedores,
                Cliente[] clientes) {
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.vendedores = vendedores;
        this.clientes = clientes;
    }

    public int contarClientes() {
        if (clientes == null) {
            return 0;
        }
        return clientes.length;
    }

    public int contarVendedores() {
        if (vendedores == null) {
            return 0;
        }
        return vendedores.length;
    }

    public void apresentar() {
        System.out.println("---- LOJA ----");
        System.out.println("Nome Fantasia: " + nomeFantasia);
        System.out.println("Razão Social: " + razaoSocial);
        System.out.println("CNPJ: " + cnpj);
        System.out.println("Endereço: " + endereco.apresentarLogradouro());
        System.out.println("Quantidade de Clientes: " + contarClientes());
        System.out.println("Quantidade de Vendedores: " + contarVendedores());
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }
    public String getRazaoSocial() {
        return razaoSocial;
    }
    public String getCnpj() {
        return cnpj;
    }
    public Endereco getEndereco() {
        return endereco;
    }
    public Vendedor[] getVendedores() {
        return vendedores;
    }
    public Cliente[] getClientes() {
        return clientes;
    }
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    public void setVendedores(Vendedor[] vendedores) {
        this.vendedores = vendedores;
    }
    public void setClientes(Cliente[] clientes) {
        this.clientes = clientes;
    }
}