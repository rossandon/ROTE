namespace Algo;

public class Quoter(BinanceQuoteProvider binanceQuoteProvider) : BackgroundService
{
    private BinanceQuote? _previousQuote;

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            var currentQuote = binanceQuoteProvider.GetLatest();
            if (currentQuote == null)
                continue;
            if (_previousQuote is null || IsQuoteDifferent(_previousQuote, currentQuote))
            {
                await Reprice(currentQuote);
            }
            _previousQuote = currentQuote;
            await Task.Delay(TimeSpan.FromMilliseconds(200), stoppingToken);
        }
    }

    private async Task Reprice(BinanceQuote? currentQuote)
    {
    }

    private static bool IsQuoteDifferent(BinanceQuote previousQuote, BinanceQuote currentQuote)
    {
        return previousQuote.BidPrice != currentQuote.BidPrice || previousQuote.AskPrice != currentQuote.AskPrice;
    }
}