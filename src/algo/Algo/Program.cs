using Algo;

var builder = Host.CreateApplicationBuilder(args);

builder.Services.Configure<RoteConfiguration>(builder.Configuration.GetSection("Rote"));
builder.Services.Configure<List<QuoterConfiguration>>(builder.Configuration.GetSection("Quoters"));

builder.Services.AddTransient<Quoter>();
builder.Services.AddSingleton<QuoteStore>();
builder.Services.AddHttpClient<RoteClient>();

builder.Services.AddHostedService<BinanceQuoteProvider>();
builder.Services.AddHostedService<CoinbaseQuoteProvider>();
builder.Services.AddHostedService<QuoterService>();

var host = builder.Build();
await host.RunAsync();

public class RoteConfiguration
{
    public string Url { get; set; }
    public string Password { get; set; }
}