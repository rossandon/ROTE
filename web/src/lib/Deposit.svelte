<script>
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

        // check the form's method and send the fetch accordingly
        if (e.target.method.toLowerCase() === 'get') {
            fetch(`${ACTION_URL}?${data}`)
        }
        else {
            fetch(ACTION_URL, {
                method: 'POST',
                body: data
            })
        }
    }
</script>

<form method="post" action="/balances/deposit" on:submit|preventDefault={handleDeposit}>
    Asset: <input type="text" name="assetCode">
    <br/>
    Amount: <input type="text" name="amount">
    <br/>
    <input type="submit" value="Deposit">
</form>