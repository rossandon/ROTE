using System.Net.WebSockets;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;

namespace Algo;

public class CoinbaseQuoteProvider(IOptions<List<QuoterConfiguration>> quoterConfigurations, QuoteStore store, ILogger<CoinbaseQuoteProvider> logger) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var threads = quoterConfigurations.Value
            .Where(c => c.Exchange == Exchange.Coinbase)
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
                await webSocket.ConnectAsync(new Uri($"wss://ws-feed.exchange.coinbase.com"), stoppingToken);
                await webSocket.SendJsonAsync(new
                {
                    type = "subscribe",
                    product_ids = new[]{instrumentCode},
                    channels = new[]{"ticker"}
                }, stoppingToken);
                while (true)
                {
                    var message = await webSocket.ReceiveAsync<CoinbaseQuote>(stoppingToken);
                    if (message == null)
                        continue;
                    if (message.Type == "ticker")
                        store.Set(new Quote(instrumentCode, Exchange.Coinbase, message.BestBid, message.BestAsk));
                }
            }
            catch (OperationCanceledException) when (stoppingToken.IsCancellationRequested)
            {
                break;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Coinbase connection error");
                await Task.Delay(TimeSpan.FromSeconds(2), stoppingToken);
            }
        }
    }
}

public class CoinbaseQuote
{
    [JsonProperty("type")]
    public string Type { get; set; }

    [JsonProperty("sequence")]
    public long Sequence { get; set; }

    [JsonProperty("product_id")]
    public string ProductId { get; set; }

    [JsonProperty("price")]
    public string Price { get; set; }

    [JsonProperty("open_24h")]
    public string Open24h { get; set; }

    [JsonProperty("volume_24h")]
    public string Volume24h { get; set; }

    [JsonProperty("low_24h")]
    public string Low24h { get; set; }

    [JsonProperty("high_24h")]
    public string High24h { get; set; }

    [JsonProperty("volume_30d")]
    public string Volume30d { get; set; }

    [JsonProperty("best_bid")]
    public decimal BestBid { get; set; }

    [JsonProperty("best_bid_size")]
    public decimal BestBidSize { get; set; }

    [JsonProperty("best_ask")]
    public decimal BestAsk { get; set; }

    [JsonProperty("best_ask_size")]
    public decimal BestAskSize { get; set; }

    [JsonProperty("side")]
    public string Side { get; set; }

    [JsonProperty("time")]
    public DateTime Time { get; set; }

    [JsonProperty("trade_id")]
    public int TradeId { get; set; }

    [JsonProperty("last_size")]
    public string LastSize { get; set; }
}



