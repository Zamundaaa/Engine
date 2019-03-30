package models;

import java.util.Map;

import org.joml.Vector4f;

import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import openGlResources.textures.Texture;

public class Material extends Savable {

	public static final Material white = new Material(1, 1, 1, 1), black = new Material(0, 0, 0, 1),
			red = new Material(1, 0, 0, 1), green = new Material(0, 1, 0, 1), blue = new Material(0, 0, 1, 1);

	protected MaterialLayer diffuse;
	protected float bloom;
	protected boolean translucent = false, transparent = false;

	public Material() {
		diffuse = new MaterialLayer(1, 1, 1, 1);
	}

	public Material(Texture diffuseTex) {
		diffuse = new MaterialLayer(diffuseTex);
		if (diffuseTex != null) {
			transparent = diffuseTex.isTransparent();
			translucent = diffuseTex.isTranslucent();
		}
	}

	public Material(Vector4f diffuseColor) {
		diffuse = new MaterialLayer(diffuseColor);
		if (diffuseColor.w == 0)
			transparent = true;
		else if (diffuseColor.w < 1)
			translucent = true;
	}

	public Material(float diffuse_r, float diffuse_g, float diffuse_b, float diffuse_a) {
		this(new Vector4f(diffuse_r, diffuse_g, diffuse_b, diffuse_a));
	}

	public Material(Material toCopy) {
		diffuse = new MaterialLayer(toCopy.diffuse);
		translucent = toCopy.translucent;
		transparent = toCopy.transparent;
		bloom = toCopy.bloom;
	}

	public Material(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public MaterialLayer diffuse() {
		return diffuse;
	}

	public float bloomFactor() {
		return bloom;
	}

	public void bloom(float bloomFactor) {
		bloom = bloomFactor;
	}

	public boolean translucent() {
		return translucent;
	}

	public void translucent(boolean translucent) {
		this.translucent = translucent;
	}

	public boolean transparent() {
		return transparent;
	}

	public void transparent(boolean t) {
		transparent = t;
	}

	public void delete() {
		if (diffuse != null)
			diffuse.delete();
	}

	@Override
	public String toString() {
		return "material " + (diffuse.hasTexture() ? "with" : "without") + " diffuse texture"
				+ (diffuse.hasTexture() ? "" : " but color " + diffuse.color);
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		diffuse.addData(dest);
		dest.addFloat(bloom);
		dest.addBool(transparent);
		dest.addBool(translucent);
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		if (diffuse == null)
			diffuse = new MaterialLayer(new Vector4f());
		diffuse.applyData(src, saveVersions);
		bloom = src.readFloat();
		transparent = src.readBool();
		translucent = src.readBool();
	}

}
