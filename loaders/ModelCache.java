package loaders;

import java.util.HashMap;
import java.util.Map;

import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import openGlResources.buffers.VAO;
import tools.AppFolder;

public class ModelCache extends Savable {

	private static Map<Thread, ModelCache> caches = new HashMap<>();
	private static Thread firstThread = null;

	public static ModelCache get() {
		ModelCache ret;
		if (firstThread == null) {
			firstThread = Thread.currentThread();
			caches.put(Thread.currentThread(), ret = new ModelCache());
		} else
			ret = caches.get(firstThread);
		return ret;
	}

	public static ModelCache getThreadInstance() {
		ModelCache ret = caches.get(Thread.currentThread());
		if (ret == null)
			caches.put(Thread.currentThread(), ret = new ModelCache());
		return ret;
	}

	private Map<String, VAO> names = new HashMap<>();
	private Map<String, VAO> vaos = new HashMap<>();
	private Map<Integer, String> paths = new HashMap<>();

	public ModelCache() {

	}

	public ModelCache(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	/**
	 * absolute if begins with "/", relative otherwise. If you put in an absolute
	 * path, it's *NOT* relative to the AppFolder. For that use
	 * {@link TextureCache#getFolderTexture(String)}
	 */
	public VAO getModel(String path) {
		String s = simplify(path);
		VAO ret = vaos.get(s);
		if (ret == null) {
			ret = names.get(s);
			if (ret == null) {
				ret = Loader.loadToVAO(s);
				if (ret == null) {
					if (s.endsWith("obj")) {
						ret = Loader.loadToVAO("res/models/obj/" + s);
					} else if (s.endsWith("dae")) {
						ret = Loader.loadToVAO("res/models/collada/" + s);
					}
				}
				if (ret == null)
					return null;
				vaos.put(s, ret);
				VAO k = names.put(extractName(s), ret);
				if (k != null)
					System.err.println("overriding '" + k.name() + "' with '" + s + "'!");
				paths.put(ret.ID(), s);
			}
		}
		return ret;
	}

	public VAO getLoadedModel(String name) {
		String n = simplify(name);
		VAO v = vaos.get(n);
		if (v != null)
			return v;
		return names.get(n);
	}

	/**
	 * @param path to the texture file in the AppFolder
	 */
	public VAO getFolderModel(String path) {
		return getModel(AppFolder.folder + path);
	}

	public static String extractName(String path) {
		String[] split = path.split("/");
		return split[split.length - 1];
	}

	public static String simplify(String path) {
		if (!path.contains("..") && !path.contains("//"))
			return path;
		String[] split = path.split("/");
		StringBuilder ret = new StringBuilder();
		if (path.charAt(0) == '/')
			ret.append('/');
		for (int i = 0; i < split.length; i++) {
			if (!split[i].isEmpty() && i < split.length - 1 && !split[i + 1].equals("..") && !split[i].equals("..")) {
				ret.append(split[i]);
				ret.append('/');
			}
		}
		return ret.toString();
	}

	public void deleteAll() {
		for (VAO v : vaos.values()) {
			if (v != null)
				v.delete();
		}
		vaos.clear();
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		dest.addInt(vaos.size());
		for (String s : vaos.keySet()) {
			dest.addString(s);
			dest.addInt(vaos.get(s).ID());
		}
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		int count = src.readInt();
		for (int i = 0; i < count; i++) {
			String path = src.readString();
			int value = src.readInt();
			paths.put(value, path);
		}
	}

	public static void mapTo(Thread other) {
		map(Thread.currentThread(), other);
	}

	public static void map(Thread one, Thread other) {
		caches.put(other, caches.get(one));
	}

}
