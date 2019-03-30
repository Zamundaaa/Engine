package vr.actions;

import static org.lwjgl.openvr.VRInput.VRInput_GetActionHandle;

public class VRAction {

	public final String path;
	public final long handle;

	public VRAction(String path) {
//		AppFolder.log.println("creating VRAction " + path);
		this.path = path;
		VRActionSet.getBuff.clear();
//		int error = 
		VRInput_GetActionHandle(path, VRActionSet.getBuff);
//		if (error != 0)
//			AppFolder.log.println(VRController_IVR.parseError(error));
		handle = VRActionSet.getBuff.get(0);
//		AppFolder.log.println(path + ", " + handle);
	}

}
