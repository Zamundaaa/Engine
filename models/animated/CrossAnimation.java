package models.animated;

import generic.Transform;

/**
 * 
 * max amount of bones: 50 (for now, perhaps possible expansion later)
 * 
 * @author xaver
 *
 */
public class CrossAnimation extends Animation {

	protected Transform[] calc, calc2;
	private Animation goalAnim;

	public CrossAnimation() {
		this(1, "crossAnim");
	}

	public CrossAnimation(float length, String name) {
		super(new KeyFrame[2], length, name);
		calc = new Transform[50];
		calc2 = new Transform[calc.length];
		for (int i = 0; i < calc.length; i++) {
			calc[i] = new Transform();
			calc2[i] = new Transform();
		}
	}

	public CrossAnimation(Transform[] startPose, Animation goalAnim, float length, Bone[] bones) {
		this(length, "->" + goalAnim.name);
		set(startPose, goalAnim, length, bones);
		repeat = false;
	}

	public CrossAnimation(Animation startAnim, Animation goalAnim, float length, Bone[] bones) {
		this(length, startAnim.name + "->" + goalAnim.name);
		set(startAnim, goalAnim, length, bones);
		repeat = false;
	}

	public CrossAnimation set(Animation startAnim, Animation goalAnim, float length, Bone[] bones) {
		Transform[] t1, t2;
		if (startAnim == null || startAnim.keyframes.length == 0) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc[b.boneID].set(b.offsetmat);
			}
			t1 = calc;
		} else {
			t1 = startAnim.calculateCurrentPose(calc);
		}
		if (goalAnim == null || goalAnim.keyframes.length == 0) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc2[b.boneID].set(b.offsetmat);
			}
			t2 = calc2;
		} else {
			t2 = goalAnim.keyframes[0].pose;
		}
		this.setGoalAnim(goalAnim);
		return set(t1, t2, length);
	}

	public CrossAnimation set(Transform[] startPose, Animation goalAnim, float length, Bone[] bones) {
		Transform[] t1, t2;
		if (startPose == null) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc[b.boneID].set(b.offsetmat);
			}
			t1 = calc;
		} else {
			t1 = startPose;
		}
		if (goalAnim == null || goalAnim.keyframes.length == 0) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc2[b.boneID].set(b.offsetmat);
			}
			t2 = calc2;
		} else {
			t2 = goalAnim.keyframes[0].pose;
		}
		this.setGoalAnim(goalAnim);
		return set(t1, t2, length);
	}

	public CrossAnimation set(Animation startAnim, Transform[] goalPose, float length, Bone[] bones) {
		Transform[] t1, t2;
		if (startAnim == null || startAnim.keyframes.length == 0) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc[b.boneID].set(b.offsetmat);
			}
			t1 = calc;
		} else {
			t1 = startAnim.calculateCurrentPose(calc);
		}
		if (goalPose == null) {
			for (int i = 0; i < bones.length; i++) {
				Bone b = bones[i];
				calc2[b.boneID].set(b.offsetmat);
			}
			t2 = calc2;
		} else {
			t2 = goalPose;
		}
		this.setGoalAnim(null);
		return set(t1, t2, length);
	}

	/**
	 * @param start  may not be null
	 * @param end    may not be null
	 * @param length
	 * @return
	 */
	public CrossAnimation set(Transform[] start, Transform[] end, float length) {
		this.length = length;
		this.playtime = 0;
		keyframes[0] = new KeyFrame(0, start);
		keyframes[1] = new KeyFrame(length, end);
		return this;
	}

	public Animation goalAnimation() {
		return goalAnim();
	}

	public Animation goalAnim() {
		return goalAnim;
	}

	public void setGoalAnim(Animation goalAnim) {
		this.goalAnim = goalAnim;
	}

}
