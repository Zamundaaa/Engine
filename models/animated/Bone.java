package models.animated;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import generic.Thing;
import generic.Transform;
import tools.misc.Vects;

public class Bone extends Thing {

	public final String name;
	protected final ArrayList<Bone> children = new ArrayList<>();
	protected final int boneID;
	protected final Matrix4f nodeTransform;
	protected final Matrix4f animatedTransform, offsetmat;

	public Bone(int ID, String name, Matrix4f offset_matrix) {
		animatedTransform = new Matrix4f();
		nodeTransform = new Matrix4f();
		this.boneID = ID;
		this.name = name;
		this.offsetmat = offset_matrix;
//		AppFolder.log.println("bone " + name);
	}

	public void putAnimatedTransforms(Matrix4f[] jointMatrices) {
		jointMatrices[boneID] = animatedTransform;
	}

	public ArrayList<Bone> children() {
		return children;
	}

	public int boneID() {
		return boneID;
	}

	public void setNodeTransform(Matrix4f transform) {
		this.nodeTransform.set(transform);
	}

	public Matrix4f offsetmat() {
		return offsetmat;
	}

	public Matrix4f nodeTransform() {
		return nodeTransform;
	}

	public Thing rep;

	/**
	 * traverses the whole tree of Bones under _this_ and transforms all the
	 * Transforms in the KeyFrame according to the bone structure
	 */
	public void transformByParentTransform(KeyFrame f, Matrix4f parentTransform) {
		// one could and should actually traverse a map of aiNodes here so it's always
		// correct,
		// even for non skeletal animations
		if (f.pose[boneID] == null)
			f.pose[boneID] = new Transform();
		Matrix4f m = f.pose[boneID].transform(new Matrix4f());
		parentTransform.mul(m);
//		if (f.timeStamp < 0.1f) {
//			AppFolder.log.print("parent: " + name + ". children: ");
//			for (int i = 0; i < children.size(); i++) {
//				AppFolder.log.print(children.get(i).name, false);
//				if (i < children.size() - 1)
//					AppFolder.log.print(", ", false);
//			}
//			AppFolder.log.println();
//		}
		parentTransform.transformPosition(f.pose[boneID].position().set(0));
		parentTransform.getUnnormalizedRotation(f.pose[boneID].rotation());

		for (int i = 0; i < children.size(); i++) {
			children.get(i).transformByParentTransform(f, new Matrix4f(parentTransform));
		}

	}

	@Override
	public Bone clone() {
		return new Bone(this.boneID, new String(this.name), new Matrix4f(offsetmat));
	}

	@Override
	public Matrix4f transform(Matrix4f m) {
		return parentTransform(m);
	}

	@Override
	public Matrix4f parentTransform(Matrix4f m) {
		if (parent != null) {
			return parent.parentTransform(m)
					.translate(animatedTransform.getTranslation(Vects.calcVect()).mul(parent.scale()))
					.rotate(animatedTransform.getUnnormalizedRotation(Vects.quat));
		} else {
			return m.mul(animatedTransform);
		}
	}

	@Override
	public Vector3f rotateAbsolute(Vector3f s) {
		if (parent == null)
			return animatedTransform.getUnnormalizedRotation(Vects.quat).transform(s);
		else
			return parent.rotateAbsolute(animatedTransform.getUnnormalizedRotation(Vects.quat).transform(s));
	}

	@Override
	public Matrix4f createTransformationMatrix(Matrix4f m) {
		Matrix4f ret = transform(m.identity());
		ret.transformPosition(absolutePosition.set(0));
		return ret;
	}

	public Matrix4f animatedTransform() {
		return animatedTransform;
	}

}
