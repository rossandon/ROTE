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
<h5 class="text-center">Balances</h5>
<table class="table">
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

