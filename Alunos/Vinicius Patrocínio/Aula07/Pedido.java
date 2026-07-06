import java.util.Date;

public class Pedido {

    private Date dataCriacao;
    private Date dataPagamento;
    private Date dataVencimentoReserva;
    private Cliente cliente;
    private Vendedor vendedor;
    private Loja loja;
    private Item[] itens;

    public Pedido(Date dataCriacao, Date dataPagamento, Date dataVencimentoReserva, Cliente cliente, Vendedor vendedor,
                  Loja loja,
                  Item[] itens) {

        this.dataCriacao = dataCriacao;
        this.dataPagamento = dataPagamento;
        this.dataVencimentoReserva = dataVencimentoReserva;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.loja = loja;
        this.itens = itens;
    }

    public double calcularValorTotal() {
        double total = 0;
        if (itens != null) {
            for (Item item : itens) {
                if (item != null) {
                    total += item.getValor();
                }
            }
        }
        return total;
    }

    public String gerarDescricaoVenda() {
        String descricao = "";
        descricao += "---- PEDIDO ----\n";
        descricao += "Cliente: " + cliente.getNome() + "\n";
        descricao += "Vendedor: " + vendedor.getNome() + "\n";
        descricao += "Loja: " + loja.getNomeFantasia() + "\n";
        descricao += "Valor Total: R$ " + calcularValorTotal() + "\n";
        return descricao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }
    public Date getDataPagamento() {
        return dataPagamento;
    }
    public Date getDataVencimentoReserva() {
        return dataVencimentoReserva;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Vendedor getVendedor() {
        return vendedor;
    }
    public Loja getLoja() {
        return loja;
    }
    public Item[] getItens() {
        return itens;
    }
    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    public void setDataVencimentoReserva(Date dataVencimentoReserva) {
        this.dataVencimentoReserva = dataVencimentoReserva;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }
    public void setLoja(Loja loja) {
        this.loja = loja;
    }
    public void setItens(Item[] itens) {
        this.itens = itens;
    }
}