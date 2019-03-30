package openGlResources;

import static openGlResources.CommonGL.*;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import window.Window;

public class GLUtils {

	/**
	 * can be used as an indicator for renderers to use WireFrame (see methods
	 * {@link GLUtils#startWireFrame()} and {@link GLUtils#stopWireFrame()}). This
	 * is used so for example the skybox doesn't have to do this, too
	 */
	public static boolean doWireframe;

	public static void enableTranslucency() {
		glEnable(GL11.GL_BLEND);
		glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableTranslucency() {
		glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA,
				GL11.GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL11.GL_BLEND);
	}

	public static void enableCulling() {
		glEnable(GL11.GL_CULL_FACE);
		glCullFace(GL11.GL_BACK);
		// GL11.glEnable(GL11.GL_BACK);
	}

	public static void enableFrontFaceCulling() {
		glEnable(GL11.GL_CULL_FACE);
		glCullFace(GL11.GL_FRONT);
		// GL11.glEnable(GL11.GL_BACK);
	}

	public static void disableCulling() {
		glDisable(GL11.GL_CULL_FACE);
		// GL11.glDisable(GL11.GL_BACK);
	}

	public static void enableDepthTest() {
		glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		glDisable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * not available in OpenGL ES!
	 */
	public static void startWireFrame() {
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	/**
	 * not available in OpenGL ES!
	 */
	public static void stopWireFrame() {
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public static void enableDepthWrite() {
		glDepthMask(true);
	}

	public static void disableDepthWrite() {
		glDepthMask(false);
	}

	public static Matrix4f createProjectionMatrix(Window w, float FOV, float FAR_PLANE, float NEAR_PLANE,
			Matrix4f projectionMatrix) {
		return createProjectionMatrix(w.width(), w.height(), FOV, FAR_PLANE, NEAR_PLANE, projectionMatrix);
	}

	public static Matrix4f createProjectionMatrix(int width, int height, float FOV, float FAR_PLANE, float NEAR_PLANE,
			Matrix4f projectionMatrix) {
		if (projectionMatrix == null) {
			projectionMatrix = new Matrix4f();
		} else {
			projectionMatrix.identity();
		}
		float aspectRatio = (float) width / (float) height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		return projectionMatrix;
	}

}
