package vr;

import java.util.ArrayList;
import java.util.List;

import org.joml.*;

import generic.Thing;
import graphics3D.Camera;
import graphics3D.terrain.ProceduralTerrainGenerator;
import vr.controllerstuff.VRControllerInit;
import vr.controllerstuff.VRGrabbable;

public class VRPlayer extends Thing {

	protected Matrix4f translationMatrix = new Matrix4f();
	protected Quaternionf rotation = new Quaternionf();
	protected HMD hmd;
	protected Vector3f velocity = new Vector3f();
	protected VRController left, right;
	protected List<VRGrabbable> addons = new ArrayList<>();
	protected List<VRControllerInit> inits = new ArrayList<>();

	public VRPlayer(HMD hmd, VRController left, VRController right) {
		this.hmd = hmd;
		this.left = left;
		this.right = right;
	}

	public void setLeftController(VRController c) {
		if (left != c) {
			left = c;
			for (int i = 0; i < inits.size(); i++) {
				inits.get(i).initCtrl(c);
			}
		}
	}

	public void setRightController(VRController c) {
		if (right != c) {
			right = c;
			for (int i = 0; i < inits.size(); i++) {
				inits.get(i).initCtrl(c);
			}
		}
	}

	public void update(float frameTimeSeconds) {
		position.add(velocity.x * frameTimeSeconds, velocity.y * frameTimeSeconds, velocity.z * frameTimeSeconds);
		translationMatrix.translation(position).rotate(rotation);
		// null checks just for safety
		if (hmd != null) {
			hmd.rawToTransMat.set(translationMatrix);
			hmd.update();
		}
		if (left != null) {
			left.rawToTransMat.set(translationMatrix);
			left.update();
			if (left.active)
				for (int i = 0; i < addons.size(); i++)
					addons.get(i).update(left);
		}
		if (right != null) {
			right.rawToTransMat.set(translationMatrix);
			right.update();
			if (right.active) {
				for (int i = 0; i < addons.size(); i++)
					addons.get(i).update(right);
			}
		}
	}

	public void updatePosition(ProceduralTerrainGenerator t, float frameTimeSeconds) {
		float h = t.heightAt(position.x + hmd.rawPos.x, position.z + hmd.rawPos.z);
		if (position.y <= h && velocity.y <= 0) {// GRAVITY?
			position.y = h;
			velocity.y = 0;
			velocity.x *= 0.99f - frameTimeSeconds;
			velocity.z *= 0.99f - frameTimeSeconds;
		} else {
			velocity.y += gravity * frameTimeSeconds;
		}
	}

	public void set(Camera c, int eye, Matrix4f projMat) {
		hmd.set(c, eye, projMat);
//		c.viewMat().mul(translationMatrix, c.viewMat());
	}

	public Quaternionf rotation() {
		return rotation;
	}

	public VRController left() {
		return left;
	}

	public VRController right() {
		return right;
	}

	public Vector3f velocity() {
		return velocity;
	}

	public HMD hmd() {
		return hmd;
	}

	public void setHMD(HMD hmd) {
		this.hmd = hmd;
	}

	public void addControllerAddon(VRGrabbable v) {
		addons.add(v);
	}

	public void addControllerInit(VRControllerInit v) {
		inits.add(v);
	}

}
