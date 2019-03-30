package models.components;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import generic.Thing;
import generic.UpdatedThing;
import genericRendering.MasterRenderer;

public class PhysicsComponent implements UpdatedThing {

	public Thing parent;
	public Vector3f velocity = new Vector3f();
	public Quaternionf angularVelocity = new Quaternionf();

	public PhysicsComponent(Thing m) {
		this.parent = m;
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
		return false;
	}

	@Override
	public boolean updateServer(float frameTimeSeconds) {
//		if (doPhysics) {
//			Vector3f s = transformedSize(Vects.calcVect());
//			Vector3f m = transformedMiddle(Vects.calcVect2());
//			if (position.y + m.y - s.y * 0.5f <= groundHeight && velocity.y <= 0) {
//				position.y = groundHeight - m.y + s.y * 0.5f;
//			} else {
//				velocity.y += gravity * frameTimeSeconds;
//				// bounce
////					if (position.y < groundHeight && velocity.y < 0) {position.y = groundHeight;if (velocity.y > -0.1f) {	velocity.y = 0;	} else {velocity.y *= -0.2f;}}
//			}
//			if (position.y + m.y - s.y * 0.5f <= groundHeight && velocity.y <= 0) {
//				float f = Math.max(0, 1 - frameTimeSeconds * 7);
//				velocity.x *= f;
//				if (abs(velocity.x) < 0.01f)
//					velocity.x = 0;
//				velocity.z *= f;
//				if (abs(velocity.z) < 0.01f)
//					velocity.z = 0;
//				velocity.y = 0;
//
//				if (f > 0)
//					angularVelocity.nlerp(Vects.identityQuat, f);
//				else
//					angularVelocity.identity();
//
//				// box collision to ground. Not actually this simple but looks cool as it is.
//				// FIXME actually consider the center of mass, not just directions. This works
//				// for cubes & nothing more!
//				// -> plan:
//				// center of mass for now: m
//				// if this works, perhaps make a guess by averaging vertices.
//				// the side this actually has to fall on is the one where the center of mass is
//				// over it.
//				// next up: this on rotated planes. Should just be DOWN -> normal.
//				// next next up: Collision one box -> another box. Broad phase filters.
//
//				Vector3f nearest = rotateAbsolute(Vects.calcVect().set(-1, 0, 0));
//				float n = nearest.y;
//				Vector3f c = rotateAbsolute(Vects.calcVect2().set(1, 0, 0));
//				float d = c.y;
//				if (d < n) {
//					n = d;
//					nearest.set(c);
//				}
//				for (int y = -1; y <= 1; y += 2) {
//					rotateAbsolute(c.set(0, y, 0));
//					d = c.y;
//					if (d < n) {
//						n = d;
//						nearest.set(c);
//					}
//				}
//				for (int z = -1; z <= 1; z += 2) {
//					rotateAbsolute(c.set(0, 0, z));
//					d = c.y;
//					if (d < n) {
//						n = d;
//						nearest.set(c);
//					}
//				}
//				if (nearest.y > -0.9999f) {
//					Vects.quat.identity().rotateTo(nearest, Vects.DOWN);
//					if (Vects.quat.lengthSquared() < 1)
//						Vects.quat.nlerp(Vects.identityQuat, Math.max(0, 1 - 50 * frameTimeSeconds));
//					Vects.quat.mul(rotation, rotation);
//				}
//			}
//		}
//		position.add(velocity.x * frameTimeSeconds, velocity.y * frameTimeSeconds, velocity.z * frameTimeSeconds);
//		rotation.mul(Vects.quat.set(angularVelocity).nlerp(Vects.identityQuat, 1 - frameTimeSeconds));
		return false;
	}

}
