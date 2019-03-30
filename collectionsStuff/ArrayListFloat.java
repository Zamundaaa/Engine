package collectionsStuff;

import java.nio.FloatBuffer;

import tools.Meth;
import tools.misc.interfaces.NotifierInterface;

public class ArrayListFloat extends PrimitiveList {

	private float[] values;

	public ArrayListFloat() {
		this(STARTCAPACITY);
	}

	public ArrayListFloat(int startCapacity) {
		values = new float[startCapacity];
	}

	public void add(float f) {
		ensureCapacity(size + 1);
		values[size] = f;
		size++;
	}

	public void add(double d) {
		add((float) d);
	}

	public void set(int i, float f) {
		values[i] = f;
	}

	public void add(int i, float f) {
		ensureCapacity(i + 1);
		float b;
		for (int j = size; j > i; j--) {
			b = values[j];
			values[j] = values[j - 1];
			values[j - 1] = b;
		}
		values[i] = f;
		size++;
	}

	/**
	 * removes a single element of the list at index i and shifts all following
	 * elements one index down
	 */
	public float remove(int i) {
		if (i >= 0 && i < size) {
			float ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	/**
	 * provides an efficient way to remove many elements at once. Should be exactly
	 * as fast as a single call to {@link ArrayListFloat#remove(int)}}
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

	public float get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		float[] newValues = new float[newSize];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		size = 0;
	}

	@Override
	public String toString() {
		return arrayToString(values);
	}

	public static String arrayToString(float[] arr) {
		StringBuilder ret = new StringBuilder();
		ret.append("[ ");
		for (int i = 0; i < arr.length - 1; i++) {
			ret.append(arr[i]);
			ret.append(", ");
		}
		ret.append(arr[arr.length - 1]);
		ret.append(" ]");
		return ret.toString();
	}

	public float[] capToArray() {
		float[] ret = new float[size];
		for (int i = 0; i < size; i++)
			ret[i] = values[i];
		return ret;
	}

	public void addAll(ArrayListFloat a) {
		for (int i = 0; i < a.size; i++)
			add(a.get(i));
	}

	@Override
	public int capacity() {
		return values.length;
	}

	@Override
	public int bytes_per_primitive() {
		return 4;
	}

	/**
	 * @return the current backing array of this ArrayListF. Changes may be backed
	 *         up, but the ArrayLists occasionally create new arrays for space. So
	 *         use with caution!
	 */
	public float[] getArray() {
		return values;
	}

	private FloatBuffer last;

	public FloatBuffer getFloatBuffer() {
		if (last == null || last.array() != values) {
			last = FloatBuffer.wrap(values, 0, Math.max(0, size - 1));
		} else if (last.limit() != Math.max(0, size - 1)) {
			last.limit(Math.max(0, size - 1));
		}
		return last;
	}

	/**
	 * like {@link ArrayListFloat#copyTo(ArrayListFloat)} but uses
	 * {@link System#arraycopy(Object, int, Object, int, int)}. Gives some speed
	 * bonus for big lists
	 */
	public ArrayListFloat copyToNative(ArrayListFloat a) {
		a.clear();
		return addToNative(a);
	}

	/**
	 * @return like [{@link ArrayListFloat#copyToNative(ArrayListFloat)} but doesn't
	 *         clear the list before copying
	 */
	public ArrayListFloat addToNative(ArrayListFloat a) {
		a.ensureCapacity(size);
		System.arraycopy(values, 0, a.values, a.size, a.size + size);
		a.size += size;
		return a;
	}

	public ArrayListFloat copyTo(ArrayListFloat a) {
		a.clear();
		a.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			a.values[i] = values[i];
		}
		return a;
	}

	public int indexOf(float f) {
		for (int i = 0; i < size; i++)
			if (values[i] == f)
				return i;
		return -1;
	}

	public boolean contains(float f) {
		return indexOf(f) != -1;
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

	public float maxValue() {
		float max = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < size; i++)
			if (values[i] > max)
				max = values[i];
		return max;
	}

	public float minValue() {
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < size; i++)
			if (values[i] < min)
				min = values[i];
		return min;
	}

	public void quicksortUp(NotifierInterface interrupt, boolean multiThread, boolean waitForAllThreads) {
		quicksortUp(0, size - 1, interrupt, multiThread, waitForAllThreads);
	}

	public void quicksortUp(int low, int high, NotifierInterface interrupt, boolean multiThread,
			boolean waitForAllThreads) {
		if (low >= high)
			return;
		final int min_splitup_count = 100;
		float pivotValue = values[(low + high) / 2];
		int i = low, j = high;

		while (i <= j) {
			while (values[i] < pivotValue && i <= j)
				i++;
			while (values[j] > pivotValue && i <= j)
				j--;
			if (i <= j) {
				if (i < j) {
					float f = values[j];
					values[j] = values[i];
					values[i] = f;
					interrupt.somethinghappened();
				}
				i++;
				j--;
			}
		}
		final int fi = i, fj = j;
		boolean mult = multiThread && fi - 1 - low > min_splitup_count;
		Thread other = null;
		if (mult) {
			other = new Thread() {
				@Override
				public void run() {
					quicksortUp(low, fj, interrupt, true, waitForAllThreads);
				}
			};
			other.start();
		}
		boolean mult2 = multiThread && high - fi - 1 > min_splitup_count && other == null;
		if (mult2) {
			other = new Thread() {
				@Override
				public void run() {
					quicksortUp(fi, high, interrupt, true, waitForAllThreads);
				}
			};
			other.start();
		}
		if (!mult) {
			quicksortUp(low, j, interrupt, multiThread, waitForAllThreads);
		}
		if (!mult2) {
			quicksortUp(i, high, interrupt, multiThread, waitForAllThreads);
		}
		if (waitForAllThreads && other != null) {
			while (other.isAlive())
				Meth.wartn(10);
		}
	}

	public void quicksortUp() {
		quicksortUp(0, size - 1);
	}

	public void quicksortUp(int low, int high) {
		if (low >= high)
			return;
		float pivotValue = values[(low + high) / 2];
		int i = low, j = high;

		while (i <= j) {
			while (values[i] < pivotValue && i <= j)
				i++;
			while (values[j] > pivotValue && i <= j)
				j--;
			if (i <= j) {
				if (i < j) {
					float f = values[j];
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
