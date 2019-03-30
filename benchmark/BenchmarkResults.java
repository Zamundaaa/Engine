package benchmark;

import collectionsStuff.ArrayListFloat;
import filters.FloatFilter;

public class BenchmarkResults {

	protected int width, height;
	protected FloatFilter frameTimeFilter = new FloatFilter();
	protected ArrayListFloat frameTimes = new ArrayListFloat(100_000);
	protected long lastPush = -1;

	public BenchmarkResults(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void nextFrame() {
		if (lastPush == -1) {
			lastPush = System.currentTimeMillis();
		} else {
			long diff = System.currentTimeMillis() - lastPush;
			lastPush = System.currentTimeMillis();
			frameTimes.add(diff);
			frameTimeFilter.push(diff);
		}
	}

	public FloatFilter frameTimeFilter() {
		return frameTimeFilter;
	}

	public ArrayListFloat frameTimes() {
		return frameTimes;
	}

}
