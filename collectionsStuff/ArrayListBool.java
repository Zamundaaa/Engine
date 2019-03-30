package collectionsStuff;

public class ArrayListBool extends PrimitiveList {

	private boolean[] values;

	public ArrayListBool() {
		this(STARTCAPACITY);
	}

	public ArrayListBool(int startCapacity) {
		values = new boolean[startCapacity];
	}

	public void add(boolean f) {
		ensureCapacity(size + 1);
		values[size] = f;
		size++;
	}

	public void set(int i, boolean f) {
		values[i] = f;
	}

	public boolean remove(int i) {
		if (i >= 0 && i < size) {
			boolean ret = values[i];
			size--;
			for (int I = i; I < size; I++) {
				values[I] = values[I + 1];
			}
			return ret;
		} else {
			throw new ArrayIndexOutOfBoundsException(i);
		}
	}

	public boolean get(int i) {
		return values[i];
	}

	public int size() {
		return size;
	}

	@Override
	protected void grow(int newSize) {
		boolean[] newValues = new boolean[newSize];
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

	public static String arrayToString(boolean[] arr) {
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

	public boolean[] capToArray() {
		boolean[] ret = new boolean[size];
		for (int i = 0; i < size; i++)
			ret[i] = values[i];
		return ret;
	}

	public void addAll(ArrayListBool a) {
		for (int i = 0; i < a.size; i++)
			add(a.get(i));
	}

	@Override
	public int capacity() {
		return values.length;
	}

	/*
	 * !!! THIS IS NOT ACCURATE !!! a boolean could theoretically be just one
	 * bit. The size of a boolean isn't defined!
	 */
	@Override
	public int bytes_per_primitive() {
		return 1;
	}

	/**
	 * provides an efficient way to remove many elements at once. Should be
	 * exactly as fast as a single call to {@link ArrayListBool#remove(int)}}
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

	public boolean getLast() {
		return values[size-1];
	}

	
	public void setLast(boolean b) {
		values[size-1] = b;
	}

}
