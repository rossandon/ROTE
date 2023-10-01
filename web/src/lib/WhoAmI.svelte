<script lang="ts">
    import {onMount} from 'svelte'

    class WhoAmIResponse {
        public name: string

        constructor(name: string) {
            this.name = name
        }
    }

    let user: WhoAmIResponse | null

    async function getUsers() {
        try {
            // 👇️ const response: Response
            const response = await fetch('system/whoami');

            if (!response.ok) {
                throw new Error(`Error! status: ${response.status}`);
            }

            // 👇️ const result: GetUsersResponse
            return (await response.json()) as WhoAmIResponse;
        } catch (error) {
            if (error instanceof Error) {
                console.log('error message: ', error.message);
                return null;
            } else {
                console.log('unexpected error: ', error);
                return null;
            }
        }
    }

    onMount(async () => {
        user = await getUsers()
    })
</script>

{#if user != null}
    Hello, {user.name}
{/if}


