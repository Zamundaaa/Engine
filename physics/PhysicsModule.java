package physics;

import org.joml.Vector3f;

import generic.Thing;
import generic.UpdatedThing;
import genericRendering.MasterRenderer;
import tools.misc.Vects;
import tools.raytracing.GenericRaytracer;
import tools.raytracing.GenericRaytracer.GoalDescriptorF;

public class PhysicsModule implements UpdatedThing {

	protected Thing t;
	// TODO replace gravity vector by method. Or do both. Get gravity vector each
	// frame anew, in case it would change. If the interface instance is null, don't
	// change the gravity vector and use it as given
	protected Vector3f gravity = new Vector3f(0, -9.81f, 0);
	protected Vector3f velocity = new Vector3f();
	protected GoalDescriptorF collision;

	public PhysicsModule() {

	}

	public PhysicsModule(Thing t) {
		this.t = t;
	}

	public void setThing(Thing t) {
		this.t = t;
		velocity.set(0);
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
		return false;
	}

	@Override
	public boolean updateServer(float frameTimeSeconds) {
		if (t != null) {
			Vector3f v = Vects.calcVect();
			velocity.add(v.set(gravity).mul(frameTimeSeconds));
			if (collision != null) {
				int ret = GenericRaytracer.find(t.position(), velocity, 0.1f, 1, collision, v);
				if (ret == -1) {
					t.position().add(v.set(velocity).mul(frameTimeSeconds));
				} else {
					velocity.set(0);
				}
			} else {
				t.position().add(v.set(velocity).mul(frameTimeSeconds));
			}
		}
		return false;
	}

}
