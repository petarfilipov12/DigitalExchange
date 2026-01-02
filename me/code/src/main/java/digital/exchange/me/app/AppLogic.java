package digital.exchange.me.app;

import digital.exchange.me.app.account.SymbolAmount;
import digital.exchange.me.app.event.emitter.EventEmitter;
import digital.exchange.me.app.events.*;
import digital.exchange.me.app.matching.engine.MatchingEngine;
import digital.exchange.me.app.matching.engine.MatchingEngineMgr;
import digital.exchange.me.app.matching.engine.OrderReturn;
import digital.exchange.me.app.account.Account;
import digital.exchange.me.app.account.AccountMgr;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;
import digital.exchange.me.utils.OrderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogic
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogic.class);

    private final EventEmitter eventEmitter;
    private final MatchingEngineMgr matchingEngineMgr;
    private final AccountMgr accountMgr;

    private Account currentAccount;

    private long order_id = 0;

    public AppLogic(final EventEmitter eventEmitter)
    {
        this.eventEmitter = eventEmitter;

        this.matchingEngineMgr = new MatchingEngineMgr(this.eventEmitter);;
        this.accountMgr = new AccountMgr();
    }

    public ReturnEnum addMatchingEngine(final String base, final String quote)
    {
        if(matchingEngineMgr.addMatchingEngine(base, quote))
        {
            eventEmitter.emitEvent(new EventPairMEAdded(base, quote));

            return ReturnEnum.OK;
        }

        return ReturnEnum.MATCHING_ENGINE_EXISTS;
    }

    public ReturnEnum removeMatchingEngine(final String base, final String quote)
    {
        if(matchingEngineMgr.removeMatchingEngine(base, quote))
        {
            eventEmitter.emitEvent(new EventPairMERemoved(base, quote));

            return ReturnEnum.OK;
        }

        return ReturnEnum.MATCHING_ENGINE_NOT_EXIST;
    }

    public OrderReturn addOrder(final long accountId,
                                final long price,
                                final long qty,
                                final OrderSide orderSide,
                                final OrderType orderType,
                                final String base,
                                final String quote
    )
    {
        currentAccount = accountMgr.getAccount(accountId);
        if(currentAccount == null)
        {
            return new OrderReturn(ReturnEnum.ACCOUNT_NOT_EXIST, null);
        }

        MatchingEngine matchingEngine = matchingEngineMgr.getMatchingEngine(base, quote);
        if(matchingEngine == null)
        {
            return new OrderReturn(ReturnEnum.MATCHING_ENGINE_NOT_EXIST, null);
        }

        SymbolAmount baseAmount = currentAccount.getSymbolAmount(base);
        if(baseAmount == null)
        {
            return new OrderReturn(ReturnEnum.ACCOUNT_SYMBOL_NOT_EXIST, null);
        }

        SymbolAmount quoteAmount = currentAccount.getSymbolAmount(quote);
        if(quoteAmount == null)
        {
            return new OrderReturn(ReturnEnum.ACCOUNT_SYMBOL_NOT_EXIST, null);
        }


        if(orderSide == OrderSide.ORDER_SIDE_SELL)
        {
            if(!baseAmount.lockAmount(qty))
            {
                return new OrderReturn(ReturnEnum.ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS, null);
            }
        }
        else if(orderSide == OrderSide.ORDER_SIDE_BUY)
        {
            if(!quoteAmount.lockAmount(price))
            {
                return new OrderReturn(ReturnEnum.ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS, null);
            }
        }
        else
        {
            //TODO order invalid, should not get here
            LOGGER.error("addOrder - Order invalid, orderSide: {}",
                    OrderUtils.convertOrderSideToString(orderSide));
        }

        Order order = new Order(order_id, accountId, orderSide, orderType, price, qty, 0L, base, quote);
        order_id++;

        ReturnEnum ret = matchingEngine.addOrder(order);

        if( (ret == ReturnEnum.ORDER_INVALID) || (ret == ReturnEnum.ORDER_EXISTS) || (ret == ReturnEnum.ORDER_REJECTED) )
        {
            if(orderSide == OrderSide.ORDER_SIDE_SELL)
            {
                baseAmount.freeLockedAmount(qty);
            }
            else if(orderSide == OrderSide.ORDER_SIDE_BUY)
            {
                quoteAmount.freeLockedAmount(price);
            }
            else
            {
                //TODO order invalid, should not get here
                LOGGER.error("addOrder - Order invalid, orderSide: {}",
                        OrderUtils.convertOrderSideToString(orderSide));
            }
        }

        if(ReturnEnum.ORDER_ADDED_TO_ORDERBOOK == ret)
        {
            eventEmitter.emitEvent(new EventMakerOrderAdded(order));
        }

        return new OrderReturn(ret, order);
    }

    public OrderReturn cancelOrder(final long accountId, final long orderId, final String base, final String quote)
    {
        MatchingEngine matchingEngine = matchingEngineMgr.getMatchingEngine(base, quote);
        if(matchingEngine == null)
        {
            return new OrderReturn(ReturnEnum.MATCHING_ENGINE_NOT_EXIST, null);
        }

        currentAccount = accountMgr.getAccount(accountId);
        if(currentAccount == null)
        {
            return new OrderReturn(ReturnEnum.ACCOUNT_NOT_EXIST, null);
        }

        OrderReturn ret = matchingEngine.cancelOrder(orderId);

        if(ret.returnEnum() == ReturnEnum.ORDER_CANCELED)
        {
            long amount = ret.order().getQty() - ret.order().getFilled();
            final String symbol;

            switch (ret.order().getOrderSide())
            {
                case OrderSide.ORDER_SIDE_SELL -> symbol = ret.order().getBase();

                case OrderSide.ORDER_SIDE_BUY -> {
                    amount = amount * ret.order().getPrice();
                    symbol = ret.order().getQuote();
                }

                default -> symbol = null;
            }

            if(symbol != null)
            {
                if(tryFreeLockedAmount(currentAccount, symbol, amount))
                {
                    eventEmitter.emitEvent(new EventMakerOrderCanceled(ret.order()));
                }
                else
                {
                    //TODO could not free. should not get here
                    LOGGER.error("cancelOrder - Could not free locked amount, accountId: {}, symbol: {}, amount: {}",
                            currentAccount.getAccountId(), symbol, amount);
                }

            }
            else
            {
                //TODO order invalid, should not get here
                LOGGER.error("cancelOrder - Order invalid, orderSide: {}",
                        OrderUtils.convertOrderSideToString(ret.order().getOrderSide()));
            }
        }

        return ret;
    }

    public ReturnEnum addAccount(final long accountId)
    {
        if(accountMgr.addAccount(accountId))
        {
            eventEmitter.emitEvent(new EventAccountAdded(accountId));

            return ReturnEnum.OK;
        }

        return ReturnEnum.ACCOUNT_EXISTS;
    }

    public ReturnEnum removeAccount(final long accountId)
    {
        if(accountMgr.removeAccount(accountId))
        {
            eventEmitter.emitEvent(new EventAccountRemoved(accountId));

            return ReturnEnum.OK;
        }

        return ReturnEnum.ACCOUNT_NOT_EXIST;
    }

    public ReturnEnum deposit(final long accountId, final String symbol, final long qty)
    {
        currentAccount = accountMgr.getAccount(accountId);
        if(currentAccount == null)
        {
            return ReturnEnum.ACCOUNT_NOT_EXIST;
        }

        currentAccount.addSymbol(symbol);

        SymbolAmount symbolAmount = currentAccount.getSymbolAmount(symbol);
        if(symbolAmount == null)
        {
            //Should not get here
            return ReturnEnum.ACCOUNT_SYMBOL_NOT_EXIST;
        }

        symbolAmount.deposit(qty);

        eventEmitter.emitEvent(new EventDeposit(accountId, symbol, qty));

        return ReturnEnum.OK;
    }

    public ReturnEnum withdraw(final long accountId, final String symbol, final long qty)
    {
        currentAccount = accountMgr.getAccount(accountId);
        if(currentAccount == null)
        {
            return ReturnEnum.ACCOUNT_NOT_EXIST;
        }

        SymbolAmount symbolAmount = currentAccount.getSymbolAmount(symbol);
        if(symbolAmount != null)
        {
            if(symbolAmount.withdraw(qty))
            {
                eventEmitter.emitEvent(new EventWithdraw(accountId, symbol, qty));

                return ReturnEnum.OK;
            }

            return ReturnEnum.ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS;
        }

        return ReturnEnum.ACCOUNT_SYMBOL_NOT_EXIST; //ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS
    }

    public void eventHandler(final Event event)
    {
        if (event.getEventId() == EventId.EVENT_ID_ORDER_FILL) {
            handleOrderFillEvent((EventOrderFill) event);
        }
    }

    private void handleOrderFillEvent(final EventOrderFill eventOrderFill)
    {
        if(!trySetCurrentAccount(eventOrderFill.getTakerOrder().getAccountId()))
        {
            LOGGER.error("handleOrderFillEvent - eventOrderFill Can't find accountId, skipping");
            return;
        }

        handleOrderFill(currentAccount, eventOrderFill.getBase(), eventOrderFill.getQuote(),
                eventOrderFill.getFillPrice(), eventOrderFill.getFillQty(),
                eventOrderFill.getTakerOrder().getOrderSide());

        Account account = accountMgr.getAccount(eventOrderFill.getMakerOrder().getAccountId());
        handleOrderFill(account, eventOrderFill.getBase(), eventOrderFill.getQuote(),
                eventOrderFill.getFillPrice(), eventOrderFill.getFillQty(),
                eventOrderFill.getMakerOrder().getOrderSide());
    }

    private void handleOrderFill(final Account account, final String base, final String quote,
                                 final long fillPrice, final long fillQty, final OrderSide orderSide)
    {
        final String decreaseLockedAmountSymbol;
        final long decreaseLockedAmountAmount;

        final String depositSymbol;
        final long depositAmount;


        if(orderSide == OrderSide.ORDER_SIDE_SELL)
        {
            decreaseLockedAmountSymbol = base;
            decreaseLockedAmountAmount = fillQty;

            depositSymbol = quote;
            depositAmount = (fillQty * fillPrice);
        }
        else if(orderSide == OrderSide.ORDER_SIDE_BUY)
        {
            decreaseLockedAmountSymbol = quote;
            decreaseLockedAmountAmount = (fillQty * fillPrice);

            depositSymbol = base;
            depositAmount = fillQty;
        }
        else
        {
            //TODO order invalid, should not get here
            LOGGER.error("handleOrderFill - Order invalid, orderSide: {}",
                    OrderUtils.convertOrderSideToString(orderSide));
            return;
        }

        if(!tryDecreaseLockedAmount(account, decreaseLockedAmountSymbol, decreaseLockedAmountAmount))
        {
            //TODO
            LOGGER.error("handleOrderFill - Failed to decrease locked amount, accountId: {}, symbol: {}, qty: {}",
                    account.getAccountId(), decreaseLockedAmountSymbol, decreaseLockedAmountAmount);
        }

        if(!tryDeposit(account, depositSymbol, depositAmount))
        {
            //TODO
            LOGGER.error("handleOrderFill - Failed to deposit, accountId: {}, symbol: {}, qty: {}",
                    account.getAccountId(), depositSymbol, depositAmount);
        }
    }

    private boolean trySetCurrentAccount(final long accountId)
    {
        //Should not happen, but just in case
        if( (currentAccount == null) || (currentAccount.getAccountId() != accountId) )
        {
            LOGGER.info("Not the current account.");

            currentAccount = accountMgr.getAccount(accountId);
            if(currentAccount == null)
            {
                LOGGER.info("AccountId not found: {}", accountId);
                return false;
            }
        }

        return true;
    }

    private boolean tryFreeLockedAmount(final Account account, final String symbol, final long amount)
    {
        SymbolAmount symbolAmount = account.getSymbolAmount(symbol);
        if(symbolAmount == null)
        {
            //should not get here
            LOGGER.info("Symbol does not exist: {}", symbol);
            return false;
        }

        if(!symbolAmount.freeLockedAmount(amount))
        {
            //should not get here
            LOGGER.info("Could not free locked amount:{}", amount);
            return false;
        }

        return true;
    }


    private boolean tryDecreaseLockedAmount(final Account account, final String symbol, final long amount)
    {
        SymbolAmount symbolAmount = account.getSymbolAmount(symbol);
        if(symbolAmount == null)
        {
            //should not get here
            LOGGER.info("Symbol does not exist: {}", symbol);
            return false;
        }

        if( !symbolAmount.decreaseLockedAmount(amount) )
        {
            //should not get here
            LOGGER.info("Could not decrease locked amount: {}", amount);
            return false;
        }

        return true;
    }

    private boolean tryDeposit(final Account account, final String symbol, final long amount)
    {
        SymbolAmount symbolAmount = account.getSymbolAmount(symbol);
        if(symbolAmount == null)
        {
            //should not get here
            LOGGER.info("Symbol does not exist: {}", symbol);
            return false;
        }

        symbolAmount.deposit(amount);
        return true;
    }
}
