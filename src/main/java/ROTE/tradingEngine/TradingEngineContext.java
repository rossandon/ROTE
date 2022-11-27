package ROTE.tradingEngine;

import ROTE.orderBook.OrderBook;
import ROTE.referential.Asset;
import ROTE.referential.Instrument;

import java.util.HashMap;
import java.util.Map;

public class TradingEngineContext {
    public final HashMap<Long, HashMap<Integer, Long>> balances;
    public final HashMap<Integer, InstrumentOrderBook> orderBooks;

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

    public Long getBalance(Account account, Asset asset) {
        return getBalance(account.accountId(), asset);
    }
    public Long getBalance(Long accountId, Asset asset) {
        if (balances.containsKey(accountId)) {
            var balancesByAsset = balances.get(accountId);
            if (balancesByAsset.containsKey(asset.id())) {
                return balancesByAsset.get(asset.id());
            }
        }
        return 0L;
    }

    public void adjustBalance(Account account, Asset asset, long adjustment) {
        adjustBalance(account.accountId(), asset, adjustment);
    }

    public void adjustBalance(Long accountId, Asset asset, long adjustment) {
        if (balances.containsKey(accountId)) {
            var balancesByAsset = balances.get(accountId);
            if (balancesByAsset.containsKey(asset.id())) {
                var balance = balancesByAsset.get(asset.id());
                balancesByAsset.put(asset.id(), balance + adjustment);
            } else {
                balancesByAsset.put(asset.id(), adjustment);
            }
        } else {
            balances.put(accountId, new HashMap<>(Map.of(asset.id(), adjustment)));
        }
    }
}
