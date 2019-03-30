package window;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;

import tools.misc.interfaces.NotifierInterface;

public class GLHandler {

	private static boolean initialized;
	private static boolean openGlES;

//	/**
//	 * @param ES just a preference. If the desired OpenGL (ES) library is not
//	 *           available then your preference may be ignored
//	 */
	public static void init(boolean ES) {
//		boolean somethingcompatible = true;
//		if (ES) {
//			ES = checkForGLES();
//			if (!ES) {
//				somethingcompatible = checkForGL();
//			}
//		} else {
//			ES = !checkForGL();
//			if (ES) {
//				somethingcompatible = checkForGLES();
//			}
//		}
//		if (!somethingcompatible)
//			throw new IllegalStateException("no OpenGL or OpenGL ES supported on this system?!?");
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		initialized = true;
		openGlES = ES;
	}

	public static Window createPlatformWindow(String title, int startXPos, int startYPos, int w, int h,
			int openGLVersion_major, int openGLVersion_minor, String internalname, NotifierInterface windowResized) {
		return new WindowGLFW(title, startXPos, startYPos, w, h, openGLVersion_major, openGLVersion_minor,
				internalname) {
			@Override
			public void windowResized() {
				windowResized.somethinghappened();
			}
		};
	}

	public static Window createPlatformWindow(String title, int w, int h, int openGLVersion_major,
			int openGLVersion_minor, String internalname, NotifierInterface windowResized) {
		return new WindowGLFW(title, w, h, openGLVersion_major, openGLVersion_minor, internalname) {
			@Override
			public void windowResized() {
				windowResized.somethinghappened();
			}
		};
	}

//	public static boolean checkForGL() {
//
//		return true;
//	}
//
//	public static boolean checkForGLES() {
//		return false;
//	}

	public static boolean ES() {
		return openGlES;
	}

	public static boolean initialized() {
		return initialized;
	}

	public static void cleanUp() {
		if (initialized) {
			glfwTerminate();
			initialized = false;
		}
	}

}
