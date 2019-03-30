package collectionsStuff;

import java.nio.IntBuffer;
import java.util.Arrays;

public class ArrayListInt extends PrimitiveList {

	private int[] values;

	/**
	 * creates an empty ArrayList for integers with a capacity of
	 * {@link PrimitiveList#STARTCAPACITY}
	 */
	public ArrayListInt() {
		this(STARTCAPACITY);
	}

	/**
	 * creates an empty ArrayList for integers with a capacity of startCapacity
	 */
	public ArrayListInt(int startCapacity) {
		values = new int[startCapacity];
	}

	public void add(int v) {
		ensureCapacity(size + 1);
		values[size] = v;
		size++;
	}

	public void set(int i, int v) {
		values[i] = v;
	}

	/**
	 * retrieves and removes the value at index i
	 */
	public int remove(int i) {
		if (i >= 0 && i < size) {
			int ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			values[size] = 0;
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(
					"Failed to remove a value with index " + i + " because it's smaller than 0 or >= " + size);
		}
	}

	public int get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		int[] newValues = new int[newSize];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		size = 0;
	}

	public int[] capToArray() {
		int[] ret = new int[size];
		for (int i = 0; i < ret.length; i++)
			ret[i] = values[i];
		return ret;
	}

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

	public static String arrToString(int[] is) {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		for (int i = 0; i < is.length - 1; i++) {
			b.append(is[i]);
			b.append(", ");
		}
		b.append(is[is.length - 1]);
		b.append(" ]");
		return b.toString();
	}

	public void addAll(ArrayListInt a) {
		for (int i = 0; i < a.size; i++)
			add(a.get(i));
	}

	public void addAll(ArrayListInt a, int add) {
		for (int i = 0; i < a.size; i++)
			add(a.get(i) + add);
	}

	private int lastSearchedInt, lastFoundIndex;

	/**
	 * @return the first index with the specified value in place; If the value isn't
	 *         contained in this list, -1
	 */
	public int indexOf(int someInt) {
		if (size > 0)
			if (someInt == lastSearchedInt && lastFoundIndex > 0 && lastFoundIndex < size
					&& values[lastFoundIndex] == someInt) {
				return lastFoundIndex;
			} else {
				for (int i = 0; i < size; i++) {
					if (values[i] == someInt)
						return i;
				}
			}
		return -1;
	}

	/**
	 * @return the *current* backing array. Changes when capacity() does
	 */
	public int[] getArray() {
		return values;
	}

	@Override
	public int capacity() {
		return values.length;
	}

	@Override
	public int bytes_per_primitive() {
		return 4;
	}

	private IntBuffer last;

	public IntBuffer getIntBuffer() {
		if (last == null || last.array() != values) {
			last = IntBuffer.wrap(values, 0, Math.max(0, size - 1));
		} else if (last.limit() != Math.max(0, size - 1)) {
			last.limit(Math.max(0, size - 1));
		}
		return last;
	}

	/**
	 * like {@link ArrayListInt#copyTo(ArrayListInt)} but uses
	 * {@link System#arraycopy(Object, int, Object, int, int)}. Gives some speed
	 * bonus for big lists
	 */
	public ArrayListInt copyToNative(ArrayListInt a) {
		a.ensureCapacity(size);
		System.arraycopy(values, 0, a.values, 0, size);
		a.size = size;
		return a;
	}

	public ArrayListInt copyTo(ArrayListInt a) {
		a.clear();
		a.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			a.values[i] = values[i];
		}
		a.size = size;
		return a;
	}

	public ArrayListInt copyToAndClear(ArrayListInt a) {
		int s = a.size;
		a.clear();
		a.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			a.values[i] = values[i];
		}
		a.size = size;
		for (int i = size; i < s; i++) {
			a.values[i] = 0;
		}
		return a;
	}

	public void clearAndNullify() {
		Arrays.fill(values, 0);
		clear();
	}

	/**
	 * @return if {@link #indexOf(int)} > -1
	 */
	public boolean contains(int i) {
		return indexOf(i) > -1;
	}

	public int removeValue(int value) {
		int i = indexOf(value);
		if (i != -1) {
			remove(i);
		}
		return i;
	}

	public static interface ForIteration {
		void process(int index, int value);
	}

	public void iterate(ForIteration f) {
		for (int i = 0; i < size; i++) {
			f.process(i, values[i]);
		}
	}

	/**
	 * provides an efficient way to remove many elements at once. Should be exactly
	 * as fast as a single call to {@link ArrayListInt#remove(int)}}
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

	public float sum() {
		float r = 0;
		for (int i = 0; i < size; i++)
			r += values[i];
		return r;
	}

	public float average() {
		return sum() / size;
	}

	public int maxValue() {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < size; i++)
			if (values[i] > max)
				max = values[i];
		return max;
	}

	public int minValue() {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < size; i++)
			if (values[i] < min)
				min = values[i];
		return min;
	}

	/**
	 * TODO make native version
	 */
	public void moveTo(ArrayListInt a, int startIndex, int count) {
		for (int i = startIndex; i < count && i < size; i++) {
			a.add(values[i]);
		}
		removeAll(startIndex, Math.max(startIndex + count - 1, size - 1));
	}

	public void quicksortUp() {
		quicksortUp(0, size - 1);
	}

	public void quicksortUp(int low, int high) {
		if (low >= high)
			return;
		int pivotValue = values[(low + high) / 2];
		int i = low, j = high;

		while (i <= j) {
			while (values[i] < pivotValue && i <= j)
				i++;
			while (values[j] > pivotValue && i <= j)
				j--;
			if (i <= j) {
				if (i < j) {
					int f = values[j];
					values[j] = values[i];
					values[i] = f;
				}
				i++;
				j--;
			}
		}

		quicksortUp(low, j);
		quicksortUp(i, high);
	}

}
