package models.animated;

import java.util.List;

import org.joml.Matrix4f;

import generic.Transform;

public class Animation {

	private static final KeyFrame[] conversionArray = new KeyFrame[0];

	public final String name;
	protected final KeyFrame[] keyframes;
	protected boolean repeat = true;
	protected float length;
	protected float playtime = 0;
	protected long lastUpdate = System.currentTimeMillis();

	public Animation(KeyFrame[] transforms, float length, String name) {
		this.keyframes = transforms;
		this.length = length;
		this.name = name;
	}

	public Animation(List<KeyFrame> transforms, float length, String name) {
		this.keyframes = transforms.toArray(conversionArray);
		this.length = length;
		this.name = name;
	}

	public KeyFrame[] getKeyFrames() {
		return keyframes;
	}

	public float length() {
		return length;
	}

	public void start() {
		playtime = 0;
	}

	public boolean update(float t) {
		playtime += t;
		if (playtime > length) {
			if (repeat)
				playtime %= length;
			return true;
		}
		while (playtime < 0)
			playtime += length;
		return false;
	}

	public Matrix4f[] calculateCurrentPose(Matrix4f[] pose, Transform[] calc) {
		if (keyframes.length > 0) {
			KeyFrame last = keyframes[0];
			KeyFrame next = null;
			for (int i = 1; i < keyframes.length; i++) {
				next = keyframes[i];
				if (next.timeStamp > playtime) {
					break;
				} else {
					last = next;
				}
			}
			if (keyframes.length > 1 && last == keyframes[keyframes.length - 1]) {
				next = keyframes[1];
			}
			if (next == null) {
				Transform[] p2 = last.getPose();
				Transform.transform(p2, pose);
			} else if (playtime > next.timeStamp) {
				Transform[] p2 = next.getPose();
				Transform.transform(p2, pose);
			} else {
				Transform.interpolate(last.pose, next.pose,
						(playtime - last.timeStamp) / (next.timeStamp - last.timeStamp), calc, pose);
			}
		}
		return pose;
	}

	public Transform[] calculateCurrentPose(Transform[] calc) {
		if (keyframes.length > 0) {
			KeyFrame last = keyframes[0];
			KeyFrame next = null;
			for (int i = 1; i < keyframes.length; i++) {
				next = keyframes[i];
				if (next.timeStamp > playtime) {
					break;
				} else {
					last = next;
				}
			}
			if (keyframes.length > 1 && last == keyframes[keyframes.length - 1]) {
				next = keyframes[0];
			}
			if (next == null) {
				Transform[] p2 = last.getPose();
				for (int i = 0; i < p2.length && i < calc.length; i++) {
					calc[i].set(p2[i]);
				}
			} else {
				Transform.interpolate(last.pose, next.pose,
						(playtime - last.timeStamp) / (next.timeStamp - last.timeStamp), calc);
			}
		}
		return calc;
	}
	
	@Override
	public Animation clone() {
		return new Animation(keyframes, length, name);
	}

	@Override
	public String toString() {
		return "animation '" + name + "' that is " + length + "s long and has " + keyframes.length + " keyframes";
	}

}
