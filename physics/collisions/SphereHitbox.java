package physics.collisions;

import generic.Thing;

public class SphereHitbox extends Collider {

	public SphereHitbox(Thing parent) {
		super(parent);
	}

	@Override
	public boolean inBody(float x, float y, float z) {
		return parent.position().distanceSquared(x, y, z) < parent.scaledHeight() * parent.scaledWidth();
	}

	@Override
	public boolean intersects(Collider two) {
		// you can optimize this away by calling other.intersectsSphere(this)! Is vastly
		// better for performance and also vastly more versatile and code-efficient!
		return two.intersectsSphere(this);
	}

	@Override
	public boolean intersectsSphere(SphereHitbox s) {
		float r = parent.scaledHeight() + s.parent.scaledHeight();
		r *= 0.5f;
		return parent.position().distanceSquared(s.parent.position()) < r * r;
	}

}
