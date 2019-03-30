package vr.controllerstuff;

import java.util.List;

import org.joml.Vector3f;

import generic.Thing;
import vr.VRController;

/**
 * you should probably make a subclass of this and attach 2 models (left &
 * right). Don't forget to react on picking stuff up! You can also use the
 * {@link ToolSelector#init()} method for this in case of an anonymous class
 * 
 * @author xaver
 *
 */
public class ToolSelector implements VRGrabbable {

	public interface ListGetter {
		public List<Thing> getTools();
	}

	protected ListGetter listgetter;
	private VRController lastLeft, lastRight;
	protected VRGrabbable left;
	protected boolean leftReleaseAllowed = false;
	protected VRGrabbable right;
	protected boolean rightReleaseAllowed = false;

	/*
	 * you should probably make a subclass of this and attach 2 models (left &
	 * right). Don't forget to react on picking them up! You can also use the {@link
	 * ToolSelector#init()} method for this in case of an anonymous class
	 */
	public ToolSelector(ListGetter l) {
		this.listgetter = l;
		init();
	}

	public ListGetter getListGetter() {
		return listgetter;
	}

	public void setListGetter(ListGetter g) {
		this.listgetter = g;
	}

	public VRGrabbable left() {
		return left;
	}

	public VRGrabbable right() {
		return right;
	}

	protected void init() {

	}

	public void setLeft(VRGrabbable t) {
		if (lastLeft != null) {
			if (left != null && lastLeft != null)
				left.released(lastLeft);
			left = t;
			if (t != null)
				t.pickedUp(lastLeft);
			lastLeft.simpleVibrate(0.1f);
//			leftReleaseAllowed = true;
		}
	}

	public void setRight(VRGrabbable t) {
		if (lastRight != null) {
			if (right != null)
				right.released(lastRight);
			right = t;
			if (t != null)
				t.pickedUp(lastRight);
			lastRight.simpleVibrate(0.1f);
//			rightReleaseAllowed = true;
		}
	}

	@Override
	public boolean update(VRController c) {
		if (c.isLeft()) {
			lastLeft = c;
			if (left != null)
				left.update(c);
			if (c.grab.change()) {
				c.simpleVibrate(0.1f);
				if (left != null) {
					if (c.grab.state() == false) {
						if (leftReleaseAllowed) {
							left.released(c);
							left = null;
						} else {
							leftReleaseAllowed = true;
						}
					}
				} else {
					if (c.grab.state() == true) {
						VRGrabbable nearest = getNearest(c);
						if (nearest != null) {
							left = nearest;
							left.pickedUp(c);
						}
						leftReleaseAllowed = false;
					}
				}
			}
		} else {
			lastRight = c;
			if (right != null)
				right.update(c);
			if (c.grab.change()) {
				c.simpleVibrate(0.1f);
				if (right != null) {
					if (c.grab.state() == false) {
						if (rightReleaseAllowed) {
							right.released(c);
							right = null;
						} else {
							rightReleaseAllowed = true;
						}
					}
				} else {
					if (c.grab.state()) {
						VRGrabbable nearest = getNearest(c);
						if (nearest != null) {
							right = nearest;
							right.pickedUp(c);
						}
						rightReleaseAllowed = false;
					}
				}
			}
		}
		return false;
	}

	private VRGrabbable getNearest(VRController c) {
		List<Thing> l = listgetter.getTools();
		VRGrabbable nearest = null;
		float mindist = Float.POSITIVE_INFINITY;
		for (int i = 0; i < l.size(); i++) {
			VRGrabbable t = l.get(i);
			float d = Float.POSITIVE_INFINITY;
//			if (t.model() != null && t.model().containsPoint(c.position()) && (nearest == null
//					|| (d = t.model().position().distanceSquared(c.position())) < mindist)) {
			boolean possible = checkForPickStatus(t, c);
			if (possible && (nearest == null || (d < Float.POSITIVE_INFINITY ? d
					: (d = t.position().distanceSquared(c.position()))) < mindist)) {
				if (nearest == null) {
					d = t.position().distanceSquared(c.position());
				}
				nearest = t;
				mindist = d;
			}
		}
		return nearest;
	}

	protected boolean checkForPickStatus(VRGrabbable v, VRController c) {
		boolean possible = v.containsPoint(c.position());
		if (!possible) {
			possible = v.position().distanceSquared(c.position()) < 0.3f;
		}
		return possible;
	}

	@Override
	public void pickedUp(VRController c) {

	}

	@Override
	public void released(VRController c) {

	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return false;
	}

	@Override
	public boolean containsPoint(Vector3f p) {
		return false;
	}

	@Override
	public Vector3f position() {
		return null;
	}

	@Override
	public boolean exactPicking() {
		return false;
	}

}
