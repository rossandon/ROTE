package tradingEngineService.orderBook;

import shared.orderBook.*;

import java.math.BigDecimal;
import java.util.ArrayList;

public class OrderBook {
    public long orderSequence;
    public long tradeSequence;
    public final ArrayList<OrderBookEntry> bids = new ArrayList<>();
    public final ArrayList<OrderBookEntry> asks = new ArrayList<>();

    public OrderBookLimitOrderResult processOrder(OrderBookLimitOrder order) {
        var allEntries = getExecutionSide(order.side());
        var executableEntries = getExecutableEntries(order, allEntries);
        if (containsCross(executableEntries, order))
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Rejected, "Cross", null, null);
        var executionResult = executeLimitOrder(order, allEntries, executableEntries);
        if (!executionResult.partial()) {
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Filled, null, executionResult.trades(), null);
        }
        var restingOrder = addRestingOrder(executionResult.getRestingOrder());
        if (executionResult.trades().isEmpty())
            return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Resting, null,null, restingOrder);
        return new OrderBookLimitOrderResult(OrderBookLimitOrderResultStatus.Partial, null, executionResult.trades(), restingOrder);
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
        var newEntry = new OrderBookEntry(order.size(), order.price(), order.accountId(), orderSequence++, order.side());
        var orderBookEntries = getRestingSide(order.side());
        for (int i = 0; i < orderBookEntries.size(); i++) {
            var entry = orderBookEntries.get(i);
            var isImprovement = order.side() == OrderBookSide.Buy
                    ? order.price().compareTo(entry.price()) > 0
                    : order.price().compareTo(entry.price()) < 0;

            if (isImprovement) {
                orderBookEntries.add(i, newEntry);
                return newEntry;
            }
        }

        orderBookEntries.add(newEntry);
        return newEntry;
    }

    private OrderBookExecutionResult executeLimitOrder(OrderBookLimitOrder order, ArrayList<OrderBookEntry> allEntries, ArrayList<OrderBookEntry> executableEntries) {
        var totalSize = BigDecimal.ZERO;
        var trades = new ArrayList<OrderBookTrade>();

        for (var i = 0; i < executableEntries.size(); i++) {
            var entry = allEntries.get(i);
            var trade = new OrderBookTrade(tradeSequence++, order.size().min(entry.size()), entry.price(), order.side(), entry.accountId(), order.accountId());
            var remainingOnOrder = entry.size().subtract(trade.size());
            allEntries.set(i, entry.withSize(remainingOnOrder));
            trades.add(trade);
            totalSize =  totalSize.add(trade.size());
            if (totalSize.compareTo(order.size()) == 0)
                break;
        }

        while (!allEntries.isEmpty()) {
            if (allEntries.get(0).size().compareTo(BigDecimal.ZERO) == 0)
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
            remaining = remaining.subtract(entry.size());
            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
                break;
        }
        return executableEntries;
    }

    public ArrayList<OrderBookEntry> cancelAll(long accountId) {
        var cancelled = new ArrayList<OrderBookEntry>();
        cancelled.addAll(cancelAll(accountId, bids));
        cancelled.addAll(cancelAll(accountId, asks));
        return cancelled;
    }

    private ArrayList<OrderBookEntry> cancelAll(long accountId, ArrayList<OrderBookEntry> entries) {
        var cancelled = new ArrayList<OrderBookEntry>();
        for (var i = entries.size() - 1; i >= 0; i--) {
            var entry = entries.get(i);
            if (entry.accountId() == accountId) {
                cancelled.add(entry);
                entries.remove(i);
            }
        }
        return cancelled;
    }
}
