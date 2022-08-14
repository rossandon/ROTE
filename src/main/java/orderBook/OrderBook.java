package orderBook;

import java.util.ArrayList;

public class OrderBook {
    public ArrayList<OrderBookEntry> Bids = new ArrayList<>();
    public ArrayList<OrderBookEntry> Asks = new ArrayList<>();

    public OrderBookLimitOrderResult process(OrderBookLimitOrder order) {
        var executionResult = ExecuteLimitOrder(order, GetExecutionSide(order.side()));
        if (!executionResult.partial())
            return new OrderBookLimitOrderResult(executionResult.trades(), null);
        var restingOrder = addRestingOrder(executionResult.getRestingOrder());
        return new OrderBookLimitOrderResult(executionResult.trades(), restingOrder);
    }

    private ArrayList<OrderBookEntry> GetExecutionSide(OrderBookSide side) {
        return side == OrderBookSide.Buy ? Asks : Bids;
    }

    private ArrayList<OrderBookEntry> GetRestingSide(OrderBookSide side) {
        return side == OrderBookSide.Sell ? Asks : Bids;
    }

    private OrderBookEntry addRestingOrder(OrderBookLimitOrder order) {
        var newEntry = new OrderBookEntry(order.size(), order.price(), order.accountId());
        var orderBookEntries = GetRestingSide(order.side());
        for (int i = 0; i < orderBookEntries.size(); i++) {
            var entry = orderBookEntries.get(i);
            var isImprovement = order.side() == OrderBookSide.Buy
                    ? order.price() > entry.price()
                    : order.price() < entry.price();

            if (isImprovement) {
                orderBookEntries.add(i, newEntry);
                return newEntry;
            }
        }

        orderBookEntries.add(newEntry);
        return newEntry;
    }

    private OrderBookExecutionResult ExecuteLimitOrder(OrderBookLimitOrder order, ArrayList<OrderBookEntry> entries) {
        var totalSize = 0;
        var trades = new ArrayList<OrderBookTrade>();

        for (var i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            var trade = order.tryFill(entry);
            if (trade == null)
                break;
            var remainingOnOrder = entry.size() - trade.size();
            entries.set(i, new OrderBookEntry(remainingOnOrder, entry.price(), order.accountId()));
            trades.add(trade);
            totalSize += trade.size();
            if (totalSize == order.size())
                return new OrderBookExecutionResult(order, totalSize, trades);
        }

        while (entries.size() > 0) {
            if (entries.get(0).size() == 0)
                entries.remove(0);
            else
                break;
        }

        return new OrderBookExecutionResult(order, totalSize, trades);
    }

}
