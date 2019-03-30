package window.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayDeque;

import org.lwjgl.glfw.GLFW;

import collectionsStuff.ArrayListInt;
import collectionsStuff.SmartByteBuffer;
import tools.Config;
import window.Window;

public class Keyboard {

	// public static int KEY_UP, KEY_DOWN, KEY_LEFT, KEY_RIGHT, KEY_JUMP,
	// KEY_RUN, KEY_CROUCH, KEY_CTRL, KEY_SHIFT,
	// KEY_TOGGLEINV, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5;
	//// keys 1-5 are free for items or something!

	private ArrayListInt pressed = new ArrayListInt();
	private boolean[] tipped = new boolean[GLFW_KEY_LAST];
	private boolean[] t2 = new boolean[GLFW_KEY_LAST];
	private ArrayDeque<Character> characters = new ArrayDeque<Character>();
	private Window w;

	public Keyboard(Window w) {
		this.w = w;
	}

	public void loadConfiguration() {
		Config cfg = new Config("InputLayout.conf");
		// so you can maybe map mouse buttons to something as well. Should build
		// that in probably for people with many mouse-buttons (also other way
		// around. Should prob put in a complete InputStuff-class)
		if (cfg.entryCount() == 0) {
			cfg = new Config("controls.standardLayout.conf", false, false);
		}
		// ... and make a default config...
		// ALSO: you'll have to make a map with string->Key or maybe you could
		// use the key names provided by glfw and create this map automatically

	}

	/**
	 * @param action is only 0, 1 or 2 --> use single byte for transmission!
	 *               currently the constants used are GLFW_PRESS (1) GLFW_RELEASE
	 *               (0) and GLFW_REPEAT (2). It's no GLFW specific thing though.
	 *               keys will have to be translated to GLFW constants if others are
	 *               used. All around you'll probably not need to use something
	 *               other than glfw as it's in itself cross platform and so on
	 * @param key
	 */
	public void processEvent(int key, int action) {
		if (action == GLFW_PRESS) {
			pressKey(key);
		} else if (action == GLFW_RELEASE) {
			pressed.removeValue(key);
		}
		if (action == GLFW_REPEAT) {
			tipped[key] = true;
			// TODO characters should remain on client-side.
			// Strings are sent to server!

			if (w != null)
				if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
					characters.add('\u0008');
				}
		}
	}

	public boolean isKeyDown(int keyCode) {
		return pressed.contains(keyCode);
	}

	public void updateSomething() {
		for (int i = 0; i < t2.length; i++) {
			t2[i] = false;
		}
	}

	public void clearTips() {
		for (int i = 0; i < tipped.length; i++) {
			tipped[i] = false;
		}
	}

	/**
	 * @param keyCode
	 * @return if the key has been pressed (since the last reset) !! sets
	 *         tipped-value to false !!
	 */
	public boolean keyTipped(int keyCode) {
		boolean ret = tipped[keyCode];
		tipped[keyCode] = false;
		return ret;
	}

	public boolean keyPressedThisFrame(int key) {
		return t2[key];
	}

	public void resetTip(int keyCode) {
		tipped[keyCode] = false;
	}

	public void resetTips() {
		for (int i = 0; i < tipped.length; i++) {
			tipped[i] = false;
		}
	}

	public boolean nextCharAvailable() {
		return characters.size() > 0;
	}

	/**
	 * @return the next char if available, else Character.MAX_VALUE
	 */
	public char getNext() {
		if (characters.size() > 0) {
			char c = characters.pop();
			return c;
		} else {
			return Character.MAX_VALUE;
		}
	}

	public void resetChars() {
		characters.clear();
	}

	public ArrayDeque<Character> getPressedChars() {
		return characters;
	}

	/**
	 * doesn't clear characters
	 */
	public void clearAllButtons() {
		pressed.clear();
		for (int i = 0; i < tipped.length; i++) {
			tipped[i] = false;
			t2[i] = false;
		}
	}

	// Just use something like a list of Key_UP,
	// KEY_DOWN, KEY_LEFT, KEY_RIGHT, KEY_JUMP, KEY_THROW, Key_1, Key_2,
	// Key_3, etc. that can be used instead of direct keys
	// although number keys should stay number keys. Just... let it be!
	// I just wait for that until I *want* to do controls. Now I can just
	// send the pressed keys, as this doesn't normally use more than a few
	// bytes. Also I can
	public void addData(SmartByteBuffer s) {
		int count = pressed.size();
		s.add((byte) count);
		for (int i = 0; i < count; i++)
			s.addShort((short) pressed.get(i));
	}

	public boolean pressKey(int key) {
		boolean ret = false;
		if (!pressed.contains(key)) {
			pressed.add(key);
			ret = true;
		}
		tipped[key] = true;
		t2[key] = true;
		if (w != null)
			if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
				characters.add('\u0008');
			}
		return ret;
	}

	public int pressedKeyCount() {
		return pressed.size();
	}

	/**
	 * @param list a list containing all the keys that should be pressed
	 * @return in case of change, true
	 */
	public boolean setPressedKeys(ArrayListInt list) {
		boolean ret = false;
		for (int i = 0; i < pressed.size(); i++) {
			int index = list.indexOf(pressed.get(i));
			if (index == -1) {
				pressed.remove(i--);
				ret = true;
			} else {
				list.remove(index);
			}
		}
		if (list.size() > 0) {
			pressed.addAll(list);
			ret = true;
		}
		return ret;
	}

	public boolean ctrl() {
		return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	public boolean alt() {
		return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
	}

	public boolean shift() {
		return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

}
