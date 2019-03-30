package vr;

import static org.lwjgl.openvr.VR.k_ulInvalidInputValueHandle;
import static org.lwjgl.openvr.VRInput.*;
import static vr.VRHandler.lBuff;

import org.lwjgl.openvr.VR;

import generic.Thing;
import tools.AppFolder;
import tools.Meth;
import vr.actions.*;

public class VRController extends TrackedDevice {

	// TODO make the use of RenderModels possible. Loads the model and texture into
	// arrays that can be instantly used and are possible to animate!

	protected long inputSourceHandle, poseActionHandle, vibratationActionHandle;

	protected VRPlayer player;
	protected boolean left = true, active = false;

	// TODO remove this?
	protected Thing held;
	protected boolean holdType = true;

	public VRAction_Pose poseAction;
	public VRAction_Bool grab;
	public VRAction_Bool action;
	public VRAction_Float trigger;
	public VRAction_Vec2 touchpadPos;

	/**
	 * loads all of the default actions
	 */
	public VRController(int trackedDeviceID, boolean left) {
		this(trackedDeviceID, left, true);
	}

	public VRController(int trackedDeviceID, boolean left, boolean defaultActions) {
		super(trackedDeviceID, null);
		this.left = left;
		if (defaultActions) {
			lBuff.clear();
			VRInput_GetInputSourceHandle("/user/hand/right", lBuff);
			inputSourceHandle = lBuff.get(0);
			VRInput_GetActionHandle("/actions/xengine/out/vibration_" + (left ? "left" : "right"), lBuff);
			vibratationActionHandle = lBuff.get(0);
			poseAction = new VRAction_Pose("/actions/xengine/in/hand_pose_" + (left ? "left" : "right"));
			if (poseAction.handle == k_ulInvalidInputValueHandle) {
				poseAction = null;
				AppFolder.log.println((left ? "left" : "right") + " pose action is invalid...");
			} else {
				super.pose = poseAction.getPose();
			}
			trigger = new VRAction_Float("/actions/xengine/in/trigger_" + (left ? "left" : "right"));
			if (trigger.handle == k_ulInvalidInputValueHandle)
				trigger = null;
			touchpadPos = new VRAction_Vec2("/actions/xengine/in/touchpad_" + (left ? "left" : "right"));
			if (touchpadPos.handle == k_ulInvalidInputValueHandle)
				touchpadPos = null;
			action = new VRAction_Bool("/actions/xengine/in/hand_action_" + (left ? "left" : "right"));
			if (action.handle == k_ulInvalidInputValueHandle)
				action = null;
			grab = new VRAction_Bool("/actions/xengine/in/grab_" + (left ? "left" : "right"));
			if (grab.handle == k_ulInvalidInputValueHandle)
				grab = null;
		}
	}

	protected int error = 0;

	@Override
	public void update() {
		if (inputSourceHandle != 0 && active) {
//			int error1 = VRInput_GetPoseActionData(poseActionHandle, ETrackingUniverseOrigin_TrackingUniverseStanding,
//					VRHandler.getSecondsTillPhotons(), poseActionDataBuffer, 0);
			if (poseAction != null)
				poseAction.update();

			super.update();
			rotation.rotateX(-Meth.PI / 2);

			if (trigger != null) {
				trigger.update();
			}
			if (touchpadPos != null) {
				touchpadPos.update();
			}
			if (action != null) {
				action.update();
			}
			if (grab != null) {
				grab.update();
			}
			if (held != null) {
				if (grab != null) {
					if (holdType) {
						if (!grab.state())
							releaseGrabbed();
					} else {
						if (grab.change() && grab.state())
							releaseGrabbed();
					}
				}
			}
		}
	}

	/**
	 * @param time seconds
	 */
	public void simpleVibrate(float time) {
		vibrate(time, 50, 0.2f);
	}

	protected int vibrError = 0;

	/**
	 * @param time      seconds
	 * @param frequency Hz
	 * @param amplitude 0-1 ?
	 */
	public void vibrate(float time, float frequency, float amplitude) {
		int error = VRInput_TriggerHapticVibrationAction(vibratationActionHandle, 0, time, frequency, amplitude,
				VR.k_ulInvalidInputValueHandle);
		if (vibrError != error) {
			vibrError = error;
			AppFolder.log.println("vibration returned " + parseError(vibrError));
		}
	}

	public void setHeld(Thing m) {
		releaseGrabbed();
		held = m;
		// funktioniert(e) irgendwie nicht?
		rotation.transform(held.position().sub(position));
		held.doPhysics = false;
		held.setParent(this);
	}

	public void releaseGrabbed() {
		if (held != null) {
			held.position().set(held.absolutePosition());
			held.setParent(null);
			held.rotation().set(rotation);
			// velocity muss mit rotation transformiert werden, sonst hat man die
			// Winkelgeschwindigkeit nicht mit drin!
			rotation.transform(held.velocity().set(vel));
			held.setAngularVelocity(eulerAngVel);
			held.doPhysics = true;
		}
		held = null;
	}

	/**
	 * @param keepHolding if you have to hold the button/trigger/whatever (true) to
	 *                    not let it fall or if you have to press to release
	 *                    (false). Only works on the integrated
	 *                    "/actions/xengine/in/grab" default action!
	 */
	public void setHoldType(boolean keepHolding) {
		holdType = keepHolding;
	}

	public Thing getHeld() {
		return held;
	}

	public VRPlayer player() {
		return player;
	}

	public void set(Thing t) {
		if (t != null) {
			t.setPosition(position);
			t.setRotation(rotation);
		}
	}

	public boolean active() {
		return active;
	}

	protected void setActive(boolean b) {
		active = b;
	}

	@Override
	public void cleanUp() {
		if (poseAction != null)
			poseAction.cleanUp();
	}

	public static String parseError(int error) {
		switch (error) {
		case VR.EVRInputError_VRInputError_None:
			return null;
		case VR.EVRInputError_VRInputError_NameNotFound:
			return "name not found";
		case VR.EVRInputError_VRInputError_WrongType:
			return "wrong type";
		case VR.EVRInputError_VRInputError_InvalidHandle:
			return "invalid handle";
		case VR.EVRInputError_VRInputError_InvalidParam:
			return "invalid parameter";
		case VR.EVRInputError_VRInputError_NoData:
			return "no data";
		case VR.EVRInputError_VRInputError_BufferTooSmall:
			return "buffer too small";
		default:
			return "unknown error. ID: " + error;
		}
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return !left;
	}
}
