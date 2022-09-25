package tradingEngine;

import orderBook.OrderBookLimitOrderResult;
import orderBook.OrderBookLimitOrderResultStatus;
import orderBook.OrderBookSide;
import orderBook.OrderBookTrade;
import referential.Asset;
import referential.Instrument;

public class TradingEngine {
    private final TradingEngineContext context;

    public TradingEngine(TradingEngineContext context) {
        this.context = context;
    }

    public void adjustBalance(Account account, Asset asset, long balance) {
        context.adjustBalance(account, asset, balance);
    }

    public Long getBalance(Long accountId, Asset asset) {
        return context.getBalance(accountId, asset);
    }

    public LimitOrderResult limitOrder(LimitOrder order) {
        var hasFunding = tryReserveFunding(order);
        if (!hasFunding)
            return new LimitOrderResult(LimitOrderResultStatus.Rejected, null);

        var book = context.ensureOrderBook(order.instrument());
        var result = book.orderBook().processOrder(order.limitOrder());
        if (result.status() == OrderBookLimitOrderResultStatus.Rejected) {
            refundFunding(order);
            return new LimitOrderResult(LimitOrderResultStatus.Rejected, null);
        }

        if (result.status() == OrderBookLimitOrderResultStatus.Partial || result.status() == OrderBookLimitOrderResultStatus.Filled) {
            bookTrades(result, order.instrument());
        }

        return new LimitOrderResult(LimitOrderResultStatus.Ok, result);
    }

    public boolean cancel(Account account, Instrument instrument, long orderId) {
        var cancelledOrder = context.ensureOrderBook(instrument).orderBook().cancelOrder(orderId);

        if (cancelledOrder != null) {
            adjustBalance(account, instrument.quoteAsset(), cancelledOrder.fundingSize());
            return true;
        } else {
            return false;
        }
    }

    private void bookTrades(OrderBookLimitOrderResult result, Instrument instrument) {
        for (var trade : result.trades()) {
            bookTrade(trade, instrument);
        }
    }

    private void bookTrade(OrderBookTrade trade, Instrument instrument) {
        var m = trade.takerSide() == OrderBookSide.Buy ? 1 : -1;
        context.adjustBalance(trade.takerAccountId(), instrument.baseAsset(), trade.size() * m);
        context.adjustBalance(trade.makerAccountId(), instrument.baseAsset(), trade.size() * -1 * m);
        context.adjustBalance(trade.takerAccountId(), instrument.quoteAsset(), trade.takerAccountId() * m * -1);
    }

    private void refundFunding(LimitOrder order) {
        var requiredFunds = order.getRequiredFunds();
        context.adjustBalance(order.account(), order.instrument().quoteAsset(), requiredFunds);
    }

    private boolean tryReserveFunding(LimitOrder order) {
        var funds = context.getBalance(order.account(), order.instrument().quoteAsset());
        var requiredFunds = order.getRequiredFunds();
        if (funds >= requiredFunds) {
            context.adjustBalance(order.account(), order.instrument().quoteAsset(), requiredFunds * -1);
            return true;
        }
        return false;
    }
}
