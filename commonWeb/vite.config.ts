import devtoolsJson from 'vite-plugin-devtools-json';
import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [tailwindcss(), sveltekit(), devtoolsJson()],
	server: {
		proxy: {
			// in dev, proxy /api requests to the API server - the "real" server.
			// in production, the API server routes the frontend requests, so this is ignored.
			'/api': { target: 'http://localhost:8080', changeOrigin: true }
		}
	}
});
