<h1>4x: Melcome to SvelteKit</h1>
<p>Visit <a href="https://svelte.dev/docs/kit">svelte.dev/docs/kit</a> to read the documentation</p>

<div class="bgc-root m-4 flex flex-col gap-3 w-fit">
	<p class="text-gray">Large Chest</p>
	<div class="grid grid-cols-9">
		{#each {length: 27} as _}
		<div class="bgc-inset size-16"></div>
		{/each}
	</div>

	<p class="text-gray">Inventory</p>
	<div class="grid grid-cols-9">
		{#each {length: 27+9} as _}
		<div class="bgc-inset size-16"></div>
		{/each}
	</div>
</div>

<p class='bgc-root m-4 text-black p-12'>
	localhost:5173
</p>

<p>&lt;thaizoo&gt; aopdsj</p>
