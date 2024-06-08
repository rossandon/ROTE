package tradingEngineService.tradingEngine;

import tradingEngineService.orderBook.OrderBookLimitOrder;
import shared.orderBook.OrderBookSide;
import tradingEngineService.referential.Asset;
import tradingEngineService.referential.Instrument;

import java.math.BigDecimal;

public record LimitOrder(Instrument instrument, Account account, OrderBookLimitOrder limitOrder) {
    public LimitOrder(Instrument instrument, Account account, BigDecimal size, BigDecimal price, OrderBookSide side) {
        this(instrument, account, new OrderBookLimitOrder(size, price, side, account.accountId()));
    }

    public BigDecimal getRequiredFunds() {
        if (limitOrder.side() == OrderBookSide.Buy) {
            return limitOrder.price().multiply(limitOrder.size());
        }
        else {
            return limitOrder.size();
        }
    }

    public Asset fundingAsset() {
        if (limitOrder.side() == OrderBookSide.Buy) {
            return instrument.quoteAsset();
        } else {
            return instrument.baseAsset();
        }
    }
}
