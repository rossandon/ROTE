import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [svelte()],
  build:{
    outDir: '../core/webService/src/main/resources/static',
    emptyOutDir: true
  }
})
