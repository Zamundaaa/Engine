package vr.controllerstuff;

import org.joml.Vector3f;

import vr.VRController;

public interface VRGrabbable {

	// does this need more?

	public void pickedUp(VRController c);

	public boolean update(VRController c);

	public void released(VRController c);

	public boolean containsPoint(float x, float y, float z);

	public boolean containsPoint(Vector3f p);

	public Vector3f position();
	
	public boolean exactPicking();

}
