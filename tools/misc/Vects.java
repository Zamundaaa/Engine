package tools.misc;

import java.lang.Math;
import java.util.*;

import org.joml.*;

import tools.Meth;

/**
 * @author xaver
 *
 */
public class Vects {

	public static final Vector2f calcVect2D = new Vector2f();
	public static final Vector2f half2D = new Vector2f(0.5f);
	public static final Vector2f NULL2 = new Vector2f();

	public static final Vector3f UP30 = new Vector3f(0, 30, 0);
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	public static final Vector3f DOWN = new Vector3f(0, -1, 0);
	public static final Vector3f XP = new Vector3f(1, 0, 0);
	public static final Vector3f ZP = new Vector3f(0, 0, 1);
	public static final Vector3f NULL = new Vector3f();
	public static final Vector3f ONE = new Vector3f(1);
	public static final Vector3f WATERDOWN = new Vector3f(0, -0.1f, 0);
	public static final Vector3f calcVect = new Vector3f();
	public static final Vector3f calcVect2 = new Vector3f();
	public static final Vector3f calcVect3 = new Vector3f();

	public static final Vector4f NULL4 = new Vector4f(0, 0, 0, 0);
	public static final Vector4f calcVect4D = new Vector4f();

	public static final Matrix4f mat4 = new Matrix4f();
	public static final Matrix4f mat42 = new Matrix4f();

	public static final Quaternionf quat = new Quaternionf();
	public static final Quaternionfc identityQuat = new Quaternionf();

	/**
	 * @param eins
	 * @param zwei
	 * @return ob eins näher an obj dran ist als zwei
	 */
	public static boolean nearer(Vector3f eins, Vector3f zwei, Vector3f obj) {
		return zwei.sub(eins, Vects.calcVect).lengthSquared() < obj.sub(zwei, Vects.calcVect).lengthSquared();
	}

	public static boolean sameIntVects(Vector3f eins, Vector3f zwei, float ungenauigkeit) {
		return (eins.x >= zwei.x - ungenauigkeit && eins.x <= zwei.x + ungenauigkeit && eins.y >= zwei.y - ungenauigkeit
				&& eins.y <= zwei.y + ungenauigkeit && eins.z >= zwei.z - ungenauigkeit
				&& eins.z <= zwei.z + ungenauigkeit);
	}

	public static boolean sameIntVects(Vector3f eins, Vector3f zwei) {
		return ((int) eins.x == (int) zwei.x && (int) eins.y == (int) zwei.y && (int) eins.z == (int) zwei.z);
	}

	/**
	 * adds random values from -range to range with the seed seed (y and z have a
	 * seed offset)
	 */
	public static Vector3f addRandom(Vector3f dest, float range, long seed) {
		dest.x += Meth.randomFloat(-range, range, seed);
		dest.y += Meth.randomFloat(-range, range, seed + 431);
		dest.z += Meth.randomFloat(-range, range, seed + 7631);
		return dest;
	}

	/**
	 * @param dest der Vector, der randomised werden soll
	 * @param min  min
	 * @param max  max
	 * @return dest mit Zufallswerten (im Bereich min - max) zu x,y und z addiert
	 */
	public static Vector3f addRandom(Vector3f dest, float min, float max) {
		dest.x += Meth.randomFloat(min, max);
		dest.y += Meth.randomFloat(min, max);
		dest.z += Meth.randomFloat(min, max);
		return dest;
	}

	/**
	 * @param min min
	 * @param max max
	 * @return erstellt einen Vector mit zufälligen xyz Werten zwischen min und max
	 */
	public static Vector3f randomVector3f(Vector3f min, Vector3f max) {
		return new Vector3f(Meth.randomFloat(min.x, max.x), Meth.randomFloat(min.y, max.y),
				Meth.randomFloat(min.z, max.z));
	}

	public static Vector3f randomVector3f(float minx, float maxx, float miny, float maxy, float minz, float maxz) {
		return randomVector3f(minx, maxx, miny, maxy, minz, maxz, new Vector3f());
	}

	public static Vector3f randomVector3f(float minx, float maxx, float miny, float maxy, float minz, float maxz,
			Vector3f dest) {
		return dest.set(Meth.randomFloat(minx, maxx), Meth.randomFloat(miny, maxy), Meth.randomFloat(minz, maxz));
	}

	public static Vector3f randomVector3f(float d) {
		return randomVector3f(-d, d, -d, d, -d, d);
	}

	public static Vector4f randomVector4f(float min, float max) {
		return addRandom(new Vector4f().set(0), min, max);
	}

	public static Vector4f addRandom(Vector4f dest, float min, float max) {
		return dest.add(Meth.randomFloat(min, max), Meth.randomFloat(min, max), Meth.randomFloat(min, max),
				Meth.randomFloat(min, max));
	}

	public static Vector3f randomVector3f(float d, Vector3f dest) {
		return randomVector3f(-d, d, -d, d, -d, d, dest);
	}

	/**
	 * @param one
	 * @param two
	 * @param blendFactor
	 * @return blends between the one and two using the formula one*(1-blendFactor)
	 *         + two*blendFactor and puts the result in a new Vector3f
	 */
	public static Vector3f blend(Vector3f one, Vector3f two, float blendFactor) {
		Vector3f ret = new Vector3f();
		ret.x = (one.x * (1 - blendFactor)) + (two.x * blendFactor);
		ret.y = (one.y * (1 - blendFactor)) + (two.y * blendFactor);
		ret.z = (one.z * (1 - blendFactor)) + (two.z * blendFactor);
		return ret;
	}

