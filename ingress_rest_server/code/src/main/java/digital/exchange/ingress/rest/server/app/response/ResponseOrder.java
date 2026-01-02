package digital.exchange.ingress.rest.server.app.response;

import digital.exchange.ingress.rest.server.app.Order;

public class ResponseOrder extends Response
{
    private final Order order;

    public ResponseOrder(String response, Order order)
    {
        super(response);

        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
