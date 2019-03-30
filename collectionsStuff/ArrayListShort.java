package collectionsStuff;

import java.nio.ShortBuffer;
import java.util.Arrays;

public class ArrayListShort extends PrimitiveList {

	private short[] values;

	/**
	 * creates an empty ArrayList for shorts with a capacity of
	 * {@link PrimitiveList#STARTCAPACITY}
	 */
	public ArrayListShort() {
		this(STARTCAPACITY);
	}

	/**
	 * creates an empty ArrayList for shorts with a capacity of startCapacity
	 */
	public ArrayListShort(int startCapacity) {
		values = new short[startCapacity];
	}

	public ArrayListShort(ShortBuffer buff) {
		this(buff.capacity());
		addAll(buff);
	}

	public void add(short f) {
		ensureCapacity(size + 1);
		values[size] = f;
		size++;
	}

	public void set(int i, short f) {
		values[i] = f;
	}

	/**
	 * retrieves and removes the value at index i
	 */
	public short remove(int i) {
		if (i >= 0 && i < size) {
			short ret = values[i];
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

	public short get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		short[] newValues = new short[newSize];
		for (int i = 0; i < values.length; i++) {
			newValues[i] = values[i];
		}
		values = newValues;
	}

	public void clear() {
		size = 0;
	}

	public short[] capToArray() {
		short[] ret = new short[size];
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

	public static String arrToString(short[] is) {
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

	public void addAll(ArrayListShort a) {
		for (int i = 0; i < a.size; i++)
			add(a.get(i));
	}

	public void addAll(ArrayListShort a, int add) {
		for (int i = 0; i < a.size; i++)
			add((short) (a.get(i) + add));
	}

	public void addAll(ShortBuffer buff) {
		for (int i = 0; i < buff.capacity(); i++) {
			add(buff.get(i));
		}
	}

	private short lastSearchedInt;
	private int lastFoundIndex;

	/**
	 * @return the first index with the specified value in place; If the value
	 *         isn't contained in this list, -1
	 */
	public int indexOf(short someShort) {
		if (size > 0)
			if (someShort == lastSearchedInt && lastFoundIndex > 0 && lastFoundIndex < size
					&& values[lastFoundIndex] == someShort) {
				return lastFoundIndex;
			} else {
				for (int i = 0; i < size; i++) {
					if (values[i] == someShort)
						return i;
				}
			}
		return -1;
	}

	/**
	 * @return the *current* backing array. Changes when capacity() does
	 */
	public short[] getArray() {
		return values;
	}

	@Override
	public int capacity() {
		return values.length;
	}

	@Override
	public int bytes_per_primitive() {
		return 2;
	}

	private ShortBuffer last;

	public ShortBuffer getShortBuffer() {
		if (last == null || last.array() != values) {
			last = ShortBuffer.wrap(values, 0, Math.max(0, size - 1));
		} else if (last.limit() != Math.max(0, size - 1)) {
			last.limit(Math.max(0, size - 1));
		}
		return last;
	}

	/**
	 * like {@link ArrayListInt#copyTo(ArrayListShort)} but uses
	 * {@link System#arraycopy(Object, int, Object, int, int)}. Gives some speed
	 * bonus for big lists
	 */
	public ArrayListShort copyToNative(ArrayListShort a) {
		a.ensureCapacity(size);
		System.arraycopy(values, 0, a.values, 0, size);
		a.size = size;
		return a;
	}

	public ArrayListShort copyTo(ArrayListShort a) {
		a.clear();
		a.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			a.values[i] = values[i];
		}
		a.size = size;
		return a;
	}

	public ArrayListShort copyToAndClear(ArrayListShort a) {
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
		Arrays.fill(values, (short) 0);
		clear();
	}

	/**
	 * @return if {@link #indexOf(short)} > -1
	 */
	public boolean contains(short i) {
		return indexOf(i) > -1;
	}

	public void removeValue(short value) {
		int i = indexOf(value);
		if (i != -1) {
			remove(i);
		}
	}

	public static interface ForIteration {
		void process(int index, short value);
	}

	public void iterate(ForIteration f) {
		for (int i = 0; i < size; i++) {
			f.process(i, values[i]);
		}
	}

	/**
	 * provides an efficient way to remove many elements at once. Should be
	 * exactly as fast as a single call to {@link ArrayListInt#remove(int)}}
	 * 
	 * @param startIndex
	 *            inclusive
	 * @param endIndex
	 *            inclusive
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

	public void moveTo(ArrayListShort a, int startIndex, int count) {
		for (int i = startIndex; i < count && i < size; i++) {
			a.add(values[i]);
		}
		removeAll(startIndex, Math.max(startIndex + count - 1, size - 1));
	}

}
