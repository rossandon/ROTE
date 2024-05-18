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
  <thead>
    <tr>
      <th>
        Asset
      </th>
      <th>
        Balance
      </th>
    </tr>
  </thead>
  <tbody>
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
  </tbody>
</table>

