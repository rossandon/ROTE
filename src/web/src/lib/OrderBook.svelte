<script lang="ts">
    import { onMount } from 'svelte'
    import {OrderBookEntry, OrderBookResponse} from "./Models";
    import OrderBookEntryList from "./OrderBookEntryList.svelte";

    let bookResponse: OrderBookResponse
    let bids: OrderBookEntry[]
    let asks: OrderBookEntry[]

    export let instrumentCode: string;

    onMount(() => {
        let socket = new WebSocket("ws://localhost:8081/market/data?instrumentCode=" + instrumentCode, "protocolOne")
        socket.onmessage = m => {
            var bookResponse = JSON.parse(m.data)
            bids = bookResponse.bids.reverse();
            asks = bookResponse.asks.reverse();
        }
    })
</script>

{#if bids != null && asks != null}
<table>
    <thead>
        <tr>
            <th>
                Price
            </th>
            <th>
                Size
            </th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <OrderBookEntryList on:order-canceled instrumentCode="{instrumentCode}" bookEntryList={asks}></OrderBookEntryList>
        <tr><td colspan="3">Mid</td></tr>
        <OrderBookEntryList on:order-canceled instrumentCode="{instrumentCode}" bookEntryList={bids}></OrderBookEntryList>
    </tbody>
</table>
{/if}

