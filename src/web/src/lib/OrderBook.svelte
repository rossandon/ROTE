<script lang="ts">
    import { onMount } from 'svelte'
    import {OrderBookEntry, OrderBookResponse} from "./Models";
    import OrderBookEntryList from "./OrderBookEntryList.svelte";

    let bids: (OrderBookEntry | null)[]
    let asks: (OrderBookEntry | null)[]

    export let len = 5;
    export let instrumentCode: string;

    function applyLen(entries: (OrderBookEntry | null)[]) : (OrderBookEntry | null)[] {
        entries = entries.slice(0, len)
        entries = entries.concat(new Array(len - entries.length))
        return entries
    }

    onMount(() => {
        let socket = new WebSocket("ws://localhost:8081/market-data/book?instrumentCode=" + instrumentCode, "protocolOne")
        socket.onmessage = m => {
            var bookResponse = JSON.parse(m.data)
            bids = applyLen(bookResponse.bids);
            asks = applyLen(bookResponse.asks).reverse();
        }
    })
</script>

{#if bids != null && asks != null}
<table class="order-book">
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
        <tr class="mid"><td colspan="3">Mid</td></tr>
        <OrderBookEntryList on:order-canceled instrumentCode="{instrumentCode}" bookEntryList={bids}></OrderBookEntryList>
    </tbody>
</table>
{/if}

