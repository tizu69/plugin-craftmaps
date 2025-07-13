<script lang="ts">
	import { colors } from '$lib';
</script>

<h1>4x: Melcome to SvelteKit</h1>
<p>Visit <a href="https://svelte.dev/docs/kit">svelte.dev/docs/kit</a> to read the documentation</p>

<!-- style="background: rgb({colors.get("WOOD", 0).join(",")});" -->
<div class="bgc-root m-4 w-fit">
	<div class="flex justify-between pb-3">
		<p class="text-gray">Chess Chest</p>
		<div class="size-7" style="background: rgb({colors.get('WOOD', 0).join(',')});"></div> 
	</div>
	<div class="grid grid-cols-8 w-fit mx-auto">
		{#each { length: 8*8 } as _, i}
			<div class={[
				"size-16",
				(i+Math.floor(i/8)) % 2 == 0 ? "bgc-inset" : "bgc-insetlong"
			]}></div>
		{/each}
	</div>

	<div class="flex items-center justify-between py-1">
		<p class="text-gray">Inventory</p>
		<input type="text" class="input bgc-inset text-shadow w-64 px-2" placeholder="Search..." />
	</div>
	<div class="grid grid-cols-9">
		{#each { length: 9*3 } as _}
			<div class="bgc-inset size-16"></div>
		{/each}
	</div>
	<div class="grid grid-cols-9 pt-3">
		{#each { length: 9 } as _}
			<div class="bgc-inset size-16"></div>
		{/each}
	</div>
</div>

<p class="bgc-root m-4 p-12 text-black">localhost:5173</p>

<p>&lt;thaizoo&gt; aopdsj</p>
