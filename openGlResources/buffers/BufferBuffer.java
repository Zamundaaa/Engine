package openGlResources.buffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

public class BufferBuffer {

	private static final Map<Thread, FloatBuffer> floaties = new HashMap<>();
	private static final Map<Thread, IntBuffer> inties = new HashMap<>();

	public static FloatBuffer getFloatBuffer(int minLength) {
		FloatBuffer ret = floaties.get(Thread.currentThread());
		if (ret == null) {
			// high init value, so less allocations are done
			floaties.put(Thread.currentThread(), ret = BufferUtils.createFloatBuffer(Math.max(1000, minLength)));
		} else if (ret.capacity() < minLength) {
			floaties.put(Thread.currentThread(), ret = BufferUtils.createFloatBuffer((int) (minLength * 1.05f)));
		}
		ret.clear();
		return ret;
	}

	public static IntBuffer getIntBuffer(int minLength) {
		IntBuffer ret = inties.get(Thread.currentThread());
		if (ret == null) {
			// high init value, so less allocations are done
			inties.put(Thread.currentThread(), ret = BufferUtils.createIntBuffer(Math.max(1000, minLength)));
		} else if (ret.capacity() < minLength) {
			inties.put(Thread.currentThread(), ret = BufferUtils.createIntBuffer((int) (minLength * 1.05f)));
		}
		ret.clear();
		return ret;
	}

}
