package vr.actions;

public class VRAction_Vec3 extends VRAction_Vec2 {

	public VRAction_Vec3(String path) {
		super(path);
	}
	
	public float z() {
		return data.z();
	}
	
	public float deltaZ() {
		return data.deltaZ();
	}
	
}
