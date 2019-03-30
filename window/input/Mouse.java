package window.input;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;

import org.lwjgl.glfw.GLFW;

import tools.Meth;
import window.Window;

public class Mouse {

	public static final int LEFT = 0, RIGHT = 1, MIDDLE = 2;

	protected boolean grabbed = false;
	private boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST],
			buttonsTipped = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

	protected double x, y, lastX, lastY;
	protected double dscrolly, dscrollyFrame;
	protected Window w;

	public Mouse() {

	}

	public Mouse(Window w) {
		this.w = w;
	}

	public void processButton(int action, int button) {
		if (action == GLFW.GLFW_PRESS) {
			buttons[button] = true;
			buttonsTipped[button] = true;
		} else if (action == GLFW.GLFW_RELEASE) {
			buttons[button] = false;
			buttonsTipped[button] = true;
		}
	}

	public void processScroll(double xoffset, double yoffset) {
		dscrolly = yoffset;
		dscrollyFrame = yoffset;
	}

	public boolean mouseInWindow;

	public float getDX() {
		float ret = (float) (x - lastX);
		lastX = x;
		return ret;
	}

	public float getDY() {
		float ret = (float) (y - lastY);
		lastY = y;
		return ret;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	/**
	 * @return the x coordinate in percent
	 */
	public float getAX() {
		return (float) ((x) / (w.width()));
	}

	/**
	 * @return the y coordinate in percent; shifted so it fits for the GUI
	 */
	public float getAY() {
		return (float) ((y) / (w.height()));
	}

	public boolean isButtonDown(int button) {
		// if (Controller.USECONTROLLER) {
		// if (button == 0)
		// return buttons[0] ||
		// Controller.isButtonDown(Controller.RIGHT_SHOULDER);
		// else if (button == 1)
		// return buttons[1] ||
		// Controller.isButtonDown(Controller.LEFT_SHOULDER);
		// }
		return buttons[button];
	}

	public boolean isGrabbed() {
		return grabbed;
	}

	public void setGrabbed(boolean b) {
		grabbed = b;
		if (grabbed) {
			// glfwSetInputMode(DisplayManager.getWindow(),
			// GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			hideCursor();
		} else {
			showCursor();
			// glfwSetInputMode(DisplayManager.getWindow(),
			// GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
	}

	public void hideCursor() {
		
	}

	public void showCursor() {
		
	}

	public void setCursorPosition(double newx, double newy) {
		x = newx;
		y = newy;
		lastX = x;
		lastY = y;
	}

	/**
	 * @return a float representing how much the mouse wheel has been scrolled since
	 *         the last call
	 */
	public float getDWheel() {
		float ret = (float) dscrolly;
		dscrolly = 0;
		return ret;
	}

	/**
	 * @return a float representing how much the mouse wheel has been scrolled this
	 *         frame
	 */
	public float getDWheelFrame() {
		return (float) dscrollyFrame;
	}

	public boolean buttonClickedThisFrame(int button) {
		return buttons[button] && buttonsTipped[button];
	}

	public boolean buttonLeftThisFrame(int button) {
		return !buttons[button] && buttonsTipped[button];
	}

	/**
	 * shall be called after every frame is drawn and before
	 * {@code glfwPollEvents()}. clears all tipped (buttonClickedThisFrame(...))
	 * flags and the scroll of this frame
	 */
	public void updateSomething() {
		for (int i = 0; i < buttonsTipped.length; i++) {
			buttonsTipped[i] = false;
		}
		dscrollyFrame = 0;
	}

	public boolean mouseInWindow() {
		return mouseInWindow;
	}

	/**
	 * @return change
	 */
	public boolean setPressedKeys(byte bitmask) {
		boolean ret = false;
		boolean pressed;
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
			pressed = Meth.readBit(bitmask, i);
			if (buttons[i] != pressed) {
				buttons[i] = pressed;
				ret = true;
			}
		}
		return ret;
	}

	public byte getButtonBitmask() {
		byte ret = 0;
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
			if (buttons[i])
				ret = Meth.setBit(ret, i, true);
		}
		return ret;
	}

	public void updatePosition(double xpos, double ypos) {
		this.x = xpos;
		this.y = ypos;
	}

}
