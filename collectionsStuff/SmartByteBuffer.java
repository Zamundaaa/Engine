package collectionsStuff;

import java.util.ArrayDeque;

import tools.AppFolder;

/**
 * A buffer that can be used to efficiently pack information into bytes and
 * process it, too
 * 
 * @author xaver
 *
 */
public class SmartByteBuffer extends ArrayListB {

	private static final ArrayDeque<SmartByteBuffer> bufferBuffer = new ArrayDeque<SmartByteBuffer>();
	private static int bufferedBytes = 0;
	/**
	 * is allowed to buffer a million bytes (<1 MB)
	 */
	public static int max_buffered_bytes = 1_000_000;

	/**
	 * @return a SmartByteBuffer from the internal buffer and ensures a capacity of
	 *         a minimum of startCapacity bytes if present, or a new one with the
	 *         specified initial capacity
	 */
	public static SmartByteBuffer getBuffer(int startCapacity) {
		SmartByteBuffer ret = null;
		if (!bufferBuffer.isEmpty()) {
			synchronized (bufferBuffer) {
				if (!bufferBuffer.isEmpty()) {
					ret = bufferBuffer.pop();
					bufferedBytes -= ret.size();
				}
			}
		}
		if (ret == null) {
			ret = new SmartByteBuffer(startCapacity);
		} else {
			ret.ensureCapacity(startCapacity);
			ret.clear();
		}
		return ret;
	}

	/**
	 * @return a SmartByteBuffer from the internal buffer and ensures a capacity of
	 *         a minimum of {@link PrimitiveList#STARTCAPACITY}
	 */
	public static SmartByteBuffer getBuffer() {
		return getBuffer(STARTCAPACITY);
	}

	public void free() {
		if (bufferedBytes + size() < max_buffered_bytes) {
			synchronized (bufferBuffer) {
				bufferBuffer.add(this);
			}
		}
	}

	private int pos = 0;

	public SmartByteBuffer(int size) {
		super(size);
	}

	public SmartByteBuffer() {
		super();
	}

	public SmartByteBuffer(byte[] data) {
		super(data);
	}

	public void addBool(boolean b) {
		add((byte) (b ? 1 : 0));
	}

	public boolean readBool() {
		return read() == 1;
	}

	/**
	 * reads a byte at the current position and increments the position
	 */
	public byte read() {
		if (pos < size)
			return values[pos++];
		else
			throw new IndexOutOfBoundsException("pos (" + pos + ") is >= size (" + size + ") !");
	}

	public void addChar(char c) {
		add((byte) ((c >> 8) & 0x00FF));
		add((byte) (c & 0x00FF));
	}

	/**
	 * @return the char composed of the first 8 bits of the byte at position i and
	 *         the last 8 bits of the byte at position i+1
	 */
	public char getChar(int i) {
		return (char) (((values[i++] & 0xFF) << 8) | (values[i] & 0xFF));
	}

	/**
	 * reads a char at the position and increments the position by 2
	 */
	public char readChar() {
		int p = pos;
		if (pos + 1 < size) {
			return (char) (((values[pos++] & 0xFF) << 8) | (values[pos++] & 0xFF));
		} else {
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a char! Index: " + pos + " size: " + size);
		}
	}

	public SmartByteBuffer addShort(short s) {
		add((byte) ((s >> 8) & 0xFF));
		add((byte) (s & 0xFF));
		return this;
	}

	/**
	 * @return the short composed of the first 8 bits of the byte at position i and
	 *         the last 8 bits of the byte at position i+1
	 */
	public short getShort(int i) {
		return (short) (((values[i++] & 0xFF) << 8) | (values[i] & 0xFF));
	}

	// you could finally remove this crap. Only drains (even if just marginal)
	// performance
	public static final int NEW = 1, OLD = 0;

	private int shortByteOrder = NEW;

	public void setToOldShortByteOrder() {
		shortByteOrder = OLD;
	}

