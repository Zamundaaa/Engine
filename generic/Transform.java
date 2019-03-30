package generic;

import java.util.Map;

import org.joml.*;

import collectionsStuff.SmartByteBuffer;

public class Transform extends Savable {

	protected Vector3f position;
	protected Quaternionf rotation;

	public Transform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public Transform() {
		position = new Vector3f();
		rotation = new Quaternionf();
	}

	public Transform(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public Vector3f position() {
		return position;
	}

	public Vector3f setPosition(Vector3f p) {
		return position.set(p);
	}

	public void setPositionRef(Vector3f v) {
		position = v;
	}

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public Quaternionf rotation() {
		return rotation;
	}

	public void setRotationRef(Quaternionf ref) {
		this.rotation = ref;
	}

	public void setRotation(Quaternionf rot) {
		this.rotation.set(rot);
	}

	public void setRotation(float x, float y, float z, float w) {
		this.rotation.set(x, y, z, w);
	}

	public Matrix4f transform(Matrix4f m) {
		return rotate(translate(m));
	}

	public Matrix4f rotate(Matrix4f m) {
		return m.rotate(rotation);
	}

	public Matrix4f translate(Matrix4f m) {
		return m.translate(position);
	}

	public Transform interpolate(Transform other, float progression, Transform dest) {
		return interpolate(this, other, progression, dest);
	}

	public static Transform interpolate(Transform a, Transform b, float progression, Transform dest) {
		if (dest == null)
			dest = new Transform();
		dest.position.set(a.position).lerp(b.position, progression);
		dest.rotation.set(a.rotation).nlerp(b.rotation, progression);
		return dest;
	}

	public static Transform interpolatePosition(Transform a, Transform b, float progression, Transform dest) {
		if (dest == null)
			dest = new Transform();
		dest.position.set(a.position.x + (b.position.x - a.position.x) * progression,
				a.position.y + (b.position.y - a.position.y) * progression,
				a.position.z + (b.position.z - a.position.z) * progression);
		return dest;
	}

	public static Matrix4f interpolatePosition(Transform a, Transform b, float progression, Matrix4f dest) {
		if (dest == null)
			dest = new Matrix4f();
		dest.translate(a.position.x + (b.position.x - a.position.x) * progression,
				a.position.y + (b.position.y - a.position.y) * progression,
				a.position.z + (b.position.z - a.position.z) * progression);
		return dest;
	}

//	public static Matrix4f interpolate(Transform a, Transform b, float progression, Matrix4f dest) {
//		if (dest == null)
//			dest = new Matrix4f();
//		else
//			dest.identity();
//		dest.translate(a.position.x + (b.position.x - a.position.x) * progression,
//				a.position.y + (b.position.y - a.position.y) * progression,
//				a.position.z + (b.position.z - a.position.z) * progression);
//		dest.rotate
//		return dest;
////		dest.x = x + (other.x() - x) * t;
////		dest.y = y + (other.y() - y) * t;
////		dest.z = z + (other.z() - z) * t;
//	}

	/**
	 * can handle different array lengths (just skips the missing entries) but not
	 * null values except in dest
	 */
	public static void interpolate(Transform[] t, Transform[] t2, float progression, Transform[] dest) {
		for (int i = 0; i < t.length && i < t2.length && i < dest.length; i++) {
			dest[i] = interpolate(t[i], t2[i], progression, dest[i]);
		}
	}

	/**
	 * can handle different array lengths (just skips the missing entries) but not
	 * null values except in dest and helper
	 */
	public static void interpolate(Transform[] t, Transform[] t2, float progression, Transform[] helper,
			Matrix4f[] dest) {
		for (int i = 0; i < t.length && i < t2.length && i < dest.length; i++) {
			helper[i] = interpolate(t[i], t2[i], progression, helper[i]);
			if (dest[i] == null)
				dest[i] = new Matrix4f();
		}
		transform(helper, dest);
	}

	public static void transform(Transform[] t, Matrix4f[] m) {
		for (int i = 0; i < t.length; i++) {
			t[i].transform(m[i].identity());
		}
	}

	public void set(Transform t) {
		this.position.set(t.position);
		this.rotation.set(t.rotation);
	}

	/**
	 * copies the translation and rotation from m. scaling not supported
	 */
	public void set(Matrix4f m) {
		m.getTranslation(position);
		rotation.setFromNormalized(m);
	}

	public void identity() {
		this.position.set(0);
		this.rotation.identity();
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		add(dest, position);
		add(dest, rotation);
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		position = vector3f(src);
		rotation = quaternionf(src);
	}

	@Override
	public void writeSaveVersions(Map<String, Short> saveVersions) {
		saveVersions.put(Transform.class.getName(), (short) 0);
	}

}
