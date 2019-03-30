package collectionsStuff;

public class ArrayListB extends PrimitiveList {

	protected byte[] values;

	public ArrayListB() {
		this(STARTCAPACITY);
	}

	public ArrayListB(int startCapacity) {
		values = new byte[startCapacity];
	}

	public ArrayListB(byte[] data) {
		values = data;
		size = values.length;
	}

	public void add(byte b) {
		ensureCapacity(size + 1);
		values[size++] = b;
	}

	public void set(int i, byte b) {
		values[i] = b;
	}

	public byte remove(int i) {
		if (i >= 0 && i < size) {
			byte ret = values[i];
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

	public byte get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		byte[] newValues = new byte[newSize];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		// unnecessary!
		// for (int i = 0; i < size; i++) {
		// values[i] = 0;
		// }
		size = 0;
	}

	public byte[] capToArray() {
		byte[] newValues = new byte[size];
		for (int i = 0; i < size; i++) {
			newValues[i] = values[i];
		}
		return newValues;
	}

	public ArrayListB addAll(byte[] data) {
		for (int i = 0; i < data.length; i++)
			add(data[i]);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int i = 0; i < size; i++) {
			sb.append(values[i]);
			if (i != size - 1)
				sb.append(", ");
		}
		sb.append(" ]");
		return sb.toString();
	}

	public void quickSortLowToHigh() {
		rekQuickSortLowToHigh(0, size - 1);
	}

	public void rekQuickSortLowToHigh(int start, int end) {

		if (start >= end) {
			return;
		}
		// System.out.println("Beginning to start from start: " + start + " to
		// end: " + end);
		// System.out.println(this);

		int i = start;
		int k = end - 1;
		byte pivot = values[end];

		do {
			while (values[i] <= pivot && i < end) {
				i++;
			}

			while (values[k] >= pivot && k > start) {
				k--;
			}

			if (i < k) {
				byte temp = values[i];
				values[i] = values[k];
				values[k] = temp;
			}

			if (values[i] > pivot) {
				byte temp = values[i];
				values[i] = values[end];
				values[end] = temp;
			}

			rekQuickSortLowToHigh(start, i - 1);
			rekQuickSortLowToHigh(i + 1, end);

		} while (i < k);
	}

	/**
	 * Be cautious! When bytes are added, this Array may be changed to fit, and then
	 * the reference doesn't work anymore
	 * 
	 * @return the current array of this ArrayListB
	 */
	public byte[] getArray() {
		return values;
	}

	@Override
	public int capacity() {
		return values.length;
	}

	@Override
	public int bytes_per_primitive() {
		return 1;
	}

	public static String toString(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int i = 0; i < bs.length; i++) {
			sb.append(bs[i]);
			if (i != bs.length - 1)
				sb.append(", ");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * provides an efficient way to remove many elements at once. Should be exactly
	 * as fast as a single call to {@link ArrayListB#remove(int)}}
	 * 
	 * @param startIndex inclusive
	 * @param endIndex   inclusive
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
			for (int i = endIndex; i < size; i++) {
				values[startIndex + i - endIndex] = values[i];
			}
			size -= endIndex - startIndex + 1;
		}
	}

	public void fill(int index, int count, byte b) {
		for (int i = index; i < count; i++)
			values[i] = b;
	}

	public void addByte(int b) {
		add((byte) b);
	}

}
