using System.Net.Http.Headers;
using System.Text;
using Algo;

var builder = Host.CreateApplicationBuilder(args);
builder.Services.AddHostedService(sp => sp.GetRequiredService<BinanceQuoteProvider>());
builder.Services.AddSingleton<BinanceQuoteProvider>();
builder.Services.AddHostedService<Quoter>();
builder.Services.AddHttpClient<RoteClient>(c =>
{
    c.BaseAddress = new Uri("http://localhost:8081");
    var authenticationString = $"ryan:devpassword";
    var base64EncodedAuthenticationString = Convert.ToBase64String(Encoding.UTF8.GetBytes(authenticationString));
    c.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", base64EncodedAuthenticationString);
});

var host = builder.Build();
var log = host.Services.GetRequiredService<ILogger<Program>>();

var roteClient = host.Services.GetRequiredService<RoteClient>();
var whoAmIResponse = await roteClient.WhoAmI(CancellationToken.None);
log.LogInformation($"Running as {whoAmIResponse.Name}");
await host.RunAsync();
