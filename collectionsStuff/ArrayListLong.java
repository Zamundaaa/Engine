package collectionsStuff;

public class ArrayListLong extends PrimitiveList {

	private long[] values;

	public ArrayListLong() {
		this(STARTCAPACITY);
	}

	public ArrayListLong(int startCapacity) {
		values = new long[startCapacity];
	}

	public void add(long l) {
		ensureCapacity(size + 1);
		values[size] = l;
		size++;
	}

	public void set(int i, long l) {
		values[i] = l;
	}

	public long remove(int i) {
		if (i >= 0 && i < size) {
			long ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			values[size] = 0;
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	public long get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		long[] newValues = new long[newSize];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			values[i] = 0;
		}
		size = 0;
	}

	/**
	 * just returns {@link collectionsStuff.ArrayListLong#arrToString(values)}
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for (int i = 0; i < size - 1; i++) {
			b.append(values[i]);
			b.append(", ");
		}
		b.append(values[size - 1]);
		b.append(" ]");
		return b.toString();
	}

	public static String arrToString(long[] ls) {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for (int i = 0; i < ls.length - 1; i++) {
			b.append(ls[i]);
			b.append(", ");
		}
		b.append(ls[ls.length - 1]);
		b.append(" ]");
		return b.toString();
	}

	public long averageValue() {
		return sum() / size;
	}

	public long sum() {
		long ret = 0;
		for (int i = 0; i < size; i++)
			ret += values[i];
		return ret;
	}

	@Override
	public int capacity() {
		return values.length;
	}

	@Override
	public int bytes_per_primitive(){
		return 8;
	}

	public void quickSortLowToHigh() {
		rekQuickSortLowToHigh(0, size - 1);
	}

	public void rekQuickSortLowToHigh(int startIndex, int endIndex) {
		rekQuickSortLowToHigh(values, startIndex, endIndex);
	}

	public static void quickSortLowToHigh(long[] values) {
		rekQuickSortLowToHigh(values, 0, values.length - 1);
	}

	public static void rekQuickSortLowToHigh(long[] values, int startIndex, int endIndex) {

		if (startIndex >= endIndex) {
			return;
		}
		// System.out.println("Beginning to start from start: " + start + " to
		// end: " + end);
		// System.out.println(this);

		int i = startIndex;
		int k = endIndex - 1;
		long pivot = values[endIndex];

		do {
			while (values[i] <= pivot && i < endIndex) {
				i++;
			}

			while (values[k] >= pivot && k > startIndex) {
				k--;
			}

			if (i < k) {
				long temp = values[i];
				values[i] = values[k];
				values[k] = temp;
			}

			if (values[i] > pivot) {
				long temp = values[i];
				values[i] = values[endIndex];
				values[endIndex] = temp;
			}

			rekQuickSortLowToHigh(values, startIndex, i - 1);
			rekQuickSortLowToHigh(values, i + 1, endIndex);

		} while (i < k);
	}
	
	/**
	 * provides an efficient way to remove many elements at once. Should be
	 * exactly as fast as a single call to {@link ArrayListLong#remove(int)}}
	 * @param startIndex inclusive
	 * @param endIndex inclusive
	 */
	@Override
	public void removeAll(int startIndex, int endIndex) {
		if (endIndex < startIndex) {
			throw new IllegalArgumentException(
					"StartIndex (" + startIndex + ") is bigger than endIndex (" + endIndex + ')');
		}
		if (startIndex < 0 && startIndex >= size && endIndex < 0 && endIndex >= size) {
			throw new ArrayIndexOutOfBoundsException("startIndex (" + startIndex + ") or endIndex (" + endIndex
					+ ") is less than 0 or bigger than size (" + size + ")");
		} else {
			for(int i = endIndex; i < size; i++){
				values[startIndex+i-endIndex] = values[i];
			}
			size -= endIndex-startIndex+1;
		}
	}

}
