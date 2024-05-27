using Algo;

var builder = Host.CreateApplicationBuilder(args);
builder.Services.AddHostedService(sp => sp.GetRequiredService<BinanceQuoteProvider>());
builder.Services.AddSingleton<BinanceQuoteProvider>();
builder.Services.AddHttpClient();

var host = builder.Build();
await host.RunAsync();
