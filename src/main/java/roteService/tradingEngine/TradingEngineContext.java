package roteService.tradingEngine;

import roteService.orderBook.OrderBook;
import roteService.referential.Asset;
import roteService.referential.Instrument;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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

    public long getBalance(Account account, Asset asset) {
        return getBalance(account.accountId(), asset);
    }

    public long getBalance(long accountId, Asset asset) {
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

    public void adjustBalance(long accountId, Asset asset, long adjustment) {
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
