<script>
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    const handleOrder = e => {
        // getting the action url
        const ACTION_URL = e.target.action

        // get the form fields data and convert it to URLSearchParams
        const formData = new FormData(e.target)
        const data = new URLSearchParams()
        for (let field of formData) {
            const [key, value] = field
            data.append(key, value)
        }

        let side = e.submitter.name === "buy" ? "Buy" : "Sell";
        data.append("side", side)

        fetch(ACTION_URL, {
            method: 'POST',
            body: data
        }).then(value => {
            e.target.reset()
            dispatch('submit-order', {})
        })
    }
</script>

<form method="post" action="/orders/submit" on:submit|preventDefault={handleOrder}>
    <label>Instrument<input type="text" name="instrumentCode" placeholder="SPY"></label>
    <label>Amount<input type="text" name="amount" placeholder="10"></label>
    <label>Price<input type="text" name="price" placeholder="$100"></label>
    <input type="submit" name="buy" value="Buy">
    <input type="submit" name="sell" value="Sell">
</form>