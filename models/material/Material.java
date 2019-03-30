package models.material;

import java.util.Map;

import org.joml.Vector4f;

import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import openGlResources.textures.Texture;

public class Material extends Savable {

	public static final Material white = new Material(1, 1, 1, 1), black = new Material(0, 0, 0, 1),
			red = new Material(1, 0, 0, 1), green = new Material(0, 1, 0, 1), blue = new Material(0, 0, 1, 1);

	private static final float defaultBloom = 0.1f;

	protected MaterialLayer diffuse,
			bloom = new MaterialLayer(new Vector4f(defaultBloom, defaultBloom, defaultBloom, 1));
	protected float reflectivity = 0.075f, reflectDampening = 10;
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

	public Material(Vector4f diffuseColor, float reflectivity) {
		diffuse = new MaterialLayer(diffuseColor);
		this.reflectivity = reflectivity;
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
		bloom = new MaterialLayer(toCopy.bloom);
		transparent = toCopy.transparent;
		translucent = toCopy.translucent;
		this.reflectivity = toCopy.reflectivity;
		this.reflectDampening = toCopy.reflectDampening;
	}

	public Material(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public MaterialLayer diffuse() {
		return diffuse;
	}

	public MaterialLayer bloomMat() {
		return bloom;
	}

	public float bloomFactor() {
		return (bloom.color.x + bloom.color.y + bloom.color.z) / 3f;
	}

	public void bloom(float bloomFactor) {
		bloom.color.set(bloomFactor, bloomFactor, bloomFactor, 1);
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

	public float reflectivity() {
		return reflectivity;
	}

	public void reflectivity(float f) {
		reflectivity = f;
	}

	public float reflectDampening() {
		return reflectDampening;
	}

	public void reflectDampening(float f) {
		reflectDampening = f;
	}

	public void delete() {
		if (diffuse != null)
			diffuse.delete();
	}

	public int compare(Material other) {
		return 0;
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
		bloom.addData(dest);
		dest.addBool(transparent);
		dest.addBool(translucent);
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		if (diffuse == null)
			diffuse = new MaterialLayer(new Vector4f());
		diffuse.applyData(src, saveVersions);
		if (bloom == null)
			bloom = new MaterialLayer(new Vector4f());
		bloom.applyData(src, saveVersions);
		transparent = src.readBool();
		translucent = src.readBool();
	}

	public Material clone() {
		return new Material(this);
	}

}
