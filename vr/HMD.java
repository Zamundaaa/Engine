package vr;

import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetEyeToHeadTransform;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.TrackedDevicePose;

import graphics3D.Camera;

public class HMD extends TrackedDevice {

	private static Matrix4f transMat1 = new Matrix4f(), transMat2 = new Matrix4f();
	private static HmdMatrix34 getMat = new HmdMatrix34(ByteBuffer.allocateDirect(HmdMatrix34.SIZEOF));

	public HMD(int trackedDeviceID, TrackedDevicePose pose) {
		super(trackedDeviceID, pose);
	}

	public void set(Camera c, int eye, Matrix4f projMat) {
		Matrix4f m = eye == EVREye_Eye_Left ? getLeftTransMat() : getRightTransMat();
		m.transformPosition(c.position().set(0));
		m.getNormalizedRotation(c.rotation());
		c.rotation().invert();
		c.update(projMat);
	}

	public Matrix4f getLeftTransMat() {
		transMat2.identity();
		VRSystem_GetEyeToHeadTransform(EVREye_Eye_Left, getMat);
		VRUtils.convertHmdMatrixToMatrix4f(getMat, transMat2);
//		transMat2.mul(transMat1.set(transMat));
		transMat1.set(transMat).mul(transMat2);
		return transMat1;
	}

	public Matrix4f getRightTransMat() {
		transMat2.identity();
		VRSystem_GetEyeToHeadTransform(EVREye_Eye_Right, getMat);
		VRUtils.convertHmdMatrixToMatrix4f(getMat, transMat2);
//		transMat2.mul(transMat1.set(transMat));
		transMat1.set(transMat).mul(transMat2);
		return transMat1;
	}

}
