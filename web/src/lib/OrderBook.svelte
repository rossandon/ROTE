<script lang="ts">
    import { onMount } from 'svelte'

    let balances = {}

    onMount(() => {
        fetch('balances/list')
            .then(response => response.json() as Promise<{data: Object}>)
            .then(result => balances = result)
    })
</script>

<table>
    <tr>
        <th>
            Asset
        </th>
        <th>
            Balance
        </th>
    </tr>
    {#each Object.keys(balances) as balance}
        <tr>
            <td>
                {balance}
            </td>
            <td>
                {balances[balance]}
            </td>
        </tr>
    {/each}
</table>

