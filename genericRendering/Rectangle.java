package genericRendering;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import generic.Thing;
import loaders.Loader;
import models.material.Material;
import openGlResources.buffers.VAO;

public class Rectangle extends Thing {

	public Rectangle(Vector3f position, Material m) {
		super(getVAO(), m, position, 1);
	}

	public Rectangle(Vector3f position, float width, float height, Material m) {
		super(getVAO(), m, position, 1);
		scale.x = width * 0.5f;
		scale.y = height * 0.5f;
	}

	public Rectangle(float x, float y, float width, float height, Material mat) {
		this(new Vector3f(x, y, 0), width, height, mat);
	}

	public Rectangle(float x, float y, float z, float width, float height, Material mat) {
		this(new Vector3f(x, y, z), width, height, mat);
	}

	public Rectangle(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	@Override
	public float scaledWidth() {
		return scale.x * 2;
	}

	@Override
	public float scaledHeight() {
		return scale.y * 2;
	}

	@Override
	public float scaledDepth() {
		return 0.01f;
	}

	// static stuff

	private static Map<Thread, VAO> threadMap = new HashMap<>();

	public static VAO getVAO() {
		VAO quad = threadMap.get(Thread.currentThread());
		if (quad == null) {
			final float one = 0.99f, zero = 0.01f;
			threadMap.put(Thread.currentThread(),
					quad = Loader.loadToVAO(new int[] { 1, 0, 2, 0, 3, 2 }, new int[] { 3, 2, 3 },
							new float[] { -1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1, 0 },
							new float[] { zero, zero, one, zero, one, one, zero, one },
							new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 }));
			quad.setName("quad");
		}
		return quad;
	}

}
