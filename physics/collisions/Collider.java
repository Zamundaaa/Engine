package physics.collisions;

import org.joml.Vector3f;

import generic.Savable;
import generic.Thing;

public abstract class Collider extends Savable {

	protected Thing parent;

	public Collider(Thing parent) {
		super();
		this.parent = parent;
	}

	public abstract boolean inBody(float x, float y, float z);

	public abstract boolean intersects(Collider two);

	public abstract boolean intersectsSphere(SphereHitbox s);

	public boolean inBody(Vector3f pos) {
		return inBody(pos.x, pos.y, pos.z);
	}

}
