package generic;

import java.util.HashMap;
import java.util.Map;

import org.joml.*;

import collectionsStuff.SmartByteBuffer;

/**
 * 
 * a Savable-Constructor *has* to be put into *every* Serializer - class
 * 
 * @author xaver
 *
 */
public abstract class Savable {

	public static interface SavableAction {

		public void action(Savable s);

	}

	private static Map<Integer, Savable> allSavables = new HashMap<>();

	private static volatile int currMaxID = 0;// Integer.MIN_VALUE

	public final int ID;

	public Savable(int ID) {
		this.ID = ID;
		// security check vielleicht später einbauen...
		allSavables.put(ID, this);
	}

	public Savable() {
		this(++currMaxID);
	}

	public Savable(SmartByteBuffer buff, Map<String, Short> saveVersions) {
		this.ID = buff.readInt();
		allSavables.put(ID, this);
		buff.setPosition(buff.position() - 4);
		applyData(buff, saveVersions);
	}

	public Savable(int ID, SmartByteBuffer buff, Map<String, Short> saveVersions) {
		this(ID);
		applyData(buff, saveVersions);
	}

	/**
	 * add the data you want to use in applyData. The layout of this data may not be
	 * different from what you read out in
	 * {@link Savable#applyData(SmartByteBuffer, short)}
	 */
	public void addData(SmartByteBuffer dest) {
		dest.addInt(ID);
	}

	/**
	 * read the data and apply it to your fields. Is called automatically
	 */
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		src.readInt();
	}

	/**
	 * should be incremented whenever you add new stuff or change the format; please
	 * implement {@link Savable#applyData(SmartByteBuffer, short)} accordingly
	 */
	public void writeSaveVersions(Map<String, Short> saveVersions) {
		saveVersions.put(this.getClass().getName(), (short) 0);
	}

	/**
	 * HAS TO BE CALLED IF OVERRIDDEN!!!
	 */
	public void cleanUp() {
		allSavables.remove(ID);
	}

	public static void add(SmartByteBuffer b, Vector3f v) {
		b.addFloat(v.x);
		b.addFloat(v.y);
		b.addFloat(v.z);
	}

	public static Vector3f vector3f(SmartByteBuffer b) {
		return vector3f(b, new Vector3f());
	}

	public static Vector3f vector3f(SmartByteBuffer b, Vector3f dest) {
		return dest.set(b.readFloat(), b.readFloat(), b.readFloat());
	}

	public static void add(SmartByteBuffer b, Vector4f v) {
		b.addFloat(v.x);
		b.addFloat(v.y);
		b.addFloat(v.z);
		b.addFloat(v.w);
	}

	public static Vector4f vector4f(SmartByteBuffer b) {
		return vector4f(b, new Vector4f());
	}

	public static Vector4f vector4f(SmartByteBuffer b, Vector4f dest) {
		return dest.set(b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat());
	}

	public static void add(SmartByteBuffer b, Quaternionf q) {
		b.addFloat(q.x);
		b.addFloat(q.y);
		b.addFloat(q.z);
		b.addFloat(q.w);
	}

	public static Quaternionf quaternionf(SmartByteBuffer b) {
		return quaternionf(b, new Quaternionf());
	}

	public static Quaternionf quaternionf(SmartByteBuffer b, Quaternionf dest) {
		return dest.set(b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat());
	}

}
