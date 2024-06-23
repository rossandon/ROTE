namespace Algo;

public class Quoter(QuoteStore quoteStore, RoteClient roteClient, ILogger<Quoter> logger)
{
    private const int MinBalance = 1_000_000_000;
    private Quote? _previousQuote;
    public QuoterConfiguration Configuration { get; set; }

    public async Task Run(CancellationToken stoppingToken)
    {
        roteClient.SetUsername(Configuration.Username);

        var whoAmIResponse = await roteClient.WhoAmI(stoppingToken);
        logger.LogInformation($"Starting quoter '{Configuration.Username}' (user id = {whoAmIResponse.AccountId})");

        await EnsureBalance(stoppingToken);

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var currentQuote = quoteStore.GetOrNull(Configuration.ExchangeInstrument, Configuration.Exchange);
                if (currentQuote == null)
                {
                    await Task.Delay(TimeSpan.FromSeconds(1), stoppingToken);
                    continue;
                }

                if (_previousQuote is null || IsQuoteDifferent(_previousQuote, currentQuote))
                    await Reprice(currentQuote, stoppingToken);
                _previousQuote = currentQuote;
                await Task.Delay(Configuration.Interval, stoppingToken);
            }
            catch (OperationCanceledException) when (stoppingToken.IsCancellationRequested)
            {
                return;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Failed to quote");
                await Task.Delay(TimeSpan.FromSeconds(5), stoppingToken);
            }
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

    private async Task Reprice(Quote currentQuote, CancellationToken cancellationToken)
    {
        logger.LogInformation($"Repricing {Configuration.Username}");
        await roteClient.CancelAll(Configuration.Symbol, cancellationToken);
        
        await roteClient.PlaceOrder(Configuration.Symbol, decimal.Round(currentQuote.BidPrice.AddBips(-Configuration.Spread), 2),
            (long)Configuration.Qty, TradeSide.Buy, cancellationToken);
        
        await roteClient.PlaceOrder(Configuration.Symbol, decimal.Round(currentQuote.AskPrice.AddBips(Configuration.Spread), 2),
            (long)Configuration.Qty, TradeSide.Sell, cancellationToken);
    }

    private static bool IsQuoteDifferent(Quote previousQuote, Quote currentQuote)
    {
        return previousQuote.BidPrice != currentQuote.BidPrice || previousQuote.AskPrice != currentQuote.AskPrice;
    }
}

public class QuoterConfiguration
{
    public int Spread { get; set; } = 10;
    public decimal Qty { get; set; } = 1;
    public TimeSpan Interval { get; set; } = TimeSpan.FromMilliseconds(200);
    public string Username { get; set; }
    public string Symbol { get; set; } = "BTC/USD";
    public string ExchangeInstrument { get; set; } = "btcusdt";
    public Exchange Exchange { get; set; } = Exchange.Binance;
}

public enum Exchange
{
    Binance,
    Coinbase
}