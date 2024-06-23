<script>
    import { createEventDispatcher } from "svelte";

    const dispatch = createEventDispatcher();

    const handleDeposit = (e) => {
        // getting the action url
        const ACTION_URL = e.target.action;

        // get the form fields data and convert it to URLSearchParams
        const formData = new FormData(e.target);
        const data = new URLSearchParams();
        for (let field of formData) {
            const [key, value] = field;
            data.append(key, value);
        }

        fetch(ACTION_URL, {
            method: "POST",
            body: data,
        }).then((value) => {
            e.target.reset();
            dispatch("deposit", {});
        });
    };
</script>
<h5 class="text-center"><span class="p-1 border-bottom" style="width: 200px">Deposit</span></h5>
<form
    method="post"
    action="/balances/deposit"
    on:submit|preventDefault={handleDeposit}
>
    <div class="mb-3">
        <label for="deposit-asset" class="form-label">Asset</label><input
            class="form-control"
            id="deposit-asset"
            type="text"
            name="assetCode"
            placeholder="USD"
        />
    </div>
    <div class="mb-3">
        <label for="deposit-amount" class="form-label">Amount</label>
        <input
            class="form-control"
            id="deposit-amount"
            type="text"
            name="amount"
            placeholder="100"
        />
    </div>
    <input class="btn btn-primary" type="submit" value="Deposit" />
</form>
