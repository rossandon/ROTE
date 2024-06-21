<script lang="ts">
    import { onMount } from "svelte";
    import { Trade } from "./Models";

    let trades: Trade[] = [];

    export let len = 11;
    export let instrumentCode: string;

    onMount(() => {
        var loc = window.location,
            wsUrlBase;
        if (loc.protocol === "https:") {
            wsUrlBase = "wss:";
        } else {
            wsUrlBase = "ws:";
        }
        wsUrlBase += "//" + loc.host;
        let interval: number;
        let socket = new WebSocket(
            wsUrlBase + "/market-data/trade?instrumentCode=" + instrumentCode,
        );
        socket.onopen = (m) => {
            // to Keep the connection alive
            interval = setInterval(() => {
                const sendMessage = JSON.stringify({ ping: 1 });
                socket.send(sendMessage);
            }, 5000);
        };
        socket.onclose = (m) => {
            clearInterval(interval);
        };
        socket.onmessage = (m) => {
            console.log(m.data);
            var trade = JSON.parse(m.data);
            trades = trades.concat(trade);
            if (trades.length > len) {
                trades = trades.slice(1);
            }
        };
    });
</script>

<div class="card">
    <div class="card-header d-flex justify-content-between">
        <div>Trades</div>
        <div>{instrumentCode}</div>
    </div>
    <div class="card-body">
        <table class="trades table table-sm">
            <thead>
                <tr>
                    <th> Price </th>
                    <th> Size </th>
                    <th> Time </th>
                </tr>
            </thead>
            <tbody>
                {#each trades as entry}
                    <tr>
                        <td>
                            {entry.price}
                        </td>
                        <td>
                            {entry.size}
                        </td>
                        <td>
                            {entry.timestamp}
                        </td>
                    </tr>
                {/each}
            </tbody>
        </table>
    </div>
</div>
