<script lang="ts">
  import Balances from './lib/Balances.svelte'
  import WhoAmI from "./lib/WhoAmI.svelte";
  import WhoAmIResponse from "./lib/WhoAmI.svelte";
  import Deposit from "./lib/Deposit.svelte";
  import OrderBook from "./lib/OrderBook.svelte";
  import PlaceOrder from "./lib/PlaceOrder.svelte";

  let user: WhoAmIResponse | null;
  let refreshBalances: () => void;
  let refreshBook: () => void;

  function handleDeposit() {
    refreshBalances()
  }

  function handleOrder() {
    refreshBalances()
    refreshBook()
  }
</script>

<main>
  <header>
    <WhoAmI bind:user={user} />
  </header>
    {#if user != null}
      <div class="row">
        <div>
          <OrderBook on:order-canceled={handleOrder} bind:refresh={refreshBook} instrumentCode="BTC/USD"></OrderBook>
        </div>
        <div>
          <PlaceOrder on:submit-order={handleOrder}></PlaceOrder>
        </div>
      </div>
      <div class="row">
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
