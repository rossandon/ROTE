import orderBook.OrderBook;

import java.util.HashMap;
import java.util.Map;

public class TradingEngineContext {
    public HashMap<Account, HashMap<Asset, Long>> balances;
    public HashMap<Instrument, InstrumentOrderBook> orderBooks;

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
        if (balances.containsKey(account)) {
            var balancesByAsset = balances.get(account);
            if (balancesByAsset.containsKey(asset)) {
                return balancesByAsset.get(asset);
            }
        }
        return 0L;
    }

    public void adjustBalance(Account account, Asset asset, long adjustment) {
        if (balances.containsKey(account)) {
            var balancesByAsset = balances.get(account);
            if (balancesByAsset.containsKey(asset)) {
                var balance = balancesByAsset.get(asset);
                balancesByAsset.put(asset, balance + adjustment);
            } else {
                balancesByAsset.put(asset, adjustment);
            }
        } else {
            balances.put(account, new HashMap<>(Map.of(asset, adjustment)));
        }
    }
}
