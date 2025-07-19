<script lang="ts">
	import { page } from '$app/state';
	import { colors, Map } from '$lib';
</script>

<div class="absolute inset-0">
	<Map
		pos={{
			// @ts-expect-error idc
			x: +page.url.searchParams.get('x') || 0,
			// @ts-expect-error idc
			z: +page.url.searchParams.get('z') || 0,
			world: 'world'
		}}
	/>
</div>

<div class="bgc-root m-4 w-fit">
	<div class="flex justify-between pb-3">
		<p class="text-gray">Chess Chest</p>
		<div class="size-7" style="background: rgb({colors.get('DIAMOND', 0).join(',')});"></div>
	</div>
	<div class="mx-auto grid w-fit grid-cols-8">
		{#each { length: 8 * 8 } as _, i}
			<div
				class={[
					'size-16',
					(i + Math.floor(i / 8)) % 2 == 0 ? 'bgc-inset' : 'bgc-insetlong'
				]}
			></div>
		{/each}
	</div>

	<div class="flex items-center justify-between py-1">
		<p class="text-gray">Inventory</p>
		<input type="text" class="input bgc-inset text-shadow w-64 px-2" placeholder="Search..." />
	</div>
	<div class="grid grid-cols-9">
		{#each { length: 9 * 3 } as _}
			<div class="bgc-inset size-16"></div>
		{/each}
	</div>
	<div class="grid grid-cols-9 pt-3">
		{#each { length: 9 } as _}
			<div class="bgc-inset size-16"></div>
		{/each}
	</div>
</div>
