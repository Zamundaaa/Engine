package graphics3D;

import java.util.Map;

import org.joml.*;
import org.lwjgl.glfw.GLFW;

import collectionsStuff.SmartByteBuffer;
import generic.Thing;
import tools.misc.Vects;
import window.Window;
import window.input.Mouse;

public class Camera extends Thing {

	protected Quaternionf calcQ = new Quaternionf();
	protected Matrix4f viewMat = new Matrix4f();
	protected Vector3f ray = new Vector3f(0, 0, -1);
	protected FrustumIntersection fi = new FrustumIntersection(viewMat);

	public Camera() {
		this(0, 0, 0);
	}

	public Camera(float x, float y, float z) {
		super(x, y, z);
	}

	public Camera(SmartByteBuffer buff, Map<String, Short> versions) {
		super(buff, versions);
	}

	public void update(Matrix4f projMat) {
		viewMat.identity().rotate(rotation).translate(-position.x, -position.y, -position.z);
		fi.set(Vects.mat4.set(projMat).mul(viewMat));
		updateRay();
	}

	/**
	 * updated in update()
	 */
	public void updateRay() {
		rotation.invert(Vects.quat).transform(ray.set(0, 0, -1));
	}

	public Matrix4f viewMat() {
		return viewMat;
	}

	public Vector3f ray() {
		return ray;
	}

	/**
	 * updated in update(). Can be used to not render stuff if not visible. Don't
	 * use it a lot for a lot of small stuff though as it'll quite possibly destroy
	 * performance more than rendering them
	 */
	public FrustumIntersection fi() {
		return fi;
	}

	public void doEasyControls(Window main) {
		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
			position().add(ray().x * main.frameTimeSeconds() * 5, ray().y * main.frameTimeSeconds() * 5,
					ray().z * main.frameTimeSeconds() * 5);
		}
		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
			position().add(-ray().x * main.frameTimeSeconds() * 5, -ray().y * main.frameTimeSeconds() * 5,
					-ray().z * main.frameTimeSeconds() * 5);
		}

		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
			rotation.invert(Vects.quat).transform(Vects.calcVect.set(-1, 0, 0));
			position().add(Vects.calcVect.x * main.frameTimeSeconds() * 5,
					Vects.calcVect.y * main.frameTimeSeconds() * 5, Vects.calcVect.z * main.frameTimeSeconds() * 5);
		}
		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
			rotation.invert(Vects.quat).transform(Vects.calcVect.set(1, 0, 0));
			position().add(Vects.calcVect.x * main.frameTimeSeconds() * 5,
					Vects.calcVect.y * main.frameTimeSeconds() * 5, Vects.calcVect.z * main.frameTimeSeconds() * 5);
		}
		if (main.mouse().isButtonDown(Mouse.LEFT)) {
			rotation().rotateY(main.mouse().getDX() * 0.003f);
			rotation().rotateLocalX(main.mouse().getDY() * 0.003f);
		} else {
			main.mouse().getDX();
			main.mouse().getDY();
		}

		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			position().y += main.frameTimeSeconds() * 5;
		}
		if (main.keyboard().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			position().y -= main.frameTimeSeconds() * 5;
		}
	}

	@Override
	public Matrix4f rotate(Matrix4f m) {
//		return m.rotateXYZ(rotation.getEulerAnglesXYZ(Vects.calcVect));
		return m.rotate(calcQ.set(rotation).invert());
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
	}

//	public static void main(String[] args) {
//		Camera c = new Camera();
//		c.setPosition(-10, 5, 42);
//		c.update();
//		System.out.println(c.viewMat);
//		c.rotation().rotate(1, 0, 0);
//		c.update();
//		System.out.println(c.viewMat);
//	}

}
