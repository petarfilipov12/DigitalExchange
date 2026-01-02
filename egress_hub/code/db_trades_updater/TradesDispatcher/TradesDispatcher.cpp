#include "TradesDispatcher.h"

#include "SbeUtilsMeOut.h"

using namespace digital::exchange::sbe::me::output;

TradesDispatcher::TradesDispatcher():
tradesDbManager()
{}

bool TradesDispatcher::dispatch(char *buffer, int64_t offset, int64_t length)
{
    messageHeaderDecoder.wrap(buffer, offset, messageHeaderDecoder.actingVersion(), length);

    switch (messageHeaderDecoder.templateId())
    {
    case AddedPairMatchingEngineEvent::SBE_TEMPLATE_ID:
        return dispatchAddedPairEvent(buffer, offset, length);

    case OrderFillEvent::SBE_TEMPLATE_ID:
        return dispatchOrderFillEvent(buffer, offset, length);
    
    default:
        return true;
    }
}

bool TradesDispatcher::dispatchAddedPairEvent(char *buffer, int64_t offset, int64_t length)
{
    addedPairEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    tradesDbManager.addPair(
        addedPairEventDecoder.getBaseAsString(), 
        addedPairEventDecoder.getQuoteAsString()
    );

    return true;
}

bool TradesDispatcher::dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length)
{
    orderFillEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    const Order makerOrder = orderFillEventDecoder.makerOrder();
    const Order takerOrder = orderFillEventDecoder.takerOrder();
    
    tradesDbManager.orderFill(
        orderFillEventDecoder.getBaseAsString(), 
        orderFillEventDecoder.getQuoteAsString(),
        orderFillEventDecoder.seqNum(),
        orderFillEventDecoder.timestamp(),
        takerOrder.accountId(),
        takerOrder.orderId(),
        SbeUtilsMeOut::convertSbeOrderSideToString(takerOrder.orderSide()),
        makerOrder.accountId(),
        makerOrder.orderId(),
        SbeUtilsMeOut::convertSbeOrderSideToString(makerOrder.orderSide()),
        orderFillEventDecoder.fillPrice(),
        orderFillEventDecoder.fillQty()
    );

    return true;
}