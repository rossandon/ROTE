<script lang="ts">
    import { onMount } from "svelte";
    import { OrderBookEntry, OrderBookResponse } from "./Models";
    import OrderBookEntryList from "./OrderBookEntryList.svelte";

    let bids: (OrderBookEntry | null)[];
    let asks: (OrderBookEntry | null)[];

    export let len = 5;
    export let instrumentCode: string;

    function applyLen(
        entries: (OrderBookEntry | null)[],
    ): (OrderBookEntry | null)[] {
        entries = entries.slice(0, len);
        entries = entries.concat(new Array(len - entries.length));
        return entries;
    }

    onMount(() => {
        var loc = window.location,
            wsUrlBase;
        if (loc.protocol === "https:") {
            wsUrlBase = "wss:";
        } else {
            wsUrlBase = "ws:";
        }
        wsUrlBase += "//" + loc.host;
        let socket = new WebSocket(
            wsUrlBase + "/market-data/book?instrumentCode=" + instrumentCode,
        );
        socket.onmessage = (m) => {
            var bookResponse = JSON.parse(m.data);
            bids = applyLen(bookResponse.bids);
            asks = applyLen(bookResponse.asks).reverse();
        };
    });
</script>

<div class="card">
    <div class="card-header d-flex justify-content-between">
        <div>Order Book</div>
        <div>{instrumentCode}</div>
    </div>
    <div class="card-body">
        {#if bids != null && asks != null}
            <table class="order-book table table-sm">
                <thead>
                    <tr>
                        <th> Price </th>
                        <th> Size </th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <OrderBookEntryList
                        on:order-canceled
                        {instrumentCode}
                        bookEntryList={asks}
                    ></OrderBookEntryList>
                    <tr class="mid"><td colspan="3">Mid</td></tr>
                    <OrderBookEntryList
                        on:order-canceled
                        {instrumentCode}
                        bookEntryList={bids}
                    ></OrderBookEntryList>
                </tbody>
            </table>
        {/if}
    </div>
</div>
