package generic;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.*;

import collectionsStuff.SmartByteBuffer;
import tools.AppFolder;

/**
 * 
 * should enable pretty easy saving and restoring of Stuff. If {@link Savable}
 * is implemented correctly, it can has version independent reading
 * capabilities.
 * 
 * @author xaver
 *
 */
public abstract class Serializer {

	// more bandwidth-efficient NetSerializer (INSTANCED!) for a NetworkConnection
	// so classVersions only have to be sent a single time. Also common IDs for
	// classes then. And probably common IDs for Strings like VAO or Texture names

	private static Map<Thread, Map<String, Short>> allClassVersions = new HashMap<>();

	/**
	 * is ok with null values in the list as with a null list (treats it the same as
	 * an empty list)
	 * 
	 * @param src
	 * @param dest
	 */
	public static SmartByteBuffer write(List<Savable> src, SmartByteBuffer dest) {
		if (dest == null)
			dest = new SmartByteBuffer();
		if (src == null || src.size() == 0) {
			dest.addInt(0);
			return dest;
		}
		Map<String, Short> classVersions = getClassVersionsMap();
		classVersions.clear();
		for (int i = 0; i < src.size(); i++) {
			Savable s = src.get(i);
			if (s != null) {
				s.writeSaveVersions(classVersions);
				if (!classVersions.containsKey(s.getClass().getName())) {
					classVersions.put(s.getClass().getName(), (short) 0);
				}
			}
		}
		dest.addInt(src.size());
		Set<String> keyset = classVersions.keySet();
		dest.addShort((short) keyset.size());

		ArrayList<String> l = new ArrayList<>();
		Map<String, Short> indices = new HashMap<>();

//		AppFolder.log.println(keyset.size() + " classes...");
		for (String s : keyset) {
			indices.put(s, (short) l.size());
			l.add(s);
			dest.addString(s);
			dest.addShort(classVersions.get(s));
		}
		for (int i = 0; i < src.size(); i++) {
			Savable s = src.get(i);
			if (s != null) {
				Short index = indices.get(s.getClass().getName());
				if (index == null) {
					AppFolder.log.println("wtf failed here");
					dest.addShort((short) -1);
				} else {
					dest.addShort(index);
					s.addData(dest);
				}
			} else {
				dest.addShort((short) -1);
			}
		}
//		AppFolder.log.println("sending " + src.size() + " Savables");
		return dest;
	}

	public static List<Savable> read(List<Savable> dest, SmartByteBuffer src) {
		return read(dest, src, null);
	}

	/**
	 * @param dest may be null
	 * @param src
	 * @return may be null. If not, it's an ArrayList containing all the Savables
	 *         read from buff. If dest is not null, the Savables are appended to
	 *         dest instead of put into a new list
	 */
	public static List<Savable> read(List<Savable> dest, SmartByteBuffer src, Map<String, Short> classVersionsDest) {
		// this format shouldn't change like *ever*
		int count = src.readInt();
		if (count == 0)
			return null;
		if (dest == null) {
			dest = new ArrayList<Savable>(count);
		} else if (dest instanceof ArrayList) {
			((ArrayList<Savable>) dest).ensureCapacity(dest.size() + count);
		}
		Map<String, Short> classVersions = getClassVersionsMap();
		classVersions.clear();
		// you _could_ buffer this list, too!
		ArrayList<String> l = new ArrayList<>();
		int classCount = src.readShort();
		for (int i = 0; i < classCount; i++) {
			String s = src.readString();
			l.add(s);
			classVersions.put(s, src.readShort());
		}
		if (classVersionsDest != null)
			classVersionsDest.putAll(classVersions);
		try {
			for (int i = 0; i < count; i++) {
				int index = src.readShort();
				if (index > -1 && index < l.size()) {
					Savable t;
					try {
						t = getThing(src, l.get(index), classVersions);
						if (t != null) {
							dest.add(t);
						} else {
							throw new InvalidParameterException(
									"something's wrong with that buffer or there's a bug; a wrong class name was found ("
											+ l.get(index) + ")!");
						}
					} catch (Exception e) {
						e.printStackTrace(AppFolder.log);
					}
				} else {
					dest.add(null);
				}
			}
		} catch (Exception e) {
			AppFolder.log.println("count was " + count + ". Stacktrace:");
			e.printStackTrace(AppFolder.log);
		}
		return dest;
	}

	public static SmartByteBuffer write(Savable s, SmartByteBuffer dest) {
		ArrayList<Savable> l = new ArrayList<>(1);
		l.add(s);
		return write(l, dest);
	}

	public static Savable read(SmartByteBuffer src) {
		List<Savable> l = read(null, src);
		if (l == null)
			return null;
		else
			return l.get(0);
	}

	private static Map<String, Short> getClassVersionsMap() {
		Map<String, Short> classVersions = allClassVersions.get(Thread.currentThread());
		if (classVersions == null) {
			classVersions = new HashMap<>();
			allClassVersions.put(Thread.currentThread(), classVersions);
		}
		return classVersions;
	}

	private static Savable getThing(SmartByteBuffer buff, String classID, Map<String, Short> versions)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		// TODO for multiplayer data transfer just use the IDs and an
		// Savable
		// FIXME find class in other packages if not present...
		return (Savable) Class.forName(classID).getConstructor(SmartByteBuffer.class, Map.class).newInstance(buff,
				versions);
	}

}
