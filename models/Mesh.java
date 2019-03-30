package models;

import java.util.Map;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import models.animated.AnimatedModelData;
import models.components.AnimationComponent;
import models.material.Material;
import openGlResources.buffers.VAO;

public class Mesh extends Model {

	protected AnimationComponent animation;

	protected Mesh(Vector3f pos) {
		super(pos);
	}

	public Mesh(VAO vao, Material mat) {
		super(vao, mat);
	}

	public Mesh(VAO vao, Material mat, float scale) {
		this(vao, mat, new Vector3f(), scale);
	}

	public Mesh(VAO vao, Material mat, Vector3f pos) {
		this(vao, mat, pos, 1);
	}

	public Mesh(VAO vao, Material mat, Vector3f pos, float scale) {
		super(vao, mat, pos, scale);
	}

	public Mesh(AnimatedModelData animdat, Material mat) {
		super(animdat.getVAO(), mat);
		animation = new AnimationComponent(animdat);
	}

	public Mesh(AnimatedModelData animdat, Material material, Vector3f pos, float scale) {
		super(animdat.getVAO(), material, pos, scale);
		animation = new AnimationComponent(animdat);
	}

	public Mesh(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public Mesh(Mesh toClone) {
		this(toClone.vao, new Material(toClone.material));
		this.position.set(toClone.position);
		this.scale.set(toClone.scale());
		if (toClone.animation != null)
			this.animation = new AnimationComponent(toClone.animation);
	}

	public void setAnimationComponent(AnimationComponent anim) {
		animation = anim;
		animation.setParent(this);
	}

	public AnimationComponent animation() {
		return animation;
	}

}
