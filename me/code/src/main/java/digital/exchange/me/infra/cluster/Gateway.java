package digital.exchange.me.infra.cluster;

import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import digital.exchange.sbe.me.input.*;
import digital.exchange.me.app.matching.engine.OrderReturn;
import digital.exchange.me.app.AppLogic;
import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.test.MsgPrinter;
import digital.exchange.me.utils.SbeMeInUtils;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demultiplexes messages from the ingress stream to the appropriate domain handler.
 */
public class Gateway
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    private final AppLogic appLogic;
    private final ClientResponder clientResponder = new ClientResponder();

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    private final AddPairMatchingEngineCommandDecoder addPairMatchingEngineCommandDecoder =
            new AddPairMatchingEngineCommandDecoder();
    private final RemovePairMatchingEngineCommandDecoder removePairMatchingEngineCommandDecoder =
            new RemovePairMatchingEngineCommandDecoder();

    private final AddOrderCommandDecoder addOrderDecoder = new AddOrderCommandDecoder();
    private final CancelOrderCommandDecoder cancelOrderDecoder = new CancelOrderCommandDecoder();

    private final AddAccountCommandDecoder addAccountDecoder = new AddAccountCommandDecoder();
    private final RemoveAccountCommandDecoder removeAccountDecoder = new RemoveAccountCommandDecoder();

    private final DepositCommandDecoder depositCommandDecoder = new DepositCommandDecoder();
    private final WithdrawCommandDecoder withdrawCommandDecoder = new WithdrawCommandDecoder();

    public Gateway(final AppLogic appLogic)
    {
        this.appLogic = appLogic;
    }

    /**
     * Dispatch a message to the appropriate domain handler.
     *
     * @param buffer the buffer containing the inbound message, including a header
     * @param offset the offset to apply
     * @param length the length of the message
     */
    public void route(final DirectBuffer buffer, final int offset, final int length)
    {
        if (length < MessageHeaderDecoder.ENCODED_LENGTH)
        {
            LOGGER.error("Message too short, ignored.");
            return;
        }
        headerDecoder.wrap(buffer, offset);

        switch (headerDecoder.templateId())
        {
            case EgressSubscribeDecoder.TEMPLATE_ID -> routeEgressSubscribe();
            case EgressUnsubscribeDecoder.TEMPLATE_ID -> routeEgressUnsubscribe();

            case AddPairMatchingEngineCommandDecoder.TEMPLATE_ID -> routeAddPairCommand(buffer, offset);
            case RemovePairMatchingEngineCommandDecoder.TEMPLATE_ID -> routeRemovePairCommand(buffer, offset);

            case AddOrderCommandDecoder.TEMPLATE_ID -> routeAddOrderCommand(buffer, offset);
            case CancelOrderCommandDecoder.TEMPLATE_ID -> routeCancelOrderCommand(buffer, offset);

            case AddAccountCommandDecoder.TEMPLATE_ID -> routeAddAccountCommand(buffer, offset);
            case RemoveAccountCommandDecoder.TEMPLATE_ID -> routeRemoveAccountCommand(buffer, offset);

            case DepositCommandDecoder.TEMPLATE_ID -> routeDepositCommand(buffer, offset);
            case WithdrawCommandDecoder.TEMPLATE_ID -> routeWithdrawCommand(buffer, offset);

            default -> LOGGER.error("Unknown message template {}, ignored.", headerDecoder.templateId());
        }
    }

    private void routeEgressSubscribe()
    {
        RuntimeContext.getSubscribedSessions().addSession(
                RuntimeContext.getSessionContext().getCurrentSession());
    }

    private void routeEgressUnsubscribe()
    {
        RuntimeContext.getSubscribedSessions().removeSession(
                RuntimeContext.getSessionContext().getCurrentSession());
    }

    private void routeAddPairCommand(final DirectBuffer buffer, final int offset)
    {
        addPairMatchingEngineCommandDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_addMatchingEngine(
                addPairMatchingEngineCommandDecoder.seqNum(),
                addPairMatchingEngineCommandDecoder.base(),
                addPairMatchingEngineCommandDecoder.quote()
        );

        final ReturnEnum resp = appLogic.addMatchingEngine(
                addPairMatchingEngineCommandDecoder.base(),
                addPairMatchingEngineCommandDecoder.quote()
        );

        MsgPrinter.print_Response(addPairMatchingEngineCommandDecoder.seqNum(), resp);

        clientResponder.response(resp, addPairMatchingEngineCommandDecoder.seqNum());
    }

    private void routeRemovePairCommand(final DirectBuffer buffer, final int offset)
    {
        removePairMatchingEngineCommandDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_removeMatchingEngine(
                removePairMatchingEngineCommandDecoder.seqNum(),
                removePairMatchingEngineCommandDecoder.base(),
                removePairMatchingEngineCommandDecoder.quote()
        );

        final ReturnEnum resp = appLogic.removeMatchingEngine(
                removePairMatchingEngineCommandDecoder.base(),
                removePairMatchingEngineCommandDecoder.quote()
        );

        MsgPrinter.print_Response(removePairMatchingEngineCommandDecoder.seqNum(), resp);

        clientResponder.response(resp, removePairMatchingEngineCommandDecoder.seqNum());
    }

    private void routeAddOrderCommand(final DirectBuffer buffer, final int offset)
    {
        addOrderDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_addOrder(
                addOrderDecoder.seqNum(),
                addOrderDecoder.accountId(),
                addOrderDecoder.price(),
                addOrderDecoder.qty(),
                SbeMeInUtils.convertSbeOrderSideToNativeOrderSide(addOrderDecoder.orderSide()),
                SbeMeInUtils.convertSbeOrderTypeToNativeOrderType(addOrderDecoder.orderType()),
                addOrderDecoder.base(),
                addOrderDecoder.quote()
        );

        OrderReturn resp = appLogic.addOrder
        (
            addOrderDecoder.accountId(),
            addOrderDecoder.price(),
            addOrderDecoder.qty(),
            SbeMeInUtils.convertSbeOrderSideToNativeOrderSide(addOrderDecoder.orderSide()),
            SbeMeInUtils.convertSbeOrderTypeToNativeOrderType(addOrderDecoder.orderType()),
            addOrderDecoder.base(),
            addOrderDecoder.quote()
        );

        if(ReturnEnum.ORDER_ADDED_TO_ORDERBOOK == resp.returnEnum())
        {
            MsgPrinter.print_Response(addOrderDecoder.seqNum(), resp.returnEnum(), resp.order());
            clientResponder.response(resp.returnEnum(), resp.order(), addOrderDecoder.seqNum());
        }
        else
        {
            MsgPrinter.print_Response(addOrderDecoder.seqNum(), resp.returnEnum());
            clientResponder.response(resp.returnEnum(), addOrderDecoder.seqNum());
        }
    }

    private void routeCancelOrderCommand(final DirectBuffer buffer, final int offset)
    {
        cancelOrderDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_cancelOrder(
                cancelOrderDecoder.seqNum(),
                cancelOrderDecoder.accountId(),
                cancelOrderDecoder.orderId(),
                cancelOrderDecoder.base(),
                cancelOrderDecoder.quote()
        );

        OrderReturn resp = appLogic.cancelOrder(
                cancelOrderDecoder.accountId(),
                cancelOrderDecoder.orderId(),
                cancelOrderDecoder.base(),
                cancelOrderDecoder.quote()
        );

        if(null == resp.order())
        {
            MsgPrinter.print_Response(cancelOrderDecoder.seqNum(), resp.returnEnum());
            clientResponder.response(resp.returnEnum(), cancelOrderDecoder.seqNum());
        }
        else
        {
            MsgPrinter.print_Response(cancelOrderDecoder.seqNum(), resp.returnEnum(), resp.order());
            clientResponder.response(resp.returnEnum(), resp.order(), cancelOrderDecoder.seqNum());
        }
    }

    private void routeAddAccountCommand(final DirectBuffer buffer, final int offset)
    {
        addAccountDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_addAccount(addAccountDecoder.seqNum(), addAccountDecoder.accountId());

        ReturnEnum resp = appLogic.addAccount(addAccountDecoder.accountId());

        MsgPrinter.print_Response(addAccountDecoder.seqNum(), resp);

        clientResponder.response(resp, addAccountDecoder.seqNum());
    }

    private void routeRemoveAccountCommand(final DirectBuffer buffer, final int offset)
    {
        removeAccountDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_removeAccount(removeAccountDecoder.seqNum(), removeAccountDecoder.accountId());

        ReturnEnum resp = appLogic.removeAccount(removeAccountDecoder.accountId());

        MsgPrinter.print_Response(removeAccountDecoder.seqNum(), resp);

        clientResponder.response(resp, removeAccountDecoder.seqNum());
    }

    private void routeDepositCommand(final DirectBuffer buffer, final int offset)
    {
        depositCommandDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_deposit(
                depositCommandDecoder.seqNum(),
                depositCommandDecoder.accountId(),
                depositCommandDecoder.symbol(),
                depositCommandDecoder.qty()
        );

        ReturnEnum resp = appLogic.deposit(
                depositCommandDecoder.accountId(),
                depositCommandDecoder.symbol(),
                depositCommandDecoder.qty()
        );

        MsgPrinter.print_Response(depositCommandDecoder.seqNum(), resp);

        clientResponder.response(resp, depositCommandDecoder.seqNum());
    }

    private void routeWithdrawCommand(final DirectBuffer buffer, final int offset) {
        withdrawCommandDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                headerDecoder.blockLength(),
                headerDecoder.version()
        );

        MsgPrinter.print_withdraw(
                withdrawCommandDecoder.seqNum(),
                withdrawCommandDecoder.accountId(),
                withdrawCommandDecoder.symbol(),
                withdrawCommandDecoder.qty()
        );

        ReturnEnum resp = appLogic.withdraw(
                withdrawCommandDecoder.accountId(),
                withdrawCommandDecoder.symbol(),
                withdrawCommandDecoder.qty()
        );

        MsgPrinter.print_Response(withdrawCommandDecoder.seqNum(), resp);

        clientResponder.response(resp, withdrawCommandDecoder.seqNum());
    }
}
