<script lang="ts">
    import { onMount } from 'svelte'
    import {OrderBookResponse} from "./Models";
    import OrderBookEntryList from "./OrderBookEntryList.svelte";

    let bookResponse: OrderBookResponse
    export let instrumentCode: string;

    export function refresh() {
        fetch(`book?instrumentCode=${instrumentCode}`)
            .then(response => response.json() as Promise<OrderBookResponse>)
            .then(result => {
                bookResponse = result;
            })
    }

    onMount(() => {
        refresh()
    })
</script>

{#if bookResponse != null}
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
        <OrderBookEntryList on:order-canceled instrumentCode="{instrumentCode}" bookEntryList={bookResponse.asks.reverse()}></OrderBookEntryList>
        <tr><td colspan="3">Mid</td></tr>
        <OrderBookEntryList on:order-canceled instrumentCode="{instrumentCode}" bookEntryList={bookResponse.bids.reverse()}></OrderBookEntryList>
    </tbody>
</table>
{/if}

