using System.Net.Http.Headers;
using System.Text;
using Algo;

var builder = Host.CreateApplicationBuilder(args);
builder.Services.AddHostedService(sp => sp.GetRequiredService<BinanceQuoteProvider>());
builder.Services.AddSingleton<BinanceQuoteProvider>();
builder.Services.AddTransient<Quoter>();
builder.Services.AddHttpClient<RoteClient>(c => { c.BaseAddress = new Uri("http://localhost:8081"); });

var quoterConfiguration = builder.Configuration.GetSection("Quoters").Get<QuoterConfiguration[]>();
if (quoterConfiguration == null)
    throw new Exception("Configuration not found");

foreach (var entry in quoterConfiguration)
{
    builder.Services.AddSingleton(IHostedService (sp) =>
    {
        var quoter = sp.GetRequiredService<Quoter>();
        quoter.Configuration = entry;
        return quoter;
    });
}

var host = builder.Build();
await host.RunAsync();