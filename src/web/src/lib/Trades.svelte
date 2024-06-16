<script lang="ts">
    import { onMount } from "svelte";
    import { Trade } from "./Models";

    let trades: Trade[] = []

    export let len = 11;
    export let instrumentCode: string;

    onMount(() => {
        let socket = new WebSocket(
            "ws://localhost:8081/market-data/trade?instrumentCode=" +
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
