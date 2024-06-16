using Microsoft.Extensions.Options;

namespace Algo;

public class QuoterService(IOptions<List<QuoterConfiguration>> quoterConfiguration, IServiceProvider serviceProvider)
    : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var tasks = quoterConfiguration.Value.Select(NewQuoter)
            .Select(c => c.Run(stoppingToken))
            .ToArray();

        await Task.WhenAll(tasks);
    }

    private Quoter NewQuoter(QuoterConfiguration c)
    {
        var quoter = serviceProvider.GetRequiredService<Quoter>();
        quoter.Configuration = c;
        return quoter;
    }
}