	/**
	 * @param one
	 * @param two
	 * @param dest        if null, created new!
	 * @param blendFactor
	 * @return blends between the one and two using the formula one*(1-blendFactor)
	 *         + two*blendFactor and puts the result in dest
	 */
	public static Vector3f blend(Vector3f one, Vector3f two, Vector3f dest, float blendFactor) {
		if (dest == null) {
			dest = new Vector3f();
		}
		dest.x = (one.x * (1 - blendFactor)) + (two.x * blendFactor);
		dest.y = (one.y * (1 - blendFactor)) + (two.y * blendFactor);
		dest.z = (one.z * (1 - blendFactor)) + (two.z * blendFactor);
		return dest;
	}

	public static Vector3f setCalcVect(Vector3f vect) {
		calcVect.x = vect.x;
		calcVect.y = vect.y;
		calcVect.z = vect.z;
		return calcVect;
	}

	public static Vector3f setCalcVect2(Vector3f vect) {
		calcVect2.x = vect.x;
		calcVect2.y = vect.y;
		calcVect2.z = vect.z;
		return calcVect2;
	}

	public static Vector3f setCalcVect(float x, float y, float z) {
		calcVect.x = x;
		calcVect.y = y;
		calcVect.z = z;
		return calcVect;
	}

	public static Vector3f setCalcVect2(float x, float y, float z) {
		calcVect2.x = x;
		calcVect2.y = y;
		calcVect2.z = z;
		return calcVect2;
	}

	/**
	 * @param position
	 * @param position2
	 * @param i
	 * @return if position has a distance <= d to position2
	 */
	public static boolean inRange(Vector3f position, Vector3f position2, float d) {
		float x = (position.x - position2.x);
		float y = position.y - position2.y;
		float z = position.z - position2.z;
		return (x * x + y * y + z * z) <= d * d;
	}

	public static Vector2f scaleToLength(Vector2f vect, float desired) {
		float l = desired / vect.length();
		vect.x *= l;
		vect.y *= l;
		return vect;
	}

	public static Vector3f add(Vector3f vect, float f) {
		vect.x += f;
		vect.y += f;
		vect.z += f;
		return vect;
	}

	public static Vector3f add(Vector3f vect, float f, Vector3f dest) {
		dest.x = vect.x + f;
		dest.y = vect.y + f;
		dest.z = vect.z + f;
		return dest;
	}

	public static boolean isIntVect(Vector3f pos, int x, int y, int z) {
		return (int) Math.floor(pos.x) == x && (int) Math.floor(pos.y) == y && (int) Math.floor(pos.z) == z;
	}

	public static float xydistSq(Vector3f position, Vector3f position2) {
		float dx = position.x - position2.x;
		float dz = position.z - position2.z;
		return dx * dx + dz * dz;
	}

	public static Vector3f setCalcVect(float f) {
		return setCalcVect(f, f, f);
	}

	public static Vector3f pointOnRay(Vector3f vect, Vector3f ray, float dist, Vector3f dest) {
		return dest.set(vect.x + ray.x * dist, vect.y + ray.y * dist, vect.z + ray.z * dist);
	}

	public static Vector3f addRandom(Vector3f dest, float minx, float maxx, float miny, float maxy, float minz,
			float maxz) {
		return dest.add(Meth.randomFloat(minx, maxx), Meth.randomFloat(miny, maxy), Meth.randomFloat(minz, maxz));
	}

	public static Vector3f addRandom(Vector3f dest, float f) {
		return addRandom(dest, -f, f, -f, f, -f, f);
	}

	public static void floor(Vector3f v) {
		v.x = (float) Math.floor(v.x);
		v.y = (float) Math.floor(v.y);
		v.z = (float) Math.floor(v.z);
	}

	// private static final ArrayDeque<Vector4i> i4 = new ArrayDeque<>();
	// private static final ArrayDeque<Vector4i> i4Water = new ArrayDeque<>();
	private static final Map<String, ArrayDeque<Vector4i>> i4s = new HashMap<String, ArrayDeque<Vector4i>>();

	public static Vector4i getV4i(int x, int y, int z, int w) {
		ArrayDeque<Vector4i> threadQueue = i4s.get(Thread.currentThread().getName());
		// if (Thread.currentThread() == WaterUpdater.updater) {
		// threadQueue = i4Water;
		// } else {
		// threadQueue = i4;
		// }
		if (threadQueue == null || threadQueue.isEmpty()) {
			return new Vector4i(x, y, z, w);
		} else {
			return threadQueue.pop().set(x, y, z, w);
		}
	}

	public static void addV4i(Vector4i v4i) {
		ArrayDeque<Vector4i> v = i4s.get(Thread.currentThread().getName());
		if (v == null) {
			v = new ArrayDeque<Vector4i>();
			i4s.put(Thread.currentThread().getName(), v);
		}
		if (v.size() > 250) {
			return;
		}
		v.add(v4i);
	}

	private static final Map<Thread, Vector3f> calcVects = new HashMap<>();
	private static final Map<Thread, Vector3f> calcVects2 = new HashMap<>();

	/**
	 * @return a thread safe Vector3f. Always the same one per thread!
	 */
	public static Vector3f calcVect() {
		Vector3f ret = calcVects.get(Thread.currentThread());
		if (ret == null)
			calcVects.put(Thread.currentThread(), ret = new Vector3f());
		return ret;
	}

	/**
	 * @return a thread safe Vector3f. Always the same one per thread!
	 */
	public static Vector3f calcVect2() {
		Vector3f ret = calcVects2.get(Thread.currentThread());
		if (ret == null)
			calcVects2.put(Thread.currentThread(), ret = new Vector3f());
		return ret;
	}

}
