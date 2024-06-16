using System.Net.WebSockets;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;

namespace Algo;

public class BinanceQuoteProvider(IOptions<List<QuoterConfiguration>> quoterConfigurations, QuoteStore store, ILogger<BinanceQuoteProvider> logger) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var threads = quoterConfigurations.Value
            .Where(c => c.Exchange == Exchange.Binance)
            .Select(c => c.ExchangeInstrument)
            .Distinct()
            .Select(i => ReadBook(i, stoppingToken))
            .ToArray();

        await Task.WhenAll(threads);
    }

    private async Task ReadBook(string instrumentCode, CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                using var webSocket = new ClientWebSocket();
                await webSocket.ConnectAsync(new Uri($"wss://fstream.binance.com/ws/{instrumentCode}@bookTicker"), stoppingToken);
                while (true)
                {
                    var message = await webSocket.ReceiveAsync<BinanceQuote>(stoppingToken);
                    if (message == null)
                        continue;
                    store.Set(new Quote(instrumentCode, Exchange.Binance, message.BidPrice, message.AskPrice));
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

