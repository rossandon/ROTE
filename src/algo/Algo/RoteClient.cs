using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text;

namespace Algo;

public class RoteClient(HttpClient client)
{
    public void SetUsername(string username)
    {
        var authenticationString = $"{username}:devpassword";
        var base64EncodedAuthenticationString = Convert.ToBase64String(Encoding.UTF8.GetBytes(authenticationString));
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", base64EncodedAuthenticationString);
    }
    
    public async Task<WhoAmIResponse> WhoAmI(CancellationToken cancellationToken)
    {
        var resp = await client.GetFromJsonAsync<WhoAmIResponse>("/system/whoami", cancellationToken);
        if (resp == null)
            throw new Exception("No response");
        return resp;
    }

    public async Task<BalanceResponse> GetBalances(CancellationToken cancellationToken)
    {
        var resp = await client.GetFromJsonAsync<BalanceResponse>("/balances/list", cancellationToken);
        if (resp == null)
            throw new Exception("No response");
        return resp;
    }

    public async Task Deposit(string assetCode, long amount, CancellationToken cancellationToken)
    {
        var resp = await client.PostAsync("/balances/deposit",
            new FormUrlEncodedContent(new List<KeyValuePair<string, string>>
            {
                new("assetCode", assetCode),
                new("amount", amount.ToString())
            }), cancellationToken);
        resp.EnsureSuccessStatusCode();
    }


    public async Task PlaceOrder(string instrumentCode, long price, long size, TradeSide side,
        CancellationToken cancellationToken)
    {
        var resp = await client.PostAsync("/orders/submit",
            new FormUrlEncodedContent(new List<KeyValuePair<string, string>>
            {
                new("instrumentCode", instrumentCode),
                new("price", price.ToString()),
                new("amount", size.ToString()),
                new("side", side.ToString())
            }), cancellationToken);
        resp.EnsureSuccessStatusCode();
    }

    public async Task CancelAll(string instrumentCode, CancellationToken cancellationToken)
    {
        var resp = await client.PostAsync("/orders/cancel-all",
            new FormUrlEncodedContent(new List<KeyValuePair<string, string>> { new("instrumentCode", instrumentCode) }),
            cancellationToken);
        resp.EnsureSuccessStatusCode();
    }
}

public class BalanceResponse : Dictionary<string, decimal>
{
}

public enum TradeSide
{
    Buy,
    Sell
}

public class WhoAmIResponse
{
    public string Name { get; set; }
    public long AccountId { get; set; }
}