package genericRendering.lights;

import java.util.*;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import generic.Thing;
import models.Asset;
import models.material.Material;
import openGlResources.buffers.VAO;
import tools.Meth;

public class Lamp extends Thing {

	protected Light light;
	protected List<Asset> assets = new ArrayList<>();

	public Lamp(VAO model, float scale, float r, float g, float b) {
		this(model, scale, new Vector3f(), r, g, b);
	}

	public Lamp(VAO model, float scale, float x, float y, float z, float r, float g, float b) {
		this(model, scale, new Vector3f(x, y, z), r, g, b);
	}

	public Lamp(VAO model, float scale, Vector3f pos, float r, float g, float b) {
		this(model, new Material(r, g, b, 1), scale, pos, r, g, b);
		float l = Meth.length(r, g, b);
		this.material.diffuse().color().x /= l;
		this.material.diffuse().color().y /= l;
		this.material.diffuse().color().z /= l;
		this.material.bloomMat().color().set(r, g, b, 1);
	}

	public Lamp(VAO model, Material mat, float scale, float r, float g, float b) {
		this(model, mat, scale, new PointLight(r, g, b));
	}

	public Lamp(VAO model, Material mat, float scale, Vector3f pos, float r, float g, float b) {
		this(model, mat, scale, pos, new PointLight(r, g, b));
	}

	public Lamp(VAO model, Material mat, float scale, Light l) {
		super(model, mat, new Vector3f(), scale);
		doPhysics = false;
		light = l;
		light.setParent(this);
		assets.add(light);
	}

	public Lamp(VAO model, Material mat, float scale, Vector3f pos, Light l) {
		super(model, mat, pos, scale);
		doPhysics = false;
		light = l;
		light.setParent(this);
		assets.add(light);
	}

	public Lamp(Lamp toClone) {
		super(toClone);
		this.light = toClone.light.clone();
	}

	public Lamp(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	/**
	 * don't forget to remove the old light (if not null)
	 * 
	 * @return the old light
	 */
	public Light setLight(Light l) {
		Light r = light;
		light = l;
		light.setParent(this);
		assets.clear();
		assets.add(light);
		return r;
	}

	public Light light() {
		return light;
	}

	@Override
	public List<Asset> assets() {
		return assets;
	}

	@Override
	public Lamp clone() {
		return new Lamp(this);
	}

}
