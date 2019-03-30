package models.geometry;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import generic.Thing;
import loaders.Loader;
import models.material.Material;
import openGlResources.buffers.VAO;

public class Line extends Thing {

	protected Vector3f second = new Vector3f();

	/**
	 * this constructor is only for the case if you set this line in some update
	 * method or something anyway
	 */
	public Line(Material m, float thickness) {
		this(m, 0, 0, 0, 1, 1, 1, thickness);
	}

	public Line(Material m, float x, float y, float z, float x2, float y2, float z2, float thickness) {
		super(getVAO(), m, new Vector3f(x, y, z), 1);
//		super(new Vector3f(x, y, z), m);
		set(x, y, z, x2, y2, z2, thickness);
		cullFaces = false;
		fakeLight(1);
//		cullModel = false;
	}

	private static Map<Thread, VAO> threadMap = new HashMap<>();

	public static VAO getVAO() {
		VAO quad = threadMap.get(Thread.currentThread());
		if (quad == null) {
			final float one = 0.99f, zero = 0.01f;
			threadMap.put(Thread.currentThread(),
					quad = Loader.loadToVAO(new int[] { 1, 0, 2, 0, 3, 2 }, new int[] { 3, 2, 3 },
							new float[] { -1, 0, 0, -1, 2, 0, 2 - 1, 2, 0, 2 - 1, 0, 0 },
							new float[] { zero, zero, one, zero, one, one, zero, one },
							new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 }));
			quad.middle().set(0, 1, 0);
			quad.size().set(1, 1, 0.01f);
			quad.setName("quad2D");
		}
		return quad;
	}

	public void set(float x, float y, float z, float x2, float y2, float z2, float thickness) {
		position.set(x, y, z);
		second.set(x2, y2, z2);
		float l = position.distance(second) * 0.5f;
		scale.set(thickness * 0.5f, l, l);
		float dx = x2 - x, dy = y2 - y, dz = z2 - z;
		if (dx == 0)
			dx = 0.000001f;
		rotation.identity().rotateTo(0, 1, 0, dx, dy, dz);
	}

	public void set(float x, float y, float z, float x2, float y2, float z2) {
		set(x, y, z, x2, y2, z2, thickness());
	}

	public float thickness() {
		return scale.x * 2;
	}

	public Vector3f second() {
		return second;
	}

}
