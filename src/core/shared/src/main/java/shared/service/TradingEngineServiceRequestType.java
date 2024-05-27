package shared.service;

public enum TradingEngineServiceRequestType {
    LimitOrder,
    AdjustBalance,
    GetBalance,
    GetBalances,
    GetBook,
    Cancel,
    Error,
    CancelAll,
}
