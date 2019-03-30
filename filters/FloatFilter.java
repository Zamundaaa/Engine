package filters;

public class FloatFilter {

	// NaN -> not yet set
	protected float lastValue = Float.NaN;
	protected float currentValue = Float.NaN;
	protected float dV, avg, avg2;
	protected float integral;

	protected FloatFilter derivative;

	protected long lastPush = System.currentTimeMillis(), avgTime = 100, avgTime2 = 10_000, dTime = 20;

	public FloatFilter() {

	}

	public FloatFilter(float start) {
		lastValue = start;
		currentValue = start;
	}

	public void push(float f) {
		push(f, currentdt());
	}

	/*
	 * @param f NaN not permitted
	 */
	public void push(float f, long t) {
		if (Float.isNaN(currentValue)) {
			avg = currentValue = f;
			integral = 0;
		}
		lastValue = currentValue;
		currentValue = f;
		dV = (currentValue - lastValue) / t;
		if (derivative != null)
			derivative.push(dV, t);
		float avgFact = Math.min(1, t / (float) avgTime);
		avg = avg * (1 - avgFact) + currentValue * avgFact;

		avgFact = Math.min(1, t / (float) avgTime2);
		avg2 = avg2 * (1 - avgFact) + currentValue * avgFact;

		integral += f * t * 0.001f;

		lastPush = System.currentTimeMillis();
	}

	public float value() {
		return currentValue;
	}

	public float lastValue() {
		return lastValue;
	}

	public float avg() {
		return avg;
	}

	public long avgTime() {
		return avgTime;
	}

	public void setAvgTime(long millis) {
		this.avgTime = millis;
	}

	public float avg2() {
		return avg2;
	}

	public long avgTime2() {
		return avgTime2;
	}

	public void setAvgTime2(long millis) {
		this.avgTime2 = millis;
	}

	/*
	 * The current diff between the current value and the last
	 */
	public float derivative_imminent() {
		return dV;
	}

	public void useDerivativeFilter(boolean b) {
		if (b) {
			if (derivative == null)
				derivative = new FloatFilter(dV);
		} else {
			derivative = null;
		}
	}

	public FloatFilter derivative() {
		return derivative;
	}

	/*
	 * the sum of all elements pushed to this times the time in between. So
	 * basically an integral over the graph value, time
	 */
	public float integral() {
		return integral;
	}
	
	public long lastPush() {
		return lastPush;
	}
	
	public long currentdt() {
		return System.currentTimeMillis() - lastPush;
	}
	
}