	/**
	 * reads a short at the position and increments the position by 2
	 */
	public short readShort() {
		int p = pos;
		if (pos + 1 < size) {
			if (shortByteOrder == OLD)
				return (short) ((values[pos++] & 0xFF) | ((values[pos++] & 0xFF) << 8));
			else
				return (short) (((values[pos++] & 0xFF) << 8) | (values[pos++] & 0xFF));
		} else {
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a short! Position: " + pos + " size: " + size);
		}
	}

	public void addFloat(float f) {
		addInt(Float.floatToRawIntBits(f));
	}

	public float getFloat(int i) {
		return Float.intBitsToFloat(getInt(i));
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public void addDouble(double d) {
		addLong(Double.doubleToRawLongBits(d));
	}

	public double getDouble(int i) {
		return Double.longBitsToDouble(getLong(i));
	}

	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public void addInt(int i) {
		add((byte) ((i >> 24) & 0xFF));
		add((byte) ((i >> 16) & 0xFF));
		add((byte) ((i >> 8) & 0xFF));
		add((byte) (i & 0xFF));
	}

	public void setInt(int pos, int i) {
		ensureCapacity(pos + 4);
		set(pos++, (byte) ((i >> 24) & 0xFF));
		set(pos++, (byte) ((i >> 16) & 0xFF));
		set(pos++, (byte) ((i >> 8) & 0xFF));
		set(pos++, (byte) (i & 0xFF));
	}

	public int getInt(int i) {
		if (i + 3 < size) {
			return (((values[i++] & 0xFF) << 24) | ((values[i++] & 0xFF) << 16) | ((values[i++] & 0xFF) << 8)
					| (values[i] & 0xFF));
		} else {
			throw new IndexOutOfBoundsException("Failed to read an int! Index: " + i + " size: " + size);
		}
	}

	public int readInt() {
		int p = pos;
		if (pos + 3 < size) {
			return (((values[pos++] & 0xFF) << 24) | ((values[pos++] & 0xFF) << 16) | ((values[pos++] & 0xFF) << 8)
					| (values[pos++] & 0xFF));
		} else {
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read an int! Index: " + p + " size: " + size);
		}
	}

	public void addLong(long l) {
		add((byte) ((l >> 56) & 0x00FF));
		add((byte) ((l >> 48) & 0x00FF));
		add((byte) ((l >> 40) & 0x00FF));
		add((byte) ((l >> 32) & 0x00FF));

		add((byte) ((l >> 24) & 0x00FF));
		add((byte) ((l >> 16) & 0x00FF));
		add((byte) ((l >> 8) & 0x00FF));
		add((byte) (l & 0x00FF));
	}

	public long getLong(int i) {
		return ((values[i++] & 0xFF) << 56) | ((values[i++] & 0xFF) << 48) | ((values[i++] & 0xFF) << 40)
				| ((values[i++] & 0xFF) << 32) | ((values[i++] & 0xFF) << 24) | ((values[i++] & 0xFF) << 16)
				| ((values[i++] & 0xFF) << 8) | (values[i] & 0xFF);
	}

	public long readLong() {
		int p = pos;
		if (pos + 7 < size) {
			return ((long) (values[pos++] & 0xFF) << 56) | ((long) (values[pos++] & 0xFF) << 48)
					| ((long) (values[pos++] & 0xFF) << 40) | ((long) (values[pos++] & 0xFF) << 32)
					| ((long) (values[pos++] & 0xFF) << 24) | ((long) (values[pos++] & 0xFF) << 16)
					| ((long) (values[pos++] & 0xFF) << 8) | ((long) values[pos++] & 0xFF);
		} else {
			pos = p;
			throw new IndexOutOfBoundsException("Failed to read a long! Index: " + pos + " size: " + size);
		}
	}

	/**
	 * the maximum string length with this method is 2^16-1 or 65535. Anything above
	 * this is not considered worth the extra two bytes. Split up your String if
	 * it's too long or write a method addLongString(String) which uses an Integer
	 * as length parameter
	 */
	public SmartByteBuffer addString(String s) {
		addShort((short) (s.length() + Short.MIN_VALUE));
		for (int i = 0; i < s.length(); i++) {
			addChar(s.charAt(i));
		}
		return this;
	}

	/**
	 * reads a short, this is the length of the returned string. This means the
	 * maximum string length with this method is 2^16-1 or 65535. This string is
	 * then read by a consecutive array of readChar() and returned
	 */
	public String readString() {
		int l = readShort() - Short.MIN_VALUE;
		StringBuilder ret = new StringBuilder(l);
		try {
			for (int i = 0; i < l; i++) {
				ret.append(readChar());
			}
		} catch (IndexOutOfBoundsException e) {
			AppFolder.log.println("failed to read a string of length " + l);
			throw e;
		}
		return ret.toString();
	}

	public int position() {
		return pos;
	}

	public void resetPos() {
		pos = 0;
	}

	public int remaining() {
		int ret = size - pos;
		return ret < 0 ? 0 : ret;
	}

	public void setPosition(int pos) {
		this.pos = pos;
	}

	@Override
	public void clear() {
		super.clear();
		resetPos();
	}

	/**
	 * sets size to s, or if s is bigger than this Buffers internal array's length,
	 * to the internal array's length; also caps this buffer's position to size-1
	 */
	public void setSize(int s) {
		this.size = s;
		if (this.size > values.length) {
			this.size = values.length;
		}
		if (pos > size) {
			pos = size;
		}
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("SmartByteBuffer with pos ");
		ret.append(pos);
		ret.append(" and a size of ");
		ret.append(size);
		ret.append("; values: ");
		ret.append(super.toString());
		return ret.toString();
	}

	public SmartByteBuffer addAll(SmartByteBuffer s, int startIndex, int count) {
		return addAll(s.values, startIndex, count);
	}

	/**
	 * bytes from b[startIndex](inclusive) to b[startIndex+count](exclusive) will be
	 * added
	 */
	public SmartByteBuffer addAll(byte[] b, int startIndex, int count) {
		ensureCapacity(size + count);
		for (int i = startIndex; i < startIndex + count; i++) {
			values[size++] = b[i];
		}
		return this;
	}

	public SmartByteBuffer addAll(SmartByteBuffer s) {
		return addAll(s, 0, s.size);
	}

	public void addPos(int i) {
		pos += i;
	}

	public int remainingCapacity() {
		return capacity() - size;
	}

	public boolean contentMatches(SmartByteBuffer s) {
		if (size != s.size) {
			return false;
		}
		for (int i = 0; i < size; i++)
			if (values[i] != s.values[i])
				return false;
		return true;
	}

	public int posForCopy() {
		if (pos == 0)
			return 0;
		else
			return pos - 1;
	}

	/*
	 * clones this SmartByteBuffer efficiently. This means it creates a new
	 * SmartByteBuffer with all elements up to size, but the internal array length
	 * and elements after index size-1 will differ. For an exact clone use {@link
	 * cloneExact}
	 */
	@Override
	public SmartByteBuffer clone() {
		return new SmartByteBuffer(size).addAll(this);
	}

	public SmartByteBuffer clone(int extrasize) {
		return new SmartByteBuffer(size + extrasize).addAll(this);
	}

	public SmartByteBuffer cloneExact() {
		SmartByteBuffer clone = new SmartByteBuffer(size);
		clone.addAll(values);
		clone.setSize(size);
		return clone;
	}


	// /**
	// * shifts the whole list by i. If i is negative, the values under the
	// index
	// * i will be removed, if i is positive it will
	// *
	// * @param i
	// * the index/amount to be shifted by
	// */
	// public void shiftDataBy(int i) {
	// if (i > 0) {
	// ensureCapacity(size + i);
	//
	// } else {
	// i *= -1;
	// removeAll(0, i - 1);
	// }
	// }

	// public static void main(String[] args){
	// SmartByteBuffer s = new SmartByteBuffer();
	// byte b = -128;
	// for(short i = 0; i < 1000; i++){
	// if(i % 2 == 0){
	// s.addShort(i);
	// }else{
	// s.add(b);
	// }
	// }
	// for(int i = 0; i < 1000; i++){
	// if(i % 2 == 0){
	// System.out.println(s.readShort());
	// }else{
	// System.out.println(s.read());
	// }
	// }
	// }

}
