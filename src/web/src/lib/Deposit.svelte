<script>
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    const handleDeposit = e => {
        // getting the action url
        const ACTION_URL = e.target.action

        // get the form fields data and convert it to URLSearchParams
        const formData = new FormData(e.target)
        const data = new URLSearchParams()
        for (let field of formData) {
            const [key, value] = field
            data.append(key, value)
        }

        fetch(ACTION_URL, {
            method: 'POST',
            body: data
        }).then(value => {
            e.target.reset()
            dispatch('deposit', {})
        })
    }
</script>

<form method="post" action="/balances/deposit" on:submit|preventDefault={handleDeposit}>
    <label>Asset<input type="text" name="assetCode" placeholder="USD"></label>
    <label>Amount<input type="text" name="amount" placeholder="100"></label>
    <input type="submit" value="Deposit">
</form>