package digital.exchange.ingress.rest.server.aeron.infra;


import digital.exchange.sbe.me.input.*;
import digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientMgr;
import digital.exchange.ingress.rest.server.utils.SbeUtils;
import org.agrona.ExpandableDirectByteBuffer;

public class MeClusterSender
{
    public static boolean sendAddPairMatchingEngine(final String base, final String quote, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final AddPairMatchingEngineCommandEncoder addPairMatchingEngineCommandEncoder =
                new AddPairMatchingEngineCommandEncoder();

        addPairMatchingEngineCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(seqNum)
                .base(base)
                .quote(quote);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + addPairMatchingEngineCommandEncoder.encodedLength());
    }

    public static boolean sendRemovePairMatchingEngine(final String base, final String quote, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final RemovePairMatchingEngineCommandEncoder removePairMatchingEngineCommandEncoder =
                new RemovePairMatchingEngineCommandEncoder();

        removePairMatchingEngineCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(seqNum)
                .base(base)
                .quote(quote);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + removePairMatchingEngineCommandEncoder.encodedLength());
    }

    public static boolean sendAddOrder(final String base, final String quote, final long accountId, final String orderSide,
                                final String orderType, long price, final long qty, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final AddOrderCommandEncoder addOrderCommandEncoder = new AddOrderCommandEncoder();

        addOrderCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder);

        addOrderCommandEncoder.seqNum(seqNum);

        addOrderCommandEncoder.base(base);
        addOrderCommandEncoder.quote(quote);

        addOrderCommandEncoder.accountId(accountId);

        addOrderCommandEncoder.orderSide(SbeUtils.covertStringToSbeOrderSide(orderSide));
        addOrderCommandEncoder.orderType(SbeUtils.covertStringToSbeOrderType(orderType));

        addOrderCommandEncoder.price(price);
        addOrderCommandEncoder.qty(qty);

        return AeronClusterClientMgr.offer(buffer, MessageHeaderEncoder.ENCODED_LENGTH + addOrderCommandEncoder.encodedLength());
    }

    public static boolean sendCancelOrder(final String base, final String quote, final long accountId, final long orderId, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final CancelOrderCommandEncoder cancelOrderCommandEncoder = new CancelOrderCommandEncoder();

        cancelOrderCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(seqNum)
                .base(base)
                .quote(quote)
                .accountId(accountId)
                .orderId(orderId);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + cancelOrderCommandEncoder.encodedLength());
    }

    public static boolean sendAddAccount(final long accountId, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final AddAccountCommandEncoder addAccountCommandEncoder = new AddAccountCommandEncoder();

        addAccountCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder);

        addAccountCommandEncoder.seqNum(seqNum);
        addAccountCommandEncoder.accountId(accountId);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + addAccountCommandEncoder.encodedLength());
    }

    public static boolean sendRemoveAccount(final long accountId, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final RemoveAccountCommandEncoder removeAccountCommandEncoder = new RemoveAccountCommandEncoder();

        removeAccountCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder);

        removeAccountCommandEncoder.seqNum(seqNum);
        removeAccountCommandEncoder.accountId(accountId);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + removeAccountCommandEncoder.encodedLength());
    }

    public static boolean sendDeposit(final long accountId, final String symbol, final long qty, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final DepositCommandEncoder depositCommandEncoder = new DepositCommandEncoder();

        depositCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(seqNum)
                .accountId(accountId)
                .symbol(symbol)
                .qty(qty);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + depositCommandEncoder.encodedLength());
    }

    public static boolean sendWithdraw(final long accountId, final String symbol, final long qty, final long seqNum)
    {
        final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        final WithdrawCommandEncoder withdrawCommandEncoder = new WithdrawCommandEncoder();

        withdrawCommandEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(seqNum)
                .accountId(accountId)
                .symbol(symbol)
                .qty(qty);

        return AeronClusterClientMgr.offer(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + withdrawCommandEncoder.encodedLength());
    }
}
