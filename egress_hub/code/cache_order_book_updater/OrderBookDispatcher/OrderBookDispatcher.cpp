#include "OrderBookDispatcher.h"

#include "AppConfig.h"

using namespace digital::exchange::sbe::me::output;

OrderBookDispatcher::OrderBookDispatcher():
redisManager(AppConfig::cacheOrderBookUri)
{}

bool OrderBookDispatcher::dispatch(char *buffer, int64_t offset, int64_t length)
{
    messageHeaderDecoder.wrap(buffer, offset, messageHeaderDecoder.actingVersion(), length);

    switch (messageHeaderDecoder.templateId())
    {
    case MakerOrderCanceledEvent::SBE_TEMPLATE_ID:
        return dispatchOrderCandeledEvent(buffer, offset, length);
    
    case MakerOrderAddedEvent::SBE_TEMPLATE_ID:
        return dispatchOrderAddedEvent(buffer, offset, length);
    
    case OrderFillEvent::SBE_TEMPLATE_ID:
        return dispatchOrderFillEvent(buffer, offset, length);
    
    default:
        return true;
    }
}

bool OrderBookDispatcher::dispatchOrderCandeledEvent(char *buffer, int64_t offset, int64_t length)
{
    orderCanceledEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    auto order = orderCanceledEventDecoder.makerOrder();

    switch (order.orderSide())
    {
    case SbeOrderSide::Value::ORDER_SIDE_BUY:
        redisManager.bidDecrease(
            order.getBaseAsString(), 
            order.getQuoteAsString(), 
            order.price(), 
            (order.qty() - order.filled()), 
            orderCanceledEventDecoder.seqNum()
        );
        break;
    
    case SbeOrderSide::Value::ORDER_SIDE_SELL:
        redisManager.askDecrease(
            order.getBaseAsString(), 
            order.getQuoteAsString(), 
            order.price(), 
            (order.qty() - order.filled()), 
            orderCanceledEventDecoder.seqNum()
        );
        break;
    
    default:
        break;
    }

    return true;
}

bool OrderBookDispatcher::dispatchOrderAddedEvent(char *buffer, int64_t offset, int64_t length)
{
    orderAddedEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    auto order = orderAddedEventDecoder.makerOrder();

    switch (order.orderSide())
    {
    case SbeOrderSide::Value::ORDER_SIDE_BUY:
        redisManager.bidIncrease(
            order.getBaseAsString(), 
            order.getQuoteAsString(), 
            order.price(), 
            (order.qty() - order.filled()), 
            orderAddedEventDecoder.seqNum()
        );
        break;
    
    case SbeOrderSide::Value::ORDER_SIDE_SELL:
        redisManager.askIncrease(
            order.getBaseAsString(), 
            order.getQuoteAsString(), 
            order.price(), 
            (order.qty() - order.filled()), 
            orderAddedEventDecoder.seqNum()
        );
        break;
    
    default:
        break;
    }

    return true;
}

bool OrderBookDispatcher::dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length)
{
    orderFillEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );
    
    switch (orderFillEventDecoder.makerOrder().orderSide())
    {
    case SbeOrderSide::Value::ORDER_SIDE_BUY:
        redisManager.bidDecrease(
            orderFillEventDecoder.getBaseAsString(), 
            orderFillEventDecoder.getQuoteAsString(), 
            orderFillEventDecoder.fillPrice(), 
            orderFillEventDecoder.fillQty(),
            orderFillEventDecoder.seqNum()
        );
        break;
    
    case SbeOrderSide::Value::ORDER_SIDE_SELL:
        redisManager.askDecrease(
            orderFillEventDecoder.getBaseAsString(), 
            orderFillEventDecoder.getQuoteAsString(), 
            orderFillEventDecoder.fillPrice(), 
            orderFillEventDecoder.fillQty(),
            orderFillEventDecoder.seqNum()
        );
        break;
    
    default:
        break;
    }

    return true;
}