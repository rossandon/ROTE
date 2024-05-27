package tradingEngineService.orderBook;

import shared.orderBook.*;

import java.util.ArrayList;

public class OrderBook {
    private long idCounter;
    public final ArrayList<OrderBookEntry> bids = new ArrayList<>();
    public final ArrayList<OrderBookEntry> asks = new ArrayList<>();

    public OrderBookLimitOrderResult processOrder(OrderBookLimitOrder order) {
        var allEntries = getExecutionSide(order.side());
        var executableEntries = getExecutableEntries(order, allEntries);
        if (containsCross(executableEntries, order))
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Rejected, null, null);
        var executionResult = executeLimitOrder(order, allEntries, executableEntries);
        if (!executionResult.partial()) {
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Filled, executionResult.trades(), null);
        }
        var restingOrder = addRestingOrder(executionResult.getRestingOrder());
        if (executionResult.trades().isEmpty())
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Resting, null, restingOrder);
        return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Partial, executionResult.trades(), restingOrder);
    }

    public OrderBookEntry cancelOrder(long id) {
        var cancelledOrder = cancelOrder(id, bids);
        if (cancelledOrder == null)
            cancelledOrder = cancelOrder(id, asks);
        return cancelledOrder;
    }

    private static OrderBookEntry cancelOrder(long id, ArrayList<OrderBookEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            if (entry.id() == id) {
                return entries.remove(i);
            }
        }
        return null;
    }

    private boolean containsCross(ArrayList<OrderBookEntry> entries, OrderBookLimitOrder order) {
        for (OrderBookEntry entry : entries) {
            if (entry.accountId() == order.accountId())
                return true;
        }
        return false;
    }

    private ArrayList<OrderBookEntry> getExecutionSide(OrderBookSide side) {
        return side == OrderBookSide.Buy ? asks : bids;
    }

    private ArrayList<OrderBookEntry> getRestingSide(OrderBookSide side) {
        return side == OrderBookSide.Sell ? asks : bids;
    }

    private OrderBookEntry addRestingOrder(OrderBookLimitOrder order) {
        var newEntry = new OrderBookEntry(order.size(), order.price(), order.accountId(), idCounter++, order.side());
        var orderBookEntries = getRestingSide(order.side());
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

    private OrderBookExecutionResult executeLimitOrder(OrderBookLimitOrder order, ArrayList<OrderBookEntry> allEntries, ArrayList<OrderBookEntry> executableEntries) {
        var totalSize = 0L;
        var trades = new ArrayList<OrderBookTrade>();

        for (var i = 0; i < executableEntries.size(); i++) {
            var entry = allEntries.get(i);
            var trade = order.fill(entry);
            var remainingOnOrder = entry.size() - trade.size();
            allEntries.set(i, entry.withSize(remainingOnOrder));
            trades.add(trade);
            totalSize += trade.size();
            if (totalSize == order.size())
                break;
        }

        while (!allEntries.isEmpty()) {
            if (allEntries.get(0).size() == 0)
                allEntries.remove(0);
            else
                break;
        }

        return new OrderBookExecutionResult(order, totalSize, trades);
    }

    private static ArrayList<OrderBookEntry> getExecutableEntries(OrderBookLimitOrder order, ArrayList<OrderBookEntry> entries) {
        var executableEntries = new ArrayList<OrderBookEntry>();
        var remaining = order.size();
        for (OrderBookEntry entry : entries) {
            if (!order.canFill(entry))
                break;
            executableEntries.add(entry);
            remaining -= entry.size();
            if (remaining <= 0)
                break;
        }
        return executableEntries;
    }

    public boolean cancelAll(long accountId) {
        return cancelAll(accountId, bids) | cancelAll(accountId, asks);
    }

    private boolean cancelAll(long accountId, ArrayList<OrderBookEntry> entries) {
        var any = false;
        for (var i = entries.size() - 1; i >= 0; i--) {
            var entry = entries.get(i);
            if (entry.accountId() == accountId) {
                entries.remove(i);
                any = true;
            }
        }
        return any;
    }
}
