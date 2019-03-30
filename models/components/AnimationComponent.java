package models.components;

import java.util.*;

import org.joml.Matrix4f;

import generic.Thing;
import generic.Transform;
import models.animated.*;
import tools.misc.Vects;

public class AnimationComponent {

	protected List<Animation> animations = new ArrayList<>();
	protected Map<String, Animation> animMap = new HashMap<>();
	protected Animation currentAnimation = null;
	protected CrossAnimation interpol = new CrossAnimation();

	protected Thing parent;
	protected final Matrix4f globalInverseTransform;
	protected final Bone[] bones;
	protected final Matrix4f[] calcPose, currentPose;
	protected final Transform[] calc;
	protected boolean updateAutomatically = true;
	protected long lastUpdate = System.currentTimeMillis();

	public AnimationComponent(AnimatedModelData animdat) {
		this.globalInverseTransform = animdat.globalInverseTransform();
		this.bones = animdat.bonesCloned();
		this.animations = animdat.animationsCloned();
		if (animations.size() > 0)
			selectAnimation(animations.get(0), 0);
		this.currentPose = new Matrix4f[bones.length];
		this.calcPose = new Matrix4f[bones.length];
		this.calc = new Transform[bones.length];
		for (int i = 0; i < currentPose.length; i++) {
			bones[i].putAnimatedTransforms(calcPose);
			currentPose[i] = new Matrix4f();
			calc[i] = new Transform();
		}
	}

	public AnimationComponent(AnimationComponent animation) {
		this.globalInverseTransform = animation.globalInverseTransform();
		this.bones = animation.bonesCloned();
		this.animations = animation.animationsCloned();
		if (animations.size() > 0)
			selectAnimation(animations.get(0), 0);
		this.currentPose = new Matrix4f[bones.length];
		this.calcPose = new Matrix4f[bones.length];
		this.calc = new Transform[bones.length];
		for (int i = 0; i < currentPose.length; i++) {
			bones[i].putAnimatedTransforms(calcPose);
			currentPose[i] = new Matrix4f();
			calc[i] = new Transform();
		}
	}

	public void setParent(Thing thing) {
		this.parent = thing;
		attachSkeleton(thing);
	}

	public void selectAnimation(String name, float interpolationTime) {
		selectAnimation(animMap.get(name), interpolationTime);
	}

	public void selectAnimation(Animation a, float interpolationTime) {
		if (interpolationTime > 0) {
			if (a == null
					&& (currentAnimation == null || currentAnimation == interpol && interpol.goalAnim() == null)) {
				// absolutely nothing to do!
			} else {
				// this kinda doesn't work anymore as it seems
				currentAnimation = interpol.set(currentAnimation, a, interpolationTime, bones);
			}
		} else {
			currentAnimation = a;
			if (currentAnimation != null)
				currentAnimation.start();
		}
	}

	public void updateAnimation() {
		long t = System.currentTimeMillis() - lastUpdate;
		if (lastUpdate == 0)
			t = 1;
		updateAnimation(t * 0.001f);
	}

	public void updateAnimation(float time) {
		lastUpdate = System.currentTimeMillis();
		if (currentAnimation == null) {
			for (int i = 0; i < currentPose.length; i++)
				currentPose[i].identity();
			return;
		}
		boolean b = currentAnimation.update(time);
		if (b && currentAnimation instanceof CrossAnimation) {
			currentAnimation = ((CrossAnimation) currentAnimation).goalAnimation();
			if (currentAnimation == null) {
				for (int i = 0; i < currentPose.length; i++)
					currentPose[i].identity();
				return;
			}
		}
		currentAnimation.calculateCurrentPose(calcPose, calc);
//		for (int i = 0; i < bones.length; i++) {
//			if (bones[i].rep != null) {
////				Vects.mat42.set(globalInverseTransform).mul(bones[i].offset_matrix).mul(currentPose[i])
////						.transformPosition(bones[i].rep.position().set(0));
//				// .mul(bones[i].invoffsetmat)
////				bones[i].invoffsetmat.invert();
//				Vects.mat4.set(globalInverseTransform).mul(currentPose[i])
//						.transformPosition(bones[i].rep.position().set(0));
////				bones[i].invoffsetmat.invert();
////				AppFolder.log.println(bones[i].rep.position());
//				Vects.mat4.getUnnormalizedRotation(bones[i].rep.rotation());
//			}
//		}
		for (int i = 0; i < currentPose.length; i++) {
			currentPose[i].set(globalInverseTransform).mul(calcPose[i]).mul(bones[i].offsetmat());
		}
	}

	public void setBoneRep(Thing m, int bone, Thing t) {// .mul(globalInverseTransform)
		m.createTransformationMatrix(Vects.mat4).mul(calcPose[bone]).transformPosition(t.position().set(0));
		Vects.mat4.getUnnormalizedRotation(t.rotation().identity());
	}

	public int bonecount() {
		return getBones().length;
	}

	public Bone getBone(String name) {
		for (int i = 0; i < bones.length; i++)
			if (bones[i].name.equals(name))
				return bones[i];
		return null;
	}

	/**
	 * just sets the bones parent to t. This is very useful for animating stuff
	 * that's handheld etc
	 */
	public void attachSkeleton(Thing t) {
		for (int i = 0; i < bones.length; i++) {
			bones[i].setParent(t);
		}
	}

	public List<Animation> getAnimations() {
		return animations;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public CrossAnimation getCrossAnim() {
		return interpol;
	}

	public Matrix4f globalInverseTransform() {
		return globalInverseTransform;
	}

	public Bone[] getBones() {
		return bones;
	}

	public Bone[] bonesCloned() {
		return AnimatedModelData.cloneBones(bones);
	}

	public List<Animation> animationsCloned() {
		return AnimatedModelData.cloneAnimationsList(animations, new ArrayList<>());
	}

	public Matrix4f[] getPose() {
		return currentPose;
	}

	public void updateAutomatically(boolean b) {
		updateAutomatically = b;
	}

	public boolean updateAutomatically() {
		return updateAutomatically;
	}

	public long lastUpdate() {
		return lastUpdate;
	}

	public Animation currentAnimation() {
		return currentAnimation;
	}

	public AnimationComponent clone() {
		return new AnimationComponent(this);
	}

}
