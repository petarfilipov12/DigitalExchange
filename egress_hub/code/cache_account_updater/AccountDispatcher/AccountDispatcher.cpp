#include "AccountDispatcher.h"

#include <vector>
#include <tuple>

#include "AppConfig.h"

using namespace digital::exchange::sbe::me::output;

AccountDispatcher::AccountDispatcher():
redisManager(AppConfig::cacheAccountUri)
{}

bool AccountDispatcher::dispatch(char *buffer, int64_t offset, int64_t length)
{
    messageHeaderDecoder.wrap(buffer, offset, messageHeaderDecoder.actingVersion(), length);
    //length = messageHeaderDecoder.blockLength();

    switch (messageHeaderDecoder.templateId())
    {
    case MakerOrderCanceledEvent::SBE_TEMPLATE_ID:
        return dispatchOrderCandeledEvent(buffer, offset, length);
    
    case MakerOrderAddedEvent::SBE_TEMPLATE_ID:
        return dispatchOrderAddedEvent(buffer, offset, length);
    
    case OrderFillEvent::SBE_TEMPLATE_ID:
        return dispatchOrderFillEvent(buffer, offset, length);
    
    case AddedAccountEvent::SBE_TEMPLATE_ID:
        return dispatchAddedAccountEvent(buffer, offset, length);
    
    case RemovedAccountEvent::SBE_TEMPLATE_ID:
        return dispatchRemovedAccountEvent(buffer, offset, length);

    case DepositEvent::SBE_TEMPLATE_ID:
        return dispatchDepositEvent(buffer, offset, length);

    case WithdrawEvent::SBE_TEMPLATE_ID:
        return dispatchWithdrawEvent(buffer, offset, length);
    
    default:
        return true;
    }
}

bool AccountDispatcher::dispatchOrderCandeledEvent(char *buffer, int64_t offset, int64_t length)
{
    orderCanceledEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    auto order = orderCanceledEventDecoder.makerOrder();

    auto amount = order.qty() - order.filled();
    auto symbol = order.getBaseAsString();

    switch (order.orderSide())
    {
    case SbeOrderSide::Value::ORDER_SIDE_SELL:
        break;
    
    case SbeOrderSide::Value::ORDER_SIDE_BUY:
        amount = amount * order.price();
        symbol = order.getQuoteAsString();
        break;
    
    default:
        break;
    }

    redisManager.accountUpdate(order.accountId(), symbol, amount, (-1 * amount));

    return true;

}

bool AccountDispatcher::dispatchOrderAddedEvent(char *buffer, int64_t offset, int64_t length)
{
    orderAddedEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    auto order = orderAddedEventDecoder.makerOrder();

    auto amount = order.qty() - order.filled();
    auto symbol = order.getBaseAsString();

    switch (order.orderSide())
    {
    case SbeOrderSide::Value::ORDER_SIDE_SELL:
        break;
    
    case SbeOrderSide::Value::ORDER_SIDE_BUY:
        amount = amount * order.price();
        symbol = order.getQuoteAsString();
        break;
    
    default:
        break;
    }

    redisManager.accountUpdate(order.accountId(), symbol, (-1 * amount), amount);

    return true;
}

bool AccountDispatcher::dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length)
{
    orderFillEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    auto fillQty = orderFillEventDecoder.fillQty();

    auto quoteAmount = fillQty * orderFillEventDecoder.fillPrice();
    auto baseAmount = fillQty;
    auto base = orderFillEventDecoder.getBaseAsString();
    auto quote = orderFillEventDecoder.getQuoteAsString();

    std::vector<std::tuple<int64_t, std::string, int64_t, int64_t> > v;

    auto order = orderFillEventDecoder.makerOrder();
    auto accountId = order.accountId();
    auto orderSide = order.orderSide();

    if(orderSide == SbeOrderSide::Value::ORDER_SIDE_SELL)
    {
        //decreaseLockedSymbol = base
        //free = 0
        //locked = -fillQty
        v.push_back({accountId, base, 0, (-1 * baseAmount)});

        //depositSymbol = quote
        //free = + (fillQty * fillPrice)
        //locked = 0
        v.push_back({accountId, quote, quoteAmount, 0});
    }
    else if(orderSide == SbeOrderSide::Value::ORDER_SIDE_BUY)
    {
        //decreaseLockedSymbol = quote
        //free = 0
        //locked = -(fillQty * fillPrice)
        v.push_back({accountId, quote, 0, (-1 * quoteAmount)});

        //depositSymbol = base
        //free = + fillQty
        //locked = 0
        v.push_back({accountId, base, baseAmount, 0});
    }

    order = orderFillEventDecoder.takerOrder();
    accountId = order.accountId();
    orderSide = order.orderSide();

    if(orderSide == SbeOrderSide::Value::ORDER_SIDE_SELL)
    {
        //decreaseLockedSymbol = base
        //free = -fillQty
        //locked = 0
        v.push_back({accountId, base, (-1 * baseAmount), 0});

        //depositSymbol = quote
        //free = + (fillQty * fillPrice)
        //locked = 0
        v.push_back({accountId, quote, quoteAmount, 0});
    }
    else if(orderSide == SbeOrderSide::Value::ORDER_SIDE_BUY)
    {
        //decreaseLockedSymbol = quote
        //free = -(fillQty * fillPrice)
        //locked = 0
        v.push_back({accountId, quote, (-1 * quoteAmount), 0});

        //depositSymbol = base
        //free = + fillQty
        //locked = 0
        v.push_back({accountId, base, baseAmount, 0});
    }

    redisManager.accountUpdate(v);

    return true;
}

bool AccountDispatcher::dispatchAddedAccountEvent(char *buffer, int64_t offset, int64_t length)
{
    addedAccountEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    redisManager.addAccount(addedAccountEventDecoder.accountId());

    return true;
}

bool AccountDispatcher::dispatchRemovedAccountEvent(char *buffer, int64_t offset, int64_t length)
{
    removedAccountEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    redisManager.removeAccount(removedAccountEventDecoder.accountId());

    return true;
}

bool AccountDispatcher::dispatchDepositEvent(char *buffer, int64_t offset, int64_t length)
{
    depositEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    redisManager.accountUpdate(
        depositEventDecoder.accountId(),
        depositEventDecoder.getSymbolAsString(),
        depositEventDecoder.qty(),
        0
    );
    
    return true;
}

bool AccountDispatcher::dispatchWithdrawEvent(char *buffer, int64_t offset, int64_t length)
{
    withdrawEventDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    redisManager.accountUpdate(
        depositEventDecoder.accountId(),
        depositEventDecoder.getSymbolAsString(),
        (-1 * depositEventDecoder.qty()),
        0
    );

    return true;
}