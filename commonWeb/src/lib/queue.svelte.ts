export default class Queue {
	readonly #queue: (() => void)[] = [];

	constructor(processEvery?: number) {
		if (processEvery) setInterval(() => this.process(), processEvery);
	}
	push(task: () => void) {
		this.#queue.push(task);
	}
	process() {
		if (!this.#queue.length) return;
		this.#queue.shift()!();
	}
}
