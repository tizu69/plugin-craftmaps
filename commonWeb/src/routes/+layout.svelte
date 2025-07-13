<script lang="ts">
	import '../app.css';

	let { children } = $props();
</script>

{@render children()}

<svelte:head>
	<meta name="og:title" content="CraftMaps" />
	<meta name="og:description" content="A Minecraft map like the real ones" />
	<meta name="theme-color" content="#7fcc19" />
</svelte:head>

