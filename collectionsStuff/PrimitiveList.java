package collectionsStuff;

public abstract class PrimitiveList {

	public static final int STARTCAPACITY = 20;

	protected int size;

	public long bytesAllocated() {
		return bytes_per_primitive() * capacity();
	}

	public long byteSize() {
		return bytes_per_primitive() * size;
	}

	public abstract int bytes_per_primitive();

	public abstract int capacity();

	protected float growthRate = 2;

	/**
	 * sets the growth rate of this PrimitiveList
	 * 
	 * @param gr *must* be *greater* than 1, else not accepted!
	 */
	public void setGrowthRate(float gr) {
		if (gr > 1)
			this.growthRate = gr;
	}

	public float growthRate() {
		return growthRate;
	}

	public void ensureCapacity(int min) {
		int nS = capacity();
		while (min > nS) {
			nS *= growthRate;
			nS++;
		}
		if (nS > capacity())
			grow(nS);
	}

	protected abstract void grow(int newSize);

	public abstract void removeAll(int startIndex, int endIndex);

	public boolean isEmpty() {
		return size == 0;
	}

	public void setSize(int s) {
		ensureCapacity(s);
		this.size = s;
	}

}
