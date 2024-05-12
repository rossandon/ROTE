<script lang="ts">
  import Balances from './lib/Balances.svelte'
  import WhoAmI from "./lib/WhoAmI.svelte";
  import WhoAmIResponse from "./lib/WhoAmI.svelte";
  import Deposit from "./lib/Deposit.svelte";

  let user: WhoAmIResponse | null;
  let refreshBalances;

  function handleDeposit() {
    refreshBalances()
  }
</script>

<main>
  <header>
    <WhoAmI bind:user={user} />
  </header>
    {#if user != null}
      <div class="test">
        <div>
          <Balances bind:refresh={refreshBalances} />
        </div>
        <div>
          <Deposit on:deposit={handleDeposit} />
        </div>
      </div>
    {:else}
      <a href="/oauth2/authorization/google">Login with Google</a>
    {/if}
</main>

<style>
</style>
