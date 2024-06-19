<script lang="ts">
    import { onMount } from "svelte";
    import { Trade } from "./Models";

    let trades: Trade[] = []

    export let len = 11;
    export let instrumentCode: string;

    onMount(() => {
        var loc = window.location, wsUrlBase;
        if (loc.protocol === "https:") {
            wsUrlBase = "wss:";
        } else {
            wsUrlBase = "ws:";
        }
        wsUrlBase += "//" + loc.host;
        let socket = new WebSocket(
            wsUrlBase + "/market-data/trade?instrumentCode=" +
                instrumentCode,
            "protocolOne",
        );
        socket.onmessage = (m) => {
            console.log(m.data)
            var trade = JSON.parse(m.data);
            trades = trades.concat(trade);
            if (trades.length > len) {
                trades = trades.slice(1)
            }
        };
    });
</script>

<table class="trades">
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
