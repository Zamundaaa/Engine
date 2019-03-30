package vr.actions;

public class VRAction_Vec2 extends VRAction_Float {

	public VRAction_Vec2(String path) {
		super(path);
	}
	
	public float y() {
		return data.y();
	}
	
	public float deltaY() {
		return data.deltaY();
	}
	
}
