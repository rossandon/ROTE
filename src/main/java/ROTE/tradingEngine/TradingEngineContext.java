package ROTE.tradingEngine;

import ROTE.orderBook.OrderBook;
import org.springframework.stereotype.Component;
import ROTE.referential.Asset;
import ROTE.referential.Instrument;

import java.util.HashMap;
import java.util.Map;

@Component
public class TradingEngineContext {
    public final HashMap<Long, HashMap<Asset, Long>> balances;
    public final HashMap<Instrument, InstrumentOrderBook> orderBooks;

    public TradingEngineContext() {
        balances = new HashMap<>();
        orderBooks = new HashMap<>();
    }

    public InstrumentOrderBook ensureOrderBook(Instrument instrument) {
        var existingBook = orderBooks.get(instrument);
        if (existingBook != null) {
            return existingBook;
        }
        var newBook = new InstrumentOrderBook(instrument, new OrderBook());
        orderBooks.put(instrument, newBook);
        return newBook;
    }

    public Long getBalance(Account account, Asset asset) {
        return getBalance(account.accountId(), asset);
    }
    public Long getBalance(Long accountId, Asset asset) {
        if (balances.containsKey(accountId)) {
            var balancesByAsset = balances.get(accountId);
            if (balancesByAsset.containsKey(asset)) {
                return balancesByAsset.get(asset);
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
            if (balancesByAsset.containsKey(asset)) {
                var balance = balancesByAsset.get(asset);
                balancesByAsset.put(asset, balance + adjustment);
            } else {
                balancesByAsset.put(asset, adjustment);
            }
        } else {
            balances.put(accountId, new HashMap<>(Map.of(asset, adjustment)));
        }
    }
}
