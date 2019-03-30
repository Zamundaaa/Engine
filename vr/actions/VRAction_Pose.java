package vr.actions;

import static org.lwjgl.openvr.VR.ETrackingUniverseOrigin_TrackingUniverseStanding;
import static org.lwjgl.openvr.VRInput.VRInput_GetPoseActionData;

import org.lwjgl.openvr.InputPoseActionData;
import org.lwjgl.openvr.TrackedDevicePose;

import tools.AppFolder;
import vr.VRController;

public class VRAction_Pose extends VRAction {

	protected InputPoseActionData poseActionData;
	protected int error = 0;

	public VRAction_Pose(String path) {
		super(path);
		poseActionData = InputPoseActionData.create();
	}

	public TrackedDevicePose getPose() {
		return poseActionData.pose();
	}

	public void update(float timeToNextFrame) {
//		if (!actionActive()) {
//			if (error != -1) {
//				error = -1;
//				AppFolder.log.println("Pose Action '" + path + "' not active!");
//			}
//		} else {
		int error1 = VRInput_GetPoseActionData(handle, ETrackingUniverseOrigin_TrackingUniverseStanding,
				timeToNextFrame, poseActionData, 0);
		if (error != error1) {
			AppFolder.log
					.println("Pose Action '" + path + "' returned error '" + VRController.parseError(error = error1)
							+ "', " + handle + ", " + poseActionData.activeOrigin());
		}
//		}
	}

	public void update() {
//		if (!actionActive()) {
//			if (error != -1) {
//				error = -1;
//				AppFolder.log.println("Pose Action '" + path + "' not active!");
//			}
//		} else {
		int error1 = VRInput_GetPoseActionData(handle, ETrackingUniverseOrigin_TrackingUniverseStanding, 0,
				poseActionData, 0);
		if (error != error1) {
			// immer "Pose Action '/actions/xengine/in/hand_pose_right' wrong type"
			AppFolder.log
					.println("Pose Action '" + path + "' returned error '" + VRController.parseError(error = error1)
							+ "', " + handle + ", " + poseActionData.activeOrigin());
		}
//		}
	}

//	public boolean actionActive() {
//		return poseActionData.bActive();
//	}

	public void cleanUp() {
		poseActionData.close();
	}

}
