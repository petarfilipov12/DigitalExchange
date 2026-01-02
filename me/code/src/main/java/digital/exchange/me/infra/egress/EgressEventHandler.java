package digital.exchange.me.infra.egress;

import digital.exchange.me.app.events.*;
import digital.exchange.me.infra.egress.archive.EgressArchivePublisher;
import digital.exchange.sbe.me.output.*;
import digital.exchange.me.utils.SbeMeOutUtils;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgressEventHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EgressEventHandler.class);

    private final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();
    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

    private final ClockUpdateEventEncoder clockUpdateEventEncoder = new ClockUpdateEventEncoder();

    private final AddedPairMatchingEngineEventEncoder  addedPairMatchingEngineEventEncoder =
            new AddedPairMatchingEngineEventEncoder();
    private final RemovedPairMatchingEngineEventEncoder removedPairMatchingEngineEventEncoder =
            new RemovedPairMatchingEngineEventEncoder();

    private final MakerOrderAddedEventEncoder makerOrderAddedEventEncoder = new MakerOrderAddedEventEncoder();
    private final MakerOrderCanceledEventEncoder makerOrderCanceledEventEncoder = new MakerOrderCanceledEventEncoder();
    private final OrderFillEventEncoder orderFillEventEncoder = new OrderFillEventEncoder();

    private final AddedAccountEventEncoder addedAccountEventEncoder = new AddedAccountEventEncoder();
    private final RemovedAccountEventEncoder removedAccountEventEncoder = new RemovedAccountEventEncoder();

    private final DepositEventEncoder depositEventEncoder = new DepositEventEncoder();
    private final WithdrawEventEncoder withdrawEventEncoder = new WithdrawEventEncoder();

    private final EgressPublisher egressPublisher;
    private final EgressArchivePublisher egressArchivePublisher;

    public EgressEventHandler(final EgressPublisher egressPublisher, final EgressArchivePublisher egressArchivePublisher)
    {
        this.egressPublisher = egressPublisher;
        this.egressArchivePublisher = egressArchivePublisher;
    }

    public void eventHandler(final Event event)
    {
        switch (event.getEventId())
        {
            case EVENT_ID_CLOCK_UPDATE -> handleClockUpdateEvent((EventClockUpdate) event);

            case EVENT_ID_ME_PAIR_ADDED -> handlePairAddedEvent((EventPairMEAdded) event);
            case EVENT_ID_ME_PAIR_REMOVED -> handlePairRemovedEvent((EventPairMERemoved) event);

            case EVENT_ID_MARKET_ORDER_ADDED -> handleMakerOrderAddedEvent((EventMakerOrderAdded) event);
            case EVENT_ID_MARKET_ORDER_CANCELED -> handleMakerOrderCanceledEvent((EventMakerOrderCanceled) event);
            case EVENT_ID_ORDER_FILL -> handleOrderFillEvent((EventOrderFill) event);

            case EVENT_ID_ACCOUNT_ADDED -> handleAccountAddedEvent((EventAccountAdded) event);
            case EVENT_ID_ACCOUNT_REMOVED -> handleAccountRemovedEvent((EventAccountRemoved) event);

            case EVENT_ID_DEPOSIT -> handleDepositEvent((EventDeposit) event);
            case EVENT_ID_WITHDRAW -> handleWithdrawEvent((EventWithdraw) event);

            default -> LOGGER.info("Unknown eventId: {}", event.getEventId());
        }
    }

    private void handleClockUpdateEvent(final EventClockUpdate eventClockUpdate)
    {
        clockUpdateEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventClockUpdate.getSeqNum())
                .timestamp(eventClockUpdate.getTimestamp());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + clockUpdateEventEncoder.encodedLength());
    }

    private void handlePairAddedEvent(final EventPairMEAdded eventPairMEAdded)
    {
        addedPairMatchingEngineEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventPairMEAdded.getSeqNum())
                .timestamp(eventPairMEAdded.getTimestamp())
                .base(eventPairMEAdded.getBase())
                .quote(eventPairMEAdded.getQuote());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + addedPairMatchingEngineEventEncoder.encodedLength());
    }

    private void handlePairRemovedEvent(final EventPairMERemoved eventPairMERemoved)
    {
        removedPairMatchingEngineEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventPairMERemoved.getSeqNum())
                .timestamp(eventPairMERemoved.getTimestamp())
                .base(eventPairMERemoved.getBase())
                .quote(eventPairMERemoved.getQuote());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + removedPairMatchingEngineEventEncoder.encodedLength());
    }

    private void handleMakerOrderAddedEvent(final EventMakerOrderAdded eventMakerOrderAdded)
    {
        makerOrderAddedEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventMakerOrderAdded.getSeqNum())
                .timestamp(eventMakerOrderAdded.getTimestamp());

        SbeMeOutUtils.convertOrderToSbeOrderEncoder(makerOrderAddedEventEncoder.makerOrder(),
                eventMakerOrderAdded.getMakerOrder());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + makerOrderAddedEventEncoder.encodedLength());
    }

    private void handleMakerOrderCanceledEvent(final EventMakerOrderCanceled eventMakerOrderCanceled)
    {
        makerOrderCanceledEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventMakerOrderCanceled.getSeqNum())
                .timestamp(eventMakerOrderCanceled.getTimestamp());

        SbeMeOutUtils.convertOrderToSbeOrderEncoder(makerOrderCanceledEventEncoder.makerOrder(),
                eventMakerOrderCanceled.getMakerOrder());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + makerOrderCanceledEventEncoder.encodedLength());
    }

    private void handleOrderFillEvent(final EventOrderFill eventOrderFill)
    {
        orderFillEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventOrderFill.getSeqNum())
                .timestamp(eventOrderFill.getTimestamp())
                .base(eventOrderFill.getBase())
                .quote(eventOrderFill.getQuote())
                .fillPrice(eventOrderFill.getFillPrice())
                .fillQty(eventOrderFill.getFillQty());

        SbeMeOutUtils.convertOrderToSbeOrderEncoder(orderFillEventEncoder.takerOrder(), eventOrderFill.getTakerOrder());
        SbeMeOutUtils.convertOrderToSbeOrderEncoder(orderFillEventEncoder.makerOrder(), eventOrderFill.getMakerOrder());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + orderFillEventEncoder.encodedLength());
    }

    private void handleAccountAddedEvent(final EventAccountAdded eventAccountAdded)
    {
        addedAccountEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventAccountAdded.getSeqNum())
                .timestamp(eventAccountAdded.getTimestamp())
                .accountId(eventAccountAdded.getAccountId());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + addedAccountEventEncoder.encodedLength());
    }

    private void handleAccountRemovedEvent(final EventAccountRemoved eventAccountRemoved)
    {
        removedAccountEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventAccountRemoved.getSeqNum())
                .timestamp(eventAccountRemoved.getTimestamp())
                .accountId(eventAccountRemoved.getAccountId());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + removedAccountEventEncoder.encodedLength());
    }

    private void handleDepositEvent(final EventDeposit eventDeposit)
    {
        depositEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventDeposit.getSeqNum())
                .timestamp(eventDeposit.getTimestamp())
                .accountId(eventDeposit.getAccountId())
                .symbol(eventDeposit.getSymbol())
                .qty(eventDeposit.getQty());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + depositEventEncoder.encodedLength());
    }

    private void handleWithdrawEvent(final EventWithdraw eventWithdraw)
    {
        withdrawEventEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .seqNum(eventWithdraw.getSeqNum())
                .timestamp(eventWithdraw.getTimestamp())
                .accountId(eventWithdraw.getAccountId())
                .symbol(eventWithdraw.getSymbol())
                .qty(eventWithdraw.getQty());

        publishEvent(buffer,
                MessageHeaderEncoder.ENCODED_LENGTH + withdrawEventEncoder.encodedLength());
    }

    private void publishEvent(final DirectBuffer buffer, final int length)
    {
        egressPublisher.publish(buffer, length);
        egressArchivePublisher.publish(buffer, length);
    }

}
