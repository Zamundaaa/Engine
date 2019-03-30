package vr;

import org.joml.Matrix4f;
import org.lwjgl.openvr.HmdMatrix34;

public class VRUtils {

	public static void convertHmdMatrixToMatrix4f(HmdMatrix34 m, Matrix4f transMat) {
		transMat.m00(get(m, 0, 0));
		transMat.m10(get(m, 0, 1));
		transMat.m20(get(m, 0, 2));
		transMat.m30(get(m, 0, 3));
		transMat.m01(get(m, 1, 0));
		transMat.m11(get(m, 1, 1));
		transMat.m21(get(m, 1, 2));
		transMat.m31(get(m, 1, 3));
		transMat.m02(get(m, 2, 0));
		transMat.m12(get(m, 2, 1));
		transMat.m22(get(m, 2, 2));
		transMat.m32(get(m, 2, 3));
		transMat.m03(0);
		transMat.m13(0);
		transMat.m23(0);
		transMat.m33(1);
	}

	/**
	 * @param i first array index
	 * @param j second array index
	 * @return the value at 'm.m[i][j]'
	 */
	public static float get(HmdMatrix34 m, int i, int j) {
		return m.m(i * 4 + j);
	}

	public static String steamLocation() {
		return "~/.local/share/Steam/";
	}

	public static String vr_glove_left_model() {
		return steamLocation() + "steamapps/common/SteamVR/resources/rendermodels/vr_glove/vr_glove_left_model.fbx";
	}

	public static String vr_glove_right_model() {
		return steamLocation() + "steamapps/common/SteamVR/resources/rendermodels/vr_glove/vr_glove_right_model.fbx";
	}

}
