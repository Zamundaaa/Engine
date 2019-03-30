package models.animated;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import models.ModelData;
import openGlResources.buffers.VAO;
import openGlResources.buffers.VBO;

public class AnimatedModelData extends ModelData {

	protected ArrayList<Animation> animations;
//	protected Bone rootBone;
	protected Matrix4f globalInverseTransform = new Matrix4f();
	protected List<Bone> bones;
	/**
	 * HEY! THIS CAN CAUSE ISSUES!!!
	 */
	protected Bone[] boneArray = new Bone[0];
	protected int[] jointIDs;
	protected float[] weights;
	protected VAO vao;

	public AnimatedModelData(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices,
			int[] jointIDs, float[] weights, ArrayList<Animation> animations, List<Bone> bones) {
		super(vertices, textureCoordinates, normals, indices);
		this.jointIDs = jointIDs;
		this.weights = weights;
		this.animations = animations;
		this.bones = bones;
	}

	public AnimatedModelData(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices,
			int[] jointIDs, float[] weights, List<Bone> bones) {
		super(vertices, textureCoordinates, normals, indices);
		this.jointIDs = jointIDs;
		this.weights = weights;
		this.animations = new ArrayList<>();
		this.bones = bones;
	}

	public ArrayList<Animation> animations() {
		return animations;
	}

	public void addAnimation(Animation a) {
		animations.add(a);
	}

	@Override
	public VAO loadToVAO() {
//		AppFolder.log.println("uploading animated model with " + (positions.length / 3) + " verts and "
//				+ indices.length + " indices");
//		vao = Loader.loadToVAO(indices, new int[] { 3, 2, 3 }, positions, textureCoordinates, normals);
		vao = super.loadToVAO();
		vao.setVbo(3, VBO.createStaticVertexDataVBO(jointIDs, 4));
		vao.setVbo(4, VBO.createStaticVertexDataVBO(weights, 4));
		return vao;
	}

	public Bone[] boneArray() {
		if (boneArray.length != bones.size()) {
			boneArray = bones.toArray(boneArray);
		}
		return boneArray;
	}

	public List<Bone> bones() {
		return bones;
	}

//	public void setRootBone(Bone rootBone) {
//		this.rootBone = rootBone;
//	}

//	public Bone rootBone() {
//		return rootBone;
//	}

	public void setGlobalInverseTransform(Matrix4f m) {
		this.globalInverseTransform = m;
	}

	public Matrix4f globalInverseTransform() {
		return globalInverseTransform;
	}

	public Bone[] bonesCloned() {
		return cloneBones(boneArray());
	}

	public List<Animation> animationsCloned() {
		return cloneAnimationsList(animations, null);
	}

	public static Bone[] cloneBones(Bone[] bones) {
		Bone[] ret = new Bone[bones.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = bones[i].clone();
		}
		return ret;
	}

	public static List<Animation> cloneAnimationsList(List<Animation> animations, List<Animation> dest) {
		if (dest == null) {
			dest = new ArrayList<>();
		}
		for (int i = 0; i < animations.size(); i++) {
			dest.add(animations.get(i).clone());
		}
		return dest;
	}

}
