namespace Algo;

public class Quoter(BinanceQuoteProvider binanceQuoteProvider, RoteClient roteClient, ILogger<Quoter> logger)
    : BackgroundService
{
    private const int MinBalance = 1_000_000;
    
    private BinanceQuote? _previousQuote;

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await EnsureBalance(stoppingToken);
        
        while (!stoppingToken.IsCancellationRequested)
        {
            var currentQuote = binanceQuoteProvider.GetLatest();
            if (currentQuote == null)
                continue;
            if (_previousQuote is null || IsQuoteDifferent(_previousQuote, currentQuote))
            {
                
                await Reprice(currentQuote, stoppingToken);
            }

            _previousQuote = currentQuote;
            await Task.Delay(TimeSpan.FromMilliseconds(200), stoppingToken);
        }
    }

    private async Task EnsureBalance(CancellationToken stoppingToken)
    {
        var balances = await roteClient.GetBalances(stoppingToken);
        foreach (var entry in balances)
        {
            if (entry.Value < MinBalance)
                await roteClient.Deposit(entry.Key, (long)(MinBalance - entry.Value), stoppingToken);
        }
    }

    private async Task Reprice(BinanceQuote currentQuote, CancellationToken cancellationToken)
    {
        logger.LogInformation("Repricing");
        await roteClient.CancelAll("BTC/USD", cancellationToken);
        await roteClient.PlaceOrder("BTC/USD", ((long)currentQuote.BidPrice) - 1, 1, TradeSide.Buy, cancellationToken);
        await roteClient.PlaceOrder("BTC/USD", ((long)currentQuote.AskPrice) + 1, 1, TradeSide.Sell, cancellationToken);
    }

    private static bool IsQuoteDifferent(BinanceQuote previousQuote, BinanceQuote currentQuote)
    {
        return previousQuote.BidPrice != currentQuote.BidPrice || previousQuote.AskPrice != currentQuote.AskPrice;
    }
}