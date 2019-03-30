package models;

import java.util.Map;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import models.material.Material;
import openGlResources.buffers.VAO;

public class StaticModel extends Model {

	// really just a utility class, nothing here

	public StaticModel(Vector3f position) {
		super(position);
	}

	public StaticModel(VAO vao, Material m) {
		super(vao, m);
	}
	
	public StaticModel(VAO vao, Material m, float scale) {
		this(vao, m, new Vector3f(), scale);
	}
	
	public StaticModel(VAO vao, Material m, Vector3f position) {
		super(vao, m, position);
	}

	public StaticModel(VAO vao, Material m, Vector3f position, float scale) {
		super(vao, m, position, scale);
	}

	public StaticModel(VAO vao, Material m, Vector3f position, float scale, Quaternionf rotation) {
		super(vao, m, position, scale, rotation);
	}

	/**
	 * has the same vao and material as m (referenced!) but different position and
	 * rotation references (those are just copied)
	 */
	public StaticModel(StaticModel m) {
		this(m.vao, m.material);
		this.scale = new Vector3f(m.scale);
		this.position.set(m.position);
		this.rotation.set(m.rotation);
	}

	public StaticModel(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	
}
