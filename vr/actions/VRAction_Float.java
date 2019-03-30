package vr.actions;

import static org.lwjgl.openvr.VRInput.VRInput_GetAnalogActionData;

import org.lwjgl.openvr.InputAnalogActionData;

import tools.AppFolder;
import vr.VRController;

public class VRAction_Float extends VRAction {

	protected float x;

	protected InputAnalogActionData data;
	protected int error = 0;

	public VRAction_Float(String path) {
		super(path);
		data = InputAnalogActionData.create();
	}

	public void update() {
//		if (!actionActive()) {
//			if (error != -1) {
//				error = -1;
//				AppFolder.log.println("Analog Action '" + path + "' not active!");
//			}
//		} else {
			int error1 = VRInput_GetAnalogActionData(handle, data, 0);
			if (error != error1)
				AppFolder.log.println(
						"Analog Action: '" + path + "' " + VRController.parseError(error = error1) + ", " + handle);
//		}
	}

	public float x() {
		return data.x();
	}

	public float deltaX() {
		return data.deltaX();
	}

	public float fUpdateTime() {
		return data.fUpdateTime();
	}

//	public boolean actionActive() {
//		return data.bActive();
//	}

}
