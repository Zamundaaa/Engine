package models.animated;

import generic.Transform;

public class KeyFrame {

	protected final float timeStamp;
	protected final Transform[] pose;

	/**
	 * poses have to be in model-space!
	 */
	public KeyFrame(float timeStamp, Transform[] pose) {
		this.pose = pose;
		this.timeStamp = timeStamp;
	}

	public Transform[] getPose() {
		return pose;
	}

	public float getTimeStamp() {
		return timeStamp;
	}

}
