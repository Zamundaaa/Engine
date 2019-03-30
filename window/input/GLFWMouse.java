package window.input;

import static org.lwjgl.glfw.GLFW.*;

import window.Window;

public class GLFWMouse extends Mouse {

	public GLFWMouse(Window w) {
		super(w);
	}

	@Override
	public void hideCursor() {
		glfwSetInputMode(w.getWindowID(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}

	@Override
	public void showCursor() {
		glfwSetInputMode(w.getWindowID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	@Override
	public void setCursorPosition(double newx, double newy) {
		glfwSetCursorPos(w.getWindowID(), newx, newy);
		super.setCursorPosition(newx, newy);
	}

}
