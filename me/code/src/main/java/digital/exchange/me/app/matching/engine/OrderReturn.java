package digital.exchange.me.app.matching.engine;

import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.app.order.Order;

public record OrderReturn(ReturnEnum returnEnum, Order order) {}
