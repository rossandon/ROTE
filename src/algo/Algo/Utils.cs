using System.Net.WebSockets;
using System.Text;
using Newtonsoft.Json;

namespace Algo;

public static class Utils
{
    public static async Task SendJsonAsync(this WebSocket ws, object obj, CancellationToken cancellationToken)
    {
        var json = JsonConvert.SerializeObject(obj);
        await ws.SendAsync(Encoding.UTF8.GetBytes(json), WebSocketMessageType.Text, true, cancellationToken);
    }

    public static async Task<T?> ReceiveAsync<T>(this WebSocket ws, CancellationToken cancellationToken)
    {
        var message = new List<byte>();
        var buffer = new byte[1024];
        while (true)
        {
            var resp = await ws.ReceiveAsync(buffer, cancellationToken);
            if (resp.Count > 0)
                message.AddRange(buffer.AsSpan(0, resp.Count));
            if (resp.EndOfMessage)
                break;
        }

        return JsonConvert.DeserializeObject<T>(Encoding.UTF8.GetString(message.ToArray()));
    }

    public static decimal AddBips(this decimal val, int bips)
    {
        return val + val * (bips / 10_000m);
    }
}