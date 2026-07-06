import java.util.Date;

public class ProcessaPedido {

    public boolean processar(Pedido pedido) {
        if (confirmarPagamento(pedido)) {
            System.out.println("Pagamento confirmado!");
            return true;
        } else {
            System.out.println("Reserva vencida. Pedido cancelado!");
            return false;
        }
    }

    private boolean confirmarPagamento(Pedido pedido) {
        Date hoje = new Date();
        return hoje.before(pedido.getDataVencimentoReserva())
                || hoje.equals(pedido.getDataVencimentoReserva());
    }
}