package physics.collisions;

import generic.Thing;

/**
 * Assumes no rotation whatsoevery. Or that scaledWidth etc is made fitting
 * 
 * @author xaver
 *
 */
public class AAABHitbox extends Collider {

	/**
	 * Assumes no rotation whatsoevery. Or that scaledWidth etc is made fitting
	 */
	public AAABHitbox(Thing parent) {
		super(parent);
	}

	@Override
	public boolean inBody(float x, float y, float z) {
		return x > parent.position().x - parent.scaledWidth() * 0.5f
				&& x < parent.position().x + parent.scaledWidth() * 0.5f
				&& y > parent.position().y - parent.scaledHeight() * 0.5f
				&& y < parent.position().y + parent.scaledHeight() * 0.5f
				&& z > parent.position().z - parent.scaledDepth() * 0.5f
				&& z < parent.position().z + parent.scaledDepth() * 0.5f;
	}

	@Override
	public boolean intersects(Collider two) {
		// TODO
		return false;
	}

	@Override
	public boolean intersectsSphere(SphereHitbox s) {
		return false;
	}

}
