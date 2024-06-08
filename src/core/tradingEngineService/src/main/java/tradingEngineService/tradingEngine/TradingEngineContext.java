package tradingEngineService.tradingEngine;

import tradingEngineService.orderBook.OrderBook;
import tradingEngineService.referential.Asset;
import tradingEngineService.referential.Instrument;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TradingEngineContext {
    public final HashMap<Long, HashMap<Integer, BigDecimal>> balances;
    public final HashMap<Integer, InstrumentOrderBook> orderBooks;
    public long sequence;

    public TradingEngineContext() {
        balances = new HashMap<>();
        orderBooks = new HashMap<>();
    }

    public InstrumentOrderBook ensureOrderBook(Instrument instrument) {
        var existingBook = orderBooks.get(instrument.id());
        if (existingBook != null) {
            return existingBook;
        }
        var newBook = new InstrumentOrderBook(instrument, new OrderBook());
        orderBooks.put(instrument.id(), newBook);
        return newBook;
    }

    public BigDecimal getBalance(Account account, Asset asset) {
        return getBalance(account.accountId(), asset);
    }

    public BigDecimal getBalance(long accountId, Asset asset) {
        if (balances.containsKey(accountId)) {
            var balancesByAsset = balances.get(accountId);
            if (balancesByAsset.containsKey(asset.id())) {
                return balancesByAsset.get(asset.id());
            }
        }
        return BigDecimal.valueOf(0L);
    }

    public void adjustBalance(Account account, Asset asset, BigDecimal adjustment) {
        adjustBalance(account.accountId(), asset, adjustment);
    }

    public void adjustBalance(long accountId, Asset asset, BigDecimal adjustment) {
        if (balances.containsKey(accountId)) {
            var balancesByAsset = balances.get(accountId);
            if (balancesByAsset.containsKey(asset.id())) {
                var balance = balancesByAsset.get(asset.id());
                balancesByAsset.put(asset.id(), balance.add(adjustment));
            } else {
                balancesByAsset.put(asset.id(), adjustment);
            }
        } else {
            balances.put(accountId, new HashMap<>(Map.of(asset.id(), adjustment)));
        }
    }
}
