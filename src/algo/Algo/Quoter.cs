namespace Algo;

public class Quoter(BinanceQuoteProvider binanceQuoteProvider, RoteClient roteClient, ILogger<Quoter> logger)
    : BackgroundService
{
    private const int MinBalance = 1_000_000;
    private BinanceQuote? _previousQuote;
    public QuoterConfiguration Configuration { get; set; }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        roteClient.SetUsername(Configuration.Username);

        var whoAmIResponse = await roteClient.WhoAmI(stoppingToken);
        logger.LogInformation($"Starting quoter '{Configuration.Username}' (user id = {whoAmIResponse.AccountId})");


        await EnsureBalance(stoppingToken);

        while (!stoppingToken.IsCancellationRequested)
        {
            var currentQuote = binanceQuoteProvider.GetLatest();
            if (currentQuote == null)
                continue;
            if (_previousQuote is null || IsQuoteDifferent(_previousQuote, currentQuote))
                await Reprice(currentQuote, stoppingToken);
            _previousQuote = currentQuote;
            await Task.Delay(Configuration.Interval, stoppingToken);
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
        logger.LogInformation($"Repricing {Configuration.Username}");
        await roteClient.CancelAll(Configuration.Symbol, cancellationToken);
        
        await roteClient.PlaceOrder(Configuration.Symbol, (long)currentQuote.BidPrice.AddBips(-Configuration.Spread),
            (long)Configuration.Qty, TradeSide.Buy, cancellationToken);
        
        await roteClient.PlaceOrder(Configuration.Symbol, (long)currentQuote.AskPrice.AddBips(Configuration.Spread),
            (long)Configuration.Qty, TradeSide.Sell, cancellationToken);
    }

    private static bool IsQuoteDifferent(BinanceQuote previousQuote, BinanceQuote currentQuote)
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
}