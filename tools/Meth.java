package tools;

import java.awt.Color;
import java.lang.Math;
import java.util.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.joml.*;

public class Meth {

	public static final double Gstar = 0.000_000_000_066738f;
	public static final float PI = (float) Math.PI, PI2 = PI / 2;
	public static final float angToRad = 2 * PI / 360;
	public static final float radToAng = 1 / angToRad;

	public static float getDistance(Vector3f one, Vector3f two) {
		return one.distance(two);
	}

	public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
		return sqrt(getDistanceSquared(x1, y1, z1, x2, y2, z2));
	}

	public static float getDistanceSquared(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dz = z2 - z1;
		return dx * dx + dy * dy + dz * dz;
	}

	public static float getDistance(float x1, float y1, float x2, float y2) {
		return sqrt(getDistanceSquared(x1, y1, x2, y2));
	}

	public static float getDistanceSquared(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy;
	}

	public static float converge(float start, float change, float aim) {
		float value = start;
		if (value < aim) {
			value += Math.abs(change);
			if (value > aim) {
				value = aim;
			}
		} else if (value > aim) {
			value -= Math.abs(change);
			if (value < aim) {
				value = aim;
			}
		}
		return value;
	}

	public static Vector3f converge(Vector3f v, float aimx, float aimy, float aimz, float change) {
		v.x = converge(v.x, change, aimx);
		v.y = converge(v.y, change, aimy);
		v.z = converge(v.z, change, aimz);
		return v;
	}

	public static Color randomColor() {
		return new Color(randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));
	}

	public static float pow(float value, int e) {
		if (e == 0) {
			return 1;
		} else {
			return pow(value, e - 1) * value;
		}
	}

	public static int pow(int value, int e) {
		if (e == 0) {
			return 1;
		} else {
			return pow(value, e - 1) * value;
		}
	}

	/**
	 * @param f
	 * @return gibt das Vorzeichen des floats zurück (-1 / +1) Wenn f 0 ist, 0
	 */
	public static int vorzeichen(float f) {
		return (int) Math.signum(f);
	}

	public static String giveCoords(String an, float x, float y, float z) {
		an = " " + an;
		return an + "x: \t" + x + an + "y: \t" + y + an + "z: \t" + z;
	}

	/**
	 * @return returns System.currentTimeMillis
	 */
	public static long systemTime() {
		return System.currentTimeMillis();
	}

	/**
	 * @param millis Anzahl an Millisekunden, die der Thread warten soll
	 */
	public static void wartn(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(AppFolder.log);
		}
	}

	public static void wartn(long millis, int nanos) {
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
			e.printStackTrace(AppFolder.log);
		}
	}

	/**
	 * @param dest
	 * @param l    die Länge, zu der der Vector gestaucht oder gestreckt werden soll
	 * @return dest mit der Länge l
	 */
	public static Vector3f scaleToLength(Vector3f dest, float l) {
		float length = dest.length();
		float fact = l / length;
		dest.x *= fact;
		dest.y *= fact;
		dest.z *= fact;
		return dest;
	}

	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public static float lengthSquared(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	/**
	 * @param f
	 * @param min min
	 * @param max max
	 * @return max, wenn f größer ist als max und min, wenn f kleiner ist als min.
	 *         Ansonsten f
	 */
	public static int clamp(int f, int min, int max) {
		int ret = Math.min(f, max);
		ret = Math.max(ret, min);
		return ret;
	}

	/**
	 * @param f
	 * @param min min
	 * @param max max
	 * @return max, wenn f größer ist als max und min, wenn f kleiner ist als min.
	 *         Ansonsten f
	 */
	public static float clamp(float f, float min, float max) {
		if (f > max) {
			return max;
		} else if (f < min) {
			return min;
		} else {
			return f;
		}
	}

	/**
	 * @param f
	 * @return (int) (f + 0.5f)
	 */
	public static int round(float f) {
		return (int) (f + 0.5f);
	}

	/**
	 * @param f
	 * @return den mit Math.ceil gerundeten Wert von f als Integer
	 */
	public static int toInt(float f) {
		return (int) Math.floor(f);
	}

	private static Map<Thread, Random> randoms = new HashMap<Thread, Random>();

	public static int randomInt(int min, int max, long seed) {
		Random r = randoms.get(Thread.currentThread());
		if (r == null) {
			r = new Random();
			randoms.put(Thread.currentThread(), r);
		}
		r.setSeed(seed);
		int ret = r.nextInt(max - min + 1) + min;
		r.setSeed(System.currentTimeMillis() % 215378);
		return ret;
	}

	public static int randomInt(int range) {
		return randomInt(-range, range);
	}

	public static int randomInt(Random r, int min, int max) {
		return r.nextInt(max - min + 1) + min;
	}

	public static int randomInt(Random r, int range) {
		return randomInt(r, -range, range);
	}

	public static float randomFloat(float min, float max, long seed) {
		Random r = randoms.get(Thread.currentThread());
		if (r == null) {
			r = new Random();
			randoms.put(Thread.currentThread(), r);
		}
		return randomFloat(r, min, max, seed);
	}

	public static float randomFloat(Random r, float min, float max, long seed) {
		r.setSeed(seed);
		float ret = r.nextFloat() * (max - min) + min;
		r.setSeed(System.currentTimeMillis() % 215378);
		return ret;
	}

	public static float randomFloat(Random r, float min, float max) {
		return r.nextFloat() * (max - min) + min;
	}

	public static float randomFloat(Random r, float range) {
		return randomFloat(r, -range, range);
	}

	//
	// /**
	// * @param chance
	// * float zwischen 0 und 1
	// * @return gibt mit einer Wahrscheinlichkeit von chance true aus
	// */
	// public static boolean doChance(float chance, long seed) {
	// boolean ret = false;
	// ThreadLocalRandom.current().setSeed(seed);
	// if (ThreadLocalRandom.current().nextFloat() > (1 - chance)) {
	// ret = true;
	// }
	// ThreadLocalRandom.current() = new Random();
	// return ret;
	// }

	/**
	 * @param min min
	 * @param max max
	 * @return Zufallszahl im Bereich von min (inclusive) - max (inclusive)
	 */
	public static int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
	}

	/**
	 * @param min min
	 * @param max max
	 * @return Zufallszahl im Bereich von min - max
	 */
	public static float randomFloat(float min, float max) {
		return ThreadLocalRandom.current().nextFloat() * (max - min) + min;
	}

	/**
	 * @return Zufallszahl im Bereich von -range bis range
	 */
	public static float randomFloat(float range) {
		return randomFloat(-range, range);
	}

	/**
	 * @param chance float zwischen 0 und 1
	 * @return gibt mit einer Wahrscheinlichkeit von chance true aus
	 */
	public static boolean doChance(float chance) {
		return ThreadLocalRandom.current().nextFloat() > (1 - chance);
	}

	public static float barryCentric(float p1x, float p1y, float p1z, float p2x, float p2y, float p2z, float p3x,
			float p3y, float p3z, float x, float y) {
		float det = (p2z - p3z) * (p1x - p3x) + (p3x - p2x) * (p1z - p3z);
		float l1 = ((p2z - p3z) * (x - p3x) + (p3x - p2x) * (y - p3z)) / det;
		float l2 = ((p3z - p1z) * (x - p3x) + (p1x - p3x) * (y - p3z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1y + l2 * p2y + l3 * p3y;
	}

	// public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3,
	// Vector2f pos) {
	// float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z -
	// p3.z);
	// float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y -
	// p3.z)) / det;
	// float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y -
	// p3.z)) / det;
	// float l3 = 1.0f - l1 - l2;
	// return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	// }

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale,
			Matrix4f matrix) {
		if (matrix == null) {
			matrix = new Matrix4f();
		}
		matrix.identity();
		matrix.translate(translation);
		matrix.rotateX(rx);
		matrix.rotateY(ry);
		matrix.rotateZ(rz);
		matrix.scale(scale);
		return matrix;
	}

	// private static Vector3f translationmarker = new Vector3f();

	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz,
			float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(x, y, z);
		matrix.rotateX(rx);
		matrix.rotateY(ry);
		matrix.rotateZ(rz);
		matrix.scale(scale);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz,
			float scale, Matrix4f matrix) {
		matrix.identity();
		matrix.translate(x, y, z);
		matrix.rotateX(rx);
		matrix.rotateY(ry);
		matrix.rotateZ(rz);
		matrix.scale(scale);
		return matrix;
	}

	private static Matrix4f transformationMatrix2D = new Matrix4f();

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		transformationMatrix2D.identity();
		transformationMatrix2D.translate(translation.x, translation.y, 0);
		transformationMatrix2D.scale(scale.x, scale.y, 1);
		// transformationMatrix2D.translation(translation.x, translation.y, 0);
		// transformationMatrix2D.scaling(scale.x, scale.y, 1);
		return transformationMatrix2D;
	}

	public static Matrix4f createTransformationMatrix(float x, float y, float scalex, float scaley) {
		transformationMatrix2D.identity();
		transformationMatrix2D.translate(x, y, 0);
		transformationMatrix2D.scale(scalex, scaley, 1);
		return transformationMatrix2D;
	}

	public static float blend(float one, float two, float blendFactor) {
		return (1 - blendFactor) * one + blendFactor * two;
	}

	/**
	 * @param one
	 * @param two
	 * @param genauigkeit in Nachkommastellen
	 * @return
	 */
	public static boolean theSame(float one, float two, int genauigkeit) {
		return ((int) (one * genauigkeit * 10) == (int) (two * genauigkeit * 10));
	}

	public static boolean theSame(float x, float y, float dist) {
		return abs(x - y) <= dist;
	}

	/**
	 * @param f
	 * @param i Nachkommastellen
	 */
	public static String floatToString(float f, int i) {
		// int p = (int) pow(10, i);
		// f *= p;
		// int v = (int) f;
		// return "" + (v / p);
		return String.format("%." + i + "f", f);
	}

	public static float si(float f) {
		return sin(f) / f;
	}

	public static float sin(float f) {
		return (float) Math.sin(f);
	}

	public static float cos(float f) {
		return (float) Math.cos(f);
	}

	public static float tan(float f) {
		return (float) Math.tan(f);
	}

	public static float asin(float f) {
		return (float) Math.asin(f);
	}

	public static float acos(float f) {
		return (float) Math.acos(f);
	}

	public static float atan(float m) {
		return (float) Math.atan(m);
	}

	public static float sinDeg(float f) {
		return sin(f * angToRad);
	}

	public static float cosDeg(float f) {
		return cos(f * angToRad);
	}

	public static float asinDeg(float f) {
		return asin(f * angToRad);
	}

	public static float acosDeg(float f) {
		return acos(f * angToRad);
	}

	public static float atanDeg(float f) {
		return atan(f * angToRad);
	}

	public static float abs(float f) {
		if (f < 0) {
			return -f;
		} else {
			return f;
		}
	}

	/**
	 * Returns the largest (closest to positive infinity) double value that is less
	 * than or equal to the argument and is equal to a mathematical integer. Special
	 * cases: If the argument value is already equal to a mathematical integer, then
	 * the result is the same as the argument. If the argument is NaN or an infinity
	 * or positive zero or negative zero, then the result is the same as the
	 * argument.
	 * 
	 * @param a - a value.
	 * @return the largest (closest to positive infinity) floating-point value that
	 *         less than or equal to the argument and is equal to a mathematical
	 *         integer.
	 */
	public static int floor(float a) {
		return (int) Math.floor(a);
	}

	public static int ceil(float x) {
		return (int) Math.ceil(x);
	}

	/**
	 * @return the remainder of the division of x; ALWAYS positive
	 */
	public static int modulo(int x, int mod) {
		if (x < 0) {
			return (-x) % mod;
		} else {
			return x % mod;
		}
	}

	// AI ZEUG
	public static float sigmoid(float x) {
		return 1f / (1 + exp(x));
	}

	public static float ablSigmoid(float x) {
		float sigm = sigmoid(x);
		return sigm * (1 - sigm);
	}

	public static float exp(float x) {
		return x;
	}
	// AI ZEUG ENDE

	public static float sqrt(float x) {
		return (float) Math.sqrt(x);
	}

	public static int sign(float x) {
		return (int) Math.signum(x);
	}

	private static long stoppedTime;

	public static long stopTime() {
		return stoppedTime = systemTime();
	}

	public static long stoppedTime() {
		return systemTime() - stoppedTime;
	}

	public static float tryToParseFloat(String s, float x) {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return x;
		}
	}

	public static final Matrix4f viewMatrix = new Matrix4f();

	public static Matrix4f createViewMatrix(Matrix4f viewMatrix, float pitch, float rotY, float roll, float x, float y,
			float z) {
		viewMatrix.identity();
		viewMatrix.rotateX(pitch);// , Vects.XP
		viewMatrix.rotateY(rotY);// , Vects.UP
		viewMatrix.rotateZ(roll);// , Vects.ZP
		viewMatrix.translate(-x, -y, -z);
		return viewMatrix;
	}

	public static int setBit(int bitmask, int index, boolean one) {
		if (one)
			return bitmask | (1 << index);
		else
			return bitmask & ~(1 << index);
	}

	public static boolean readBit(int bitmask, int index) {
		return ((bitmask >> index) & 1) == 1;
	}

	public static byte setBit(byte bitmask, int index, boolean one) {
		if (one)
			return (byte) (bitmask | (1 << index));
		else
			return (byte) (bitmask & ~(1 << index));
	}

	public static boolean readBit(byte bitmask, int index) {
		return ((bitmask >> index) & 1) == 1;
	}

	public static int log2int(int bits) {
		if (bits == 0)
			return 0; // or throw exception
		return 31 - Integer.numberOfLeadingZeros(bits);
	}

	public static String coordinatesToString(int x, int y, int z) {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	public static String coordinatesToString(float x, float y, float z) {
		return "[" + ((int) (x * 10)) * 0.1f + ", " + ((int) (y * 10)) * 0.1f + ", " + ((int) (z * 10)) * 0.1f + "]";
	}

	public static String coordinatesToString(Vector3f v) {
		return String.format("[%.3f; %.3f; %.3f]", v.x, v.y, v.z);
	}

	public static <T> T getRandom(List<T> list) {
		if (list.size() < 1)
			return null;
		else if (list.size() == 1)
			return list.get(0);
		else
			return list.get(randomInt(0, list.size() - 1));
	}

	public static int pickRandom(int i1, int i2) {
		return doChance(0.5f) ? i1 : i2;
	}

	public static int pickRandom(int i1, int i2, int i3) {
		int i = randomInt(0, 2);
		if (i == 0)
			return i1;
		if (i == 1)
			return i2;
		return i3;
	}

	public static int pickRandom(int... values) {
		return values[randomInt(0, values.length - 1)];
	}

	/**
	 * @param angle in radians
	 */
	public static float xOnCircle(float middlex, float radius, float angle) {
		return middlex + radius * cos(angle);
	}

	/**
	 * @param angle in radians
	 */
	public static float yOnCircle(float middley, float radius, float angle) {
		return middley + radius * sin(angle);
	}

	/**
	 * @return a randomized version of toHash. For every same input it is guaranteed
	 *         to give the same output
	 */
	public static String hashString(String toHash, long seed) {
		Random r = new Random(seed);
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < toHash.length(); i++) {
			ret.append(toHash.charAt(i) + r.nextInt(11) - 5);
			if (r.nextBoolean())
				ret.append((char) r.nextInt(Character.MAX_VALUE));
		}
		return ret.toString();
	}

}
