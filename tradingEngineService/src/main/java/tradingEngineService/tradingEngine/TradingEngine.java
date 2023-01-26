package tradingEngineService.tradingEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tradingEngineService.referential.Asset;
import tradingEngineService.referential.Instrument;
import shared.orderBook.*;
import shared.service.LimitOrderResult;

@Component
public class TradingEngine {
    private final TradingEngineContextInstance tradingEngineContextInstance;

    public TradingEngine(TradingEngineContext tradingEngineContext) {
        this.tradingEngineContextInstance = new TradingEngineContextInstance(tradingEngineContext);
    }

    @Autowired
    public TradingEngine(TradingEngineContextInstance tradingEngineContextInstance) {
        this.tradingEngineContextInstance = tradingEngineContextInstance;
    }

    public void adjustBalance(Account account, Asset asset, long balance) {
        getContext().adjustBalance(account, asset, balance);
    }

    public long getBalance(long accountId, Asset asset) {
        return getContext().getBalance(accountId, asset);
    }

    public LimitOrderResult limitOrder(LimitOrder order) {
        var hasFunding = tryReserveFunding(order);
        if (!hasFunding)
            return new LimitOrderResult(LimitOrderResultStatus.Rejected, null);

        var book = getContext().ensureOrderBook(order.instrument());
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
        var cancelledOrder = getContext().ensureOrderBook(instrument).orderBook().cancelOrder(orderId);

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
        getContext().adjustBalance(trade.takerAccountId(), instrument.baseAsset(), trade.size() * m);
        getContext().adjustBalance(trade.makerAccountId(), instrument.baseAsset(), trade.size() * -1 * m);
        getContext().adjustBalance(trade.takerAccountId(), instrument.quoteAsset(), trade.takerAccountId() * m * -1);
    }

    private void refundFunding(LimitOrder order) {
        var requiredFunds = order.getRequiredFunds();
        getContext().adjustBalance(order.account(), order.instrument().quoteAsset(), requiredFunds);
    }

    private boolean tryReserveFunding(LimitOrder order) {
        var funds = getContext().getBalance(order.account(), order.instrument().quoteAsset());
        var requiredFunds = order.getRequiredFunds();
        if (funds >= requiredFunds) {
            getContext().adjustBalance(order.account(), order.instrument().quoteAsset(), requiredFunds * -1);
            return true;
        }
        return false;
    }

    private TradingEngineContext getContext() {
        return tradingEngineContextInstance.getContext();
    }
}
