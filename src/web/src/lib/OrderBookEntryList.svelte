<script lang="ts">
    import type {OrderBookEntry} from "./Models";
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    export let bookEntryList: (OrderBookEntry | null)[]
    export let instrumentCode: string

    const handleCancel = e => {
        // getting the action url
        const ACTION_URL = e.target.action

        // get the form fields data and convert it to URLSearchParams
        const formData = new FormData(e.target)
        const data = new URLSearchParams()
        for (let field of formData) {
            const [key, value] = field
            data.append(key, value)
        }

        fetch(ACTION_URL, {
            method: 'POST',
            body: data
        }).then(value => {
            dispatch('order-canceled')
        })
    }
</script>

{#if bookEntryList.length === 0}
    <tr>
        <td colspan="3">
            No entries.
        </td>
    </tr>
{:else}
{#each bookEntryList as entry}
    {#if entry == null}
    <tr>
        <td colspan="3">-</td>
    </tr>
    {:else}
    <tr>
        <td>
            {entry.price}
        </td>
        <td>
            {entry.size}
        </td>
        <td>
            {#if entry.canCancel}
                <form class="order-cancel" method="post" action="/orders/cancel" on:submit|preventDefault={handleCancel}>
                    <input type="hidden" name="id" value="{entry.id}">
                    <input type="hidden" name="instrumentCode" value="{instrumentCode}">
                    <input type="submit" value="Cancel">
                </form>
            {/if}
        </td>
    </tr>
    {/if}

{/each}
{/if}