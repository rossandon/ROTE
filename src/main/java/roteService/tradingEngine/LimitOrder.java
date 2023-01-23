package roteService.tradingEngine;

import roteService.orderBook.OrderBookLimitOrder;
import roteService.orderBook.OrderBookSide;
import roteService.referential.Instrument;

public record LimitOrder(Instrument instrument, Account account, OrderBookLimitOrder limitOrder) {
    public LimitOrder(Instrument instrument, Account account, long size, long price, OrderBookSide side) {
        this(instrument, account, new OrderBookLimitOrder(size, price, side, account.accountId()));
    }

    public long getRequiredFunds() {
        return limitOrder.price() * limitOrder.size();
    }
}
