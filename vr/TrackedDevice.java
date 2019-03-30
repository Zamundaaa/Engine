package vr;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.*;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.TrackedDevicePose;

import generic.Thing;
import vr.actions.VRAction;

public abstract class TrackedDevice extends Thing {

	protected int trackedDeviceID;
	protected TrackedDevicePose pose;
	protected Matrix4f rawToTransMat = new Matrix4f();
	protected Matrix4f rawTransMat = new Matrix4f(), transMat = new Matrix4f();
	protected Vector3f rawPos = new Vector3f(), rawVel = new Vector3f(), rawAngVel = new Vector3f(),
			calcVect = new Vector3f();
	protected Vector3f vel = new Vector3f(), eulerAngVel = new Vector3f(), ray = new Vector3f();
	protected Quaternionf rawRot = new Quaternionf();
	protected Quaternionf rawToRot = new Quaternionf();
	protected Thing model;
	protected ArrayList<VRAction> actions = new ArrayList<>();

	public TrackedDevice(int trackedDeviceID, TrackedDevicePose pose) {
		this.trackedDeviceID = trackedDeviceID;
		this.pose = pose;
	}

	public void update() {
		if (pose != null) {
			FloatBuffer v = pose.vAngularVelocity().v();
			rawAngVel.set(v.get(0), v.get(1), v.get(2));
			v = pose.vVelocity().v();
			rawVel.set(v.get(0), v.get(1), v.get(2));
			HmdMatrix34 m = pose.mDeviceToAbsoluteTracking();
			VRUtils.convertHmdMatrixToMatrix4f(m, rawTransMat);
			rawTransMat.transformPosition(rawPos.set(0));
			rawTransMat.getNormalizedRotation(rawRot);

			rawToTransMat.transformDirection(rawVel, vel);
			rawToTransMat.getNormalizedRotation(rawToRot);

			rawToRot.transform(rawAngVel, eulerAngVel);
			angularVelocity.identity().rotateXYZ(eulerAngVel.x, eulerAngVel.y, eulerAngVel.z);

			rawToTransMat.mul(rawTransMat, transMat);
			transMat.transformPosition(position.set(0));
			transMat.getNormalizedRotation(rotation);

//			if (model != null) {
//				model.setPosition(pos);
//				model.setRotation(rot);
//			}

			calculateRay(ray);
		}

	}

	public int trackedDeviceID() {
		return trackedDeviceID;
	}

	public Matrix4f getRawToTransMat() {
		return rawToTransMat;
	}

	public Matrix4f getTransMat() {
		return transMat;
	}

	public Vector3f velocity() {
		return vel;
	}

	public Vector3f eulerAngularVelocity() {
		return eulerAngVel;
	}

	public Vector3f rawPosition() {
		return rawPos;
	}

	public Vector3f calculateRay(Vector3f dest) {
		return rotation.transform(dest.set(0, 0, -1));
	}

	public Vector3f ray() {
		return ray;
	}

	public Thing model() {
		return model;
	}

	public void setModel(Thing m) {
		this.model = m;
		m.setParent(this);
	}

//	private void calcRotation(HmdMatrix34 m) {
//		// jumps when crossing 'horizontal line' / at a certain point
//		Quaternionf q = rot;
//		q.w = (float) (sqrt(max(0, 1 + get(m, 0, 0) + get(m, 1, 1) + get(m, 2, 2))) / 2f);
//		q.x = (float) (sqrt(max(0, 1 + get(m, 0, 0) - get(m, 1, 1) - get(m, 2, 2))) / 2f);
//		q.y = (float) (sqrt(max(0, 1 - get(m, 0, 0) + get(m, 1, 1) - get(m, 2, 2))) / 2f);
//		q.z = (float) (sqrt(max(0, 1 - get(m, 0, 0) - get(m, 1, 1) + get(m, 2, 2))) / 2f);
//		q.x = copysign(q.x, get(m, 2, 1) - get(m, 1, 2));
//		q.y = copysign(q.y, get(m, 0, 2) - get(m, 2, 0));
//		q.z = copysign(q.z, get(m, 1, 0) - get(m, 0, 1));
//		rot.rotateLocalX(Meth.PI2);
//	}

//	private float copysign(float x, float s) {
//		if ((x < 0 && s >= 0) || (x > 0 && s < 0))
//			return -x;
//		else
//			return x;
//	}

}

//private void setTransmat(HmdMatrix34 m) {
//		// with this z does work! but not xy
//		int i = 0;
//		transMat.m00(m.m(i * 3));
//		transMat.m01(m.m(i * 3 + 1));
//		transMat.m02(m.m(3 * i + 2));
//		transMat.m03(0);
//		i++;
//		transMat.m10(m.m(0 + i * 3));// x
//		transMat.m11(m.m(1 + i * 3));
//		transMat.m21(m.m(2 + i * 3));
//		transMat.m13(0);
//		i++;
//		transMat.m02(m.m(0 + i * 3));
//		transMat.m21(m.m(1 + i * 3));// y
//		transMat.m22(m.m(2 + i * 3));
//		transMat.m23(0);
//		i++;
//		transMat.m30(m.m(0 + i * 3));// should be: x
//		transMat.m31(m.m(1 + i * 3));// should be: y
//		transMat.m32(m.m(2 + i * 3));// z
//		transMat.m33(1.0f);
//
//		// doesn't really work! e.g. Pos (but x) is messed up
//		int i = 0;
//		transMat.m00(m.m(0));
//		transMat.m01(m.m(3));//x
//		transMat.m02(m.m(3 * 2));
//		transMat.m03(0);
//		i++;
//		transMat.m10(m.m(0 + i));
//		transMat.m11(m.m(3 + i));
//		transMat.m21(m.m(3 * 2 + i));// y
//		transMat.m13(0);
//		i++;
//		transMat.m02(m.m(0 + i));
//		transMat.m21(m.m(3 + i));
//		transMat.m22(m.m(3 * 2 + i));
//		transMat.m23(0);
//		i++;
//		transMat.m30(m.m(0 + i));
//		transMat.m31(m.m(3 + i));
//		transMat.m32(m.m(3 * 2 + i));// z
//		transMat.m33(1.0f);
//}
