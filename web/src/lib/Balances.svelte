<script lang="ts">
  import { onMount } from 'svelte'

  let balances = {}

  export function refresh() {
    fetch('balances/list')
            .then(response => response.json() as Promise<{data: Object}>)
            .then(result => balances = result)
  }

  onMount(() => {
    refresh()
  })
</script>

<table>
  <tr>
    <th style="min-width: 200px">
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

