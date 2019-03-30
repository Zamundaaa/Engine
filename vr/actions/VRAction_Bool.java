package vr.actions;

import static org.lwjgl.openvr.VRInput.VRInput_GetDigitalActionData;

import org.lwjgl.openvr.InputDigitalActionData;

import tools.AppFolder;
import vr.VRController;

public class VRAction_Bool extends VRAction {

	protected InputDigitalActionData data;
	protected int error = 0;

	public VRAction_Bool(String path) {
		super(path);
		data = InputDigitalActionData.create();
	}

	public void update() {
//		if (!actionActive()) {
//			if (error != -1) {
//				error = -1;
//				AppFolder.log.println("Digital Action '" + path + "' not active!");
//			}
//		} else {
			int error1 = VRInput_GetDigitalActionData(handle, data, 0);
			if (error != error1) {
				AppFolder.log.println(
						"Digital Action '" + path + "' " + VRController.parseError(error = error1) + ", " + handle);
			}
//		}
	}

//	public boolean actionActive() {
//		return data.bActive();
//	}
	
	public boolean state() {
		return data.bState();
	}

	public boolean change() {
		return data.bChanged();
	}

	public float fUpdateTime() {
		return data.fUpdateTime();
	}

	/**
	 * @return if the state of this digital action changed to stateChangedTo
	 */
	public boolean change(boolean stateChangedTo) {
		return change() && state() == stateChangedTo;
	}

}
