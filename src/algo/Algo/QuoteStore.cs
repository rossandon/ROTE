using System.Collections.Concurrent;

namespace Algo;

public class QuoteStore
{
    private readonly ConcurrentDictionary<(string, Exchange), Quote> _quotes = new();

    public void Set(Quote quote)
    {
        _quotes[(quote.InstrumentCode, quote.Exchange)] = quote;
    }

    public Quote? GetOrNull(string instrumentCode, Exchange exchange)
    {
        return _quotes.GetValueOrDefault((instrumentCode, exchange));
    }
}

public record Quote(string InstrumentCode, Exchange Exchange, decimal BidPrice, decimal AskPrice)
{
}