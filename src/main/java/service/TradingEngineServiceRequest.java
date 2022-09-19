package service;

import orderBook.OrderBookSide;

public record TradingEngineServiceRequest(TradingEngineServiceRequestType type, long amount, long price, long accountId, String instrumentCode, OrderBookSide side, String assetCode) {
    public TradingEngineServiceRequest(long amount, long price, long accountId, String instrumentCode, OrderBookSide side)     {
        this(TradingEngineServiceRequestType.LimitOrder, amount, price, accountId, instrumentCode, side, null);
    }

    public TradingEngineServiceRequest(long amount, long accountId, String assetCode)     {
        this(TradingEngineServiceRequestType.AdjustBalance, amount, 0, accountId, null, OrderBookSide.Buy, assetCode);
    }

    public TradingEngineServiceRequest(long accountId, String assetCode)     {
        this(TradingEngineServiceRequestType.GetBalance, 0, 0, accountId, null, OrderBookSide.Buy, assetCode);
    }
}
