using System.Net.WebSockets;
using Newtonsoft.Json;

namespace Algo;

public class BinanceQuoteProvider(ILogger<BinanceQuoteProvider> logger) : BackgroundService
{
    private BinanceQuote? _latestQuote;

    public BinanceQuote? GetLatest()
    {
        lock (this)
            return _latestQuote;
    }
    
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                using var webSocket = new ClientWebSocket();
                await webSocket.ConnectAsync(new Uri("wss://fstream.binance.com/ws/btcusdt@bookTicker"), stoppingToken);
                while (true)
                {
                    var message = await webSocket.ReceiveAsync<BinanceQuote>(stoppingToken);
                    if (message == null)
                        continue;
                    lock (this)
                        _latestQuote = message;
                }
            }
            catch (OperationCanceledException) when (stoppingToken.IsCancellationRequested)
            {
                break;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Binance connection error");
                await Task.Delay(TimeSpan.FromSeconds(2), stoppingToken);
            }
        }
    }
}

public class BinanceQuote
{
    [JsonProperty("e")]
    public string Type { get; set; }

    [JsonProperty("u")]
    public long Timestamp { get; set; }

    [JsonProperty("s")]
    public string Symbol { get; set; }

    [JsonProperty("b")]
    public decimal BidPrice { get; set; }

    [JsonProperty("B")]
    public decimal BidSize { get; set; }

    [JsonProperty("a")]
    public decimal AskPrice { get; set; }

    [JsonProperty("A")]
    public decimal AskSize { get; set; }

    [JsonProperty("T")]
    public long T { get; set; }

    [JsonProperty("E")]
    public long E { get; set; }
}

