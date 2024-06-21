<script lang="ts">
    import {onMount} from 'svelte'

    class WhoAmIResponse {
        public name: string

        constructor(name: string) {
            this.name = name
        }
    }

    export let user: WhoAmIResponse | null = null

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
    <span class="px-3">Hello, {user.name}.</span>
    <a href="/logout"><button type="button" class="btn btn-outline-light me-2">Logout</button></a>
{/if}


