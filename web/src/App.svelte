<script lang="ts">
  import Balances from './lib/Balances.svelte'
  import WhoAmI from "./lib/WhoAmI.svelte";
  import WhoAmIResponse from "./lib/WhoAmI.svelte";
  import Deposit from "./lib/Deposit.svelte";

  let user: WhoAmIResponse | null;
</script>

<main>
  <WhoAmI bind:user={user} />
  {#if user != null}
    <Balances />
    <Deposit />
  {:else}
    <a href="/oauth2/authorization/google">Login with Google</a>
  {/if}
</main>

<style>
</style>
