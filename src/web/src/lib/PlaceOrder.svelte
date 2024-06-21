<script>
    import { createEventDispatcher } from "svelte";

    const dispatch = createEventDispatcher();

    const handleOrder = (e) => {
        // getting the action url
        const ACTION_URL = e.target.action;

        // get the form fields data and convert it to URLSearchParams
        const formData = new FormData(e.target);
        const data = new URLSearchParams();
        for (let field of formData) {
            const [key, value] = field;
            data.append(key, value);
        }

        let side = e.submitter.name === "buy" ? "Buy" : "Sell";
        data.append("side", side);

        fetch(ACTION_URL, {
            method: "POST",
            body: data,
        }).then((value) => {
            e.target.reset();
            dispatch("submit-order", {});
        });
    };
</script>

<div class="card">
    <div class="card-header">Place Order</div>
    <div class="card-body">
        <form
            method="post"
            action="/orders/submit"
            on:submit|preventDefault={handleOrder}
        >
            <div class="mb-3">
                <label for="order-instrument" class="form-label"
                    >Instrument</label
                >
                <input
                    class="form-control"
                    id="order-instrument"
                    type="text"
                    name="instrumentCode"
                    placeholder="SPY"
                />
            </div>
            <div class="mb-3">
                <label for="order-amount" class="form-label">Amount</label>
                <input
                    class="form-control"
                    id="order-amount"
                    type="text"
                    name="amount"
                    placeholder="10"
                />
            </div>
            <div class="mb-3">
                <label for="order-price" class="form-label">Price</label>
                <input
                    class="form-control"
                    id="order-price"
                    type="text"
                    name="price"
                    placeholder="$100"
                />
            </div>
            <input
                type="submit"
                name="buy"
                value="Buy"
                class="btn btn-primary"
            />
            <input
                type="submit"
                name="sell"
                value="Sell"
                class="btn btn-primary"
            />
        </form>
    </div>
</div>
