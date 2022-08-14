import orderBook.OrderBookLimitOrderResult;

import java.util.ArrayList;

public class TradingEngine {
    public TradingEngineContext context;

    public TradingEngine(TradingEngineContext context) {
        this.context = context;
    }

    public void addFunding(Account account, Asset asset, long balance) {
        context.adjustBalance(account, asset, balance);
    }

    public LimitOrderResult process(LimitOrder order) {
        var hasFunding = tryReserveFunding(order);
        if (!hasFunding)
            return new LimitOrderResult(LimitOrderResultType.Rejected, null);

        var book = context.ensureOrderBook(order.instrument());
        var result = book.orderBook().process(order.limitOrder());
        return new LimitOrderResult(LimitOrderResultType.Ok, result);
    }

    private boolean tryReserveFunding(LimitOrder order) {
        if (!context.balances.containsKey(order.account()))
            return false;
        var funds = context.getBalance(order.account(), order.instrument().quoteAsset());
        var requiredFunds = order.getRequiredFunds();
        if (funds >= requiredFunds) {
            context.adjustBalance(order.account(), order.instrument().quoteAsset(), requiredFunds * -1);
            return true;
        }
        return false;
    }


}
