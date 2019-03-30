package loaders;

import static tools.AppFolder.absolute;

import java.util.HashMap;
import java.util.Map;

import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import openGlResources.textures.Texture;
import tools.AppFolder;

public class TextureCache extends Savable {

	private static Map<Thread, TextureCache> caches = new HashMap<>();

	public static TextureCache get() {
		TextureCache ret = caches.get(Thread.currentThread());
		if (ret == null)
			caches.put(Thread.currentThread(), ret = new TextureCache());
		return ret;
	}

	private Map<String, Texture> texes = new HashMap<>();

	public TextureCache() {

	}

	public TextureCache(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	/**
	 * absolute if begins with "/", relative otherwise. If you put in an absolute
	 * path, it's *NOT* relative to the AppFolder. For that use
	 * {@link TextureCache#getFolderTexture(String)}
	 */
	public Texture getTexture(String path) {
		String s = simplify(path);
		Texture ret = texes.get(s);
		if (ret == null) {
			texes.put(s, ret = (absolute(s) ? Texture.loadExternalTexture(s) : Texture.loadJarTexture(s)));
		}
		return ret;
	}

	public Texture getLoadedTexture(String name) {
		return texes.get(simplify(name));
	}

	/**
	 * @param path to the texture file in the AppFolder
	 */
	public Texture getFolderTexture(String path) {
		return getTexture(AppFolder.folder + path);
	}

	public Texture putTexture(String name, Texture t) {
		Texture ret = texes.get(name);
		texes.put(name, t);
		return ret;
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
		for (Texture tex : texes.values()) {
			if (tex != null)
				tex.delete();
		}
		texes.clear();
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
	}

}
