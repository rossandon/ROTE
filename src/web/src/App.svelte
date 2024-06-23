<script lang="ts">
  import Balances from "./lib/Balances.svelte";
  import WhoAmI from "./lib/WhoAmI.svelte";
  import WhoAmIResponse from "./lib/WhoAmI.svelte";
  import Deposit from "./lib/Deposit.svelte";
  import OrderBook from "./lib/OrderBook.svelte";
  import PlaceOrder from "./lib/PlaceOrder.svelte";
  import Trades from "./lib/Trades.svelte";

  let user: WhoAmIResponse | null;
  let refreshBalances: () => void;

  function handleDeposit() {
    refreshBalances();
  }

  function handleOrder() {
    refreshBalances();
  }
</script>

<div>
  <div
    class="offcanvas show offcanvas-start"
    tabindex="-1"
    id="offcanvas"
    aria-labelledby="offcanvasLabel"
    data-bs-scroll="true" data-bs-backdrop="false" data-bs-keyboard="false"
  >
    <div class="offcanvas-header">
    </div>
    <div class="offcanvas-body">
      <div class="container">
        <div class="row">
          <Balances bind:refresh={refreshBalances} />
        </div>
        <br/>
        <br/>
        <div class="row">
          <Deposit on:deposit={handleDeposit} />
        </div>
      </div>
    </div>
  </div>
  <div class="container">
    <div class="row">
      <header class="d-flex py-3 mb-4 border-bottom">
        <a
          href="/"
          class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none"
        >
          <span class="fs-4 app-title">ROTE</span>
        </a>
        <div class="text-end">
          <WhoAmI bind:user />
        </div>
      </header>
    </div>
    <div class="row">
      <div class="col">
        <OrderBook on:order-canceled={handleOrder} instrumentCode="BTC/USD"
        ></OrderBook>
      </div>
      <div class="col">
        <PlaceOrder on:submit-order={handleOrder}></PlaceOrder>
      </div>
      <div class="col">
        <Trades instrumentCode="BTC/USD"></Trades>
      </div>
    </div>
  </div>
</div>
