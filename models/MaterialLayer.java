package models;

import java.util.Map;

import org.joml.Vector4f;

import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import loaders.TextureCache;
import openGlResources.textures.Texture;

/**
 * 
 * usage: if {@link MaterialLayer#hasTexture()} -> use
 * {@link MaterialLayer#tex()}, otherwise use {@link MaterialLayer#color()}
 * 
 * @author xaver
 * 
 */
public class MaterialLayer extends Savable {

	protected Vector4f color;
	protected Texture t;

	public MaterialLayer(Texture t) {
		this.t = t;
		color = new Vector4f();
	}

	public MaterialLayer(Vector4f color) {
		this.color = color;
	}

	public MaterialLayer(float r, float g, float b, float a) {
		this(new Vector4f(r, g, b, a));
	}

	public MaterialLayer(MaterialLayer toCopy) {
		t = toCopy.t;
		if (toCopy.color != null)
			this.color = new Vector4f(toCopy.color);
	}

	public MaterialLayer(SmartByteBuffer data, Map<String, Short> version) {
		super(data, version);
	}

	public Texture tex() {
		return t;
	}

	public Vector4f color() {
		return color;
	}

	public boolean hasTexture() {
		return t != null;
	}

	public boolean hasColor() {
		return color != null;
	}

	public void delete() {
		if (t != null)
			t.delete();
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		dest.add((byte) (color == null ? 0 : 1));
		if (color != null) {
			add(dest, color);
		}
		if (t == null || t.name() == null) {
			dest.addString("");
		} else {
			dest.addString(t.name());
		}
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		byte c = src.read();
		if (c == 1) {
			color = vector4f(src, color == null ? new Vector4f() : color);
		} else {
			color = new Vector4f();
		}
		String name = src.readString();
		if (name.length() > 0) {
			if (t == null || !t.name().equals(name))
				t = TextureCache.get().getTexture(name);
		} else {
			t = null;
		}
	}

}
