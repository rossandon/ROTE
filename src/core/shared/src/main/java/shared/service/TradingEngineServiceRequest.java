package shared.service;

import shared.orderBook.OrderBookSide;

public record TradingEngineServiceRequest(TradingEngineServiceRequestType type, long amount, long price, long accountId,
                                          String instrumentCode, OrderBookSide side, String assetCode, long orderId) {
    public static TradingEngineServiceRequest limitOrder(long amount, long price, long accountId, String instrumentCode, OrderBookSide side) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.LimitOrder, amount, price, accountId, instrumentCode, side, null, 0);
    }

    public static TradingEngineServiceRequest adjustBalance(long amount, long accountId, String assetCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.AdjustBalance, amount, 0, accountId, null, OrderBookSide.Buy, assetCode, 0);
    }

    public static TradingEngineServiceRequest getBalance(long accountId, String assetCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBalance, 0, 0, accountId, null, OrderBookSide.Buy, assetCode, 0);
    }

    public static TradingEngineServiceRequest getBook(String instrumentCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBook, 0, 0, 0, instrumentCode, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest getBalances(long accountId) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBalances, 0, 0, accountId, null, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest cancel(long accountId, String instrumentCode, long orderId) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.Cancel, 0, 0, accountId, instrumentCode, OrderBookSide.Buy, null, orderId);
    }

    public static TradingEngineServiceRequest cancelAll(long accountId, String instrumentCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.CancelAll, 0, 0, accountId, instrumentCode, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest error() {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.Error, 0, 0, 0, null, OrderBookSide.Buy, null, 0);
    }
}
