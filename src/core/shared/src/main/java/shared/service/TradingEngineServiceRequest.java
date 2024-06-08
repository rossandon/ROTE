package shared.service;

import shared.orderBook.OrderBookSide;

import java.math.BigDecimal;

public record TradingEngineServiceRequest(TradingEngineServiceRequestType type, BigDecimal amount, BigDecimal price, long accountId,
                                          String instrumentCode, OrderBookSide side, String assetCode, long orderId) {
    public static TradingEngineServiceRequest limitOrder(BigDecimal amount, BigDecimal price, long accountId, String instrumentCode, OrderBookSide side) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.LimitOrder, amount, price, accountId, instrumentCode, side, null, 0);
    }

    public static TradingEngineServiceRequest adjustBalance(BigDecimal amount, long accountId, String assetCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.AdjustBalance, amount, BigDecimal.ZERO, accountId, null, OrderBookSide.Buy, assetCode, 0);
    }

    public static TradingEngineServiceRequest getBalance(long accountId, String assetCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBalance, BigDecimal.valueOf(0), BigDecimal.ZERO, accountId, null, OrderBookSide.Buy, assetCode, 0);
    }

    public static TradingEngineServiceRequest getBook(String instrumentCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBook, BigDecimal.valueOf(0), BigDecimal.ZERO, 0, instrumentCode, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest getBalances(long accountId) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.GetBalances, BigDecimal.valueOf(0), BigDecimal.ZERO, accountId, null, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest cancel(long accountId, String instrumentCode, long orderId) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.Cancel, BigDecimal.valueOf(0), BigDecimal.ZERO, accountId, instrumentCode, OrderBookSide.Buy, null, orderId);
    }

    public static TradingEngineServiceRequest cancelAll(long accountId, String instrumentCode) {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.CancelAll, BigDecimal.valueOf(0), BigDecimal.ZERO, accountId, instrumentCode, OrderBookSide.Buy, null, 0);
    }

    public static TradingEngineServiceRequest error() {
        return new TradingEngineServiceRequest(TradingEngineServiceRequestType.Error, BigDecimal.valueOf(0), BigDecimal.ZERO, 0, null, OrderBookSide.Buy, null, 0);
    }
}
