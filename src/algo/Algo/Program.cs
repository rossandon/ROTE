using Algo;

var builder = Host.CreateApplicationBuilder(args);

builder.Services.AddTransient<Quoter>();
builder.Services.AddSingleton<QuoteStore>();
builder.Services.Configure<List<QuoterConfiguration>>(builder.Configuration.GetSection("Quoters"));
builder.Services.AddHttpClient<RoteClient>(c => { c.BaseAddress = new Uri("http://localhost:8081"); });

builder.Services.AddHostedService<BinanceQuoteProvider>();
builder.Services.AddHostedService<CoinbaseQuoteProvider>();
builder.Services.AddHostedService<QuoterService>();

var host = builder.Build();
await host.RunAsync();