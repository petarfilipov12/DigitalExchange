package digital.exchange.me.test;

import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgPrinter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgPrinter.class);

    public static void print_addMatchingEngine(final long seqNum, final String base, final String quote)
    {
        LOGGER.info("sqeNum: {} - IN - addMatchingEngine: base: {}, quote {}", seqNum, base, quote);
    }

    public static void print_removeMatchingEngine(final long seqNum, final String base, final String quote)
    {
        LOGGER.info("sqeNum: {} - IN - removeMatchingEngine: base: {}, quote {}", seqNum, base, quote);
    }

    public static void print_addOrder(
            final long seqNum,
            final long accountId,
             final long price,
             final long qty,
             final OrderSide orderSide,
             final OrderType orderType,
             final String base,
             final String quote
    )
    {
        LOGGER.info("sqeNum: {} - IN - addOrder: price: {}, qty: {}, orderSide: {}, orderType: {}, base: {}, quote {}",
                seqNum,
                price,
                qty,
                orderSide,
                orderType,
                base,
                quote
        );
    }

    public static void print_cancelOrder(final long seqNum, final long accountId, final long orderId,
                                         final String base, final String quote)
    {
        LOGGER.info("sqeNum: {} - IN - cancelOrder: accountId: {}, orderId: {}, base: {}, quote {}",
                seqNum,
                accountId,
                orderId,
                base,
                quote
        );
    }

    public static void print_addAccount(final long seqNum, final long accountId)
    {
        LOGGER.info("sqeNum: {} - IN - addAccount: accountId: {}", seqNum, accountId);
    }

    public static void print_removeAccount(final long seqNum, final long accountId)
    {
        LOGGER.info("sqeNum: {} - IN - removeAccount: accountId: {}", seqNum, accountId);
    }

    public static void print_deposit(final long seqNum, final long accountId, final String symbol, final long qty)
    {
        LOGGER.info("sqeNum: {} - IN - deposit: accountId: {}, symbol: {}, qty: {}", seqNum, accountId, symbol, qty);
    }

    public static void print_withdraw(final long seqNum, final long accountId, final String symbol, final long qty)
    {
        LOGGER.info("sqeNum: {} - IN - withdraw: accountId: {}, symbol: {}, qty: {}", seqNum, accountId, symbol, qty);
    }

    public static void print_Response(final long seqNum, final ReturnEnum ret)
    {
        LOGGER.info("sqeNum: {} - IN - Response: return: {}", seqNum, ret);
    }

    public static void print_Response(final long seqNum, final ReturnEnum ret, final Order order)
    {
        LOGGER.info("sqeNum: {} - IN - Response: return: {}, order: {}",
                seqNum,
                ret,
                order
        );
    }
}
