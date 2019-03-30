package vr.actions;

import static org.lwjgl.openvr.VRInput.VRInput_GetActionSetHandle;
import static org.lwjgl.openvr.VRInput.VRInput_UpdateActionState;

import java.nio.LongBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.openvr.VRActiveActionSet;

import tools.AppFolder;
import vr.VRController;

public class VRActionSet {

	public static final LongBuffer getBuff = BufferUtils.createLongBuffer(1);
	public static final ArrayList<VRActionSet> allSets = new ArrayList<>();
//	public static final ArrayList<VRActionSet> activeSets = new ArrayList<>();
	private static VRActiveActionSet.Buffer buff;

	private ArrayList<VRAction> containedActions = new ArrayList<>();
	public final long handle;

	protected VRActionSet(String name) {
		getBuff.clear();
		VRInput_GetActionSetHandle(name, getBuff);
		handle = getBuff.get(0);
		allSets.add(this);
//		activeSets.add(this);
		if (buff == null) {
			buff = VRActiveActionSet.create(1);
			buff.get(0).ulActionSet(handle);
			AppFolder.log.println(
					"actionset '" + name + "' has handle: " + handle + "; buff remaining: " + buff.remaining());
		} else {
			// FIXME! make ArrayListL of all previous action sets and then delete the old
			// buffer, make a new one and add this.handle!
		}
	}

	public ArrayList<VRAction> getActions() {
		return containedActions;
	}

	protected void cleanUp() {
		allSets.remove(this);
//		activeSets.remove(this);
	}

	protected static int error = 0;

	public static void update() {
		if (buff != null) {
			int error1 = VRInput_UpdateActionState(buff, VRActiveActionSet.SIZEOF);
			if (error != error1) {
				AppFolder.log.println("updateActionState returned: " + VRController.parseError(error = error1));
			}
		}
	}

}
