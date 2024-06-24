package tradingEngineService.tradingEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tradingEngineService.referential.Asset;
import tradingEngineService.referential.Instrument;
import shared.orderBook.*;
import shared.service.results.LimitOrderResult;

import java.math.BigDecimal;

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

    public void adjustBalance(Account account, Asset asset, BigDecimal balance) {
        getContext().adjustBalance(account, asset, balance);
    }

    public BigDecimal getBalance(long accountId, Asset asset) {
        return getContext().getBalance(accountId, asset);
    }

    public InstrumentOrderBook ensureOrderBook(Instrument instrument) {
        return getContext().ensureOrderBook(instrument);
    }

    public LimitOrderResult limitOrder(LimitOrder order) {
        var hasFunding = tryReserveFunding(order);
        if (!hasFunding)
            return new LimitOrderResult(LimitOrderResultStatus.Rejected, "Insufficient funding", null);

        var book = getContext().ensureOrderBook(order.instrument());
        var result = book.orderBook().processOrder(order.limitOrder());
        if (result.status() == OrderBookLimitOrderResultStatus.Rejected) {
            refundFunding(order);
            return new LimitOrderResult(LimitOrderResultStatus.Rejected, result.rejectReason(),null);
        }

        if (result.status() == OrderBookLimitOrderResultStatus.Partial || result.status() == OrderBookLimitOrderResultStatus.Filled) {
            bookTrades(result, order);
        }

        return new LimitOrderResult(LimitOrderResultStatus.Ok, null, result);
    }


    public boolean cancel(Account account, Instrument instrument, long orderId) {
        var cancelledOrder = getContext().ensureOrderBook(instrument).orderBook().cancelOrder(orderId);

        if (cancelledOrder != null) {
            refundCancelledOrder(account, instrument, cancelledOrder);
            return true;
        } else {
            return false;
        }
    }

    private void refundCancelledOrder(Account account, Instrument instrument, OrderBookEntry cancelledOrder) {
        var fundingAsset = cancelledOrder.side() == OrderBookSide.Buy ? instrument.quoteAsset() : instrument.baseAsset();
        var fundingSize = cancelledOrder.side() == OrderBookSide.Buy ? cancelledOrder.price().multiply(cancelledOrder.size()) : cancelledOrder.size();
        adjustBalance(account, fundingAsset, fundingSize);
    }

    private void bookTrades(OrderBookLimitOrderResult result, LimitOrder order) {
        var contraTotal = BigDecimal.ZERO;

        for (var trade : result.trades()) {
            bookTrade(trade, order.instrument());
            contraTotal = contraTotal.add(trade.price().multiply(trade.size()));
        }

        if (order.limitOrder().side() == OrderBookSide.Buy) {
            var difference = order.limitOrder().price().multiply(order.limitOrder().size()).subtract(contraTotal);
            adjustBalance(order.account(), order.fundingAsset(), difference);
        }
    }

    private void bookTrade(OrderBookTrade trade, Instrument instrument) {
        if (trade.takerSide() == OrderBookSide.Buy) {
            getContext().adjustBalance(trade.takerAccountId(), instrument.baseAsset(), trade.size());
            getContext().adjustBalance(trade.makerAccountId(), instrument.quoteAsset(), trade.size().multiply(trade.price()));
        } else {
            getContext().adjustBalance(trade.takerAccountId(), instrument.quoteAsset(), trade.size().multiply(trade.price()));
            getContext().adjustBalance(trade.makerAccountId(), instrument.baseAsset(), trade.size());
        }

    }

    private void refundFunding(LimitOrder order) {
        var requiredFunds = order.getRequiredFunds();
        getContext().adjustBalance(order.account(), order.fundingAsset(), requiredFunds);
    }

    private boolean tryReserveFunding(LimitOrder order) {
        var fundingAsset = order.fundingAsset();
        var funds = getContext().getBalance(order.account(), fundingAsset);
        var requiredFunds = order.getRequiredFunds();
        if (funds.compareTo(requiredFunds) >= 0) {
            getContext().adjustBalance(order.account(), fundingAsset, requiredFunds.multiply(BigDecimal.valueOf(-1)));
            return true;
        }
        return false;
    }

    private TradingEngineContext getContext() {
        return tradingEngineContextInstance.getContext();
    }

    public boolean cancelAll(Account account, Instrument instrument) {
        var book = getContext().ensureOrderBook(instrument);
        var cancelled = book.orderBook().cancelAll(account.accountId());
        for (var order : cancelled) {
            refundCancelledOrder(account, instrument, order);
        }
        return !cancelled.isEmpty();
    }
}
