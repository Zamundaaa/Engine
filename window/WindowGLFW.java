package window;

import static openGlResources.CommonGL.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES32;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import openGlResources.GLUtils;
import tools.AppFolder;
import window.input.GLFWMouse;
import window.input.Keyboard;

public abstract class WindowGLFW extends Window {

	public WindowGLFW(String title, int w, int h, int openGL_version_major, int openGL_version_minor) {
		this(title, w, h, openGL_version_major, openGL_version_minor, null);
	}

	public WindowGLFW(String title, int w, int h, int openGL_version_major, int openGL_version_minor,
			String internalWindowName) {
		this(title, -1, -1, w, h, openGL_version_major, openGL_version_minor, internalWindowName);
	}

	/**
	 * creates a glfw window with the specified title, position and size values and
	 * a openGL context for this thread with the specified version. Pass -1 for
	 * position or size for it to be chosen by the system. If you pass an internal
	 * window name, and there's a config, the values of the config will be preferred
	 * to yours. If you want to override that, set the windows position and size
	 * after creation. Before this can be called, {@link GLHandler#init()} has to
	 * have been called on the main thread!
	 */
	public WindowGLFW(String title, int startXPos, int startYPos, int w, int h, int openGL_version_major,
			int openGL_version_minor, String internalWindowName) {
		super(title, startXPos, startYPos, w, h, openGL_version_major, openGL_version_minor, internalWindowName);
		glfwDefaultWindowHints();
		// the window will stay hidden after creation
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		// OpenGL MAJOR.MINOR
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, openGL_version_major);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, openGL_version_minor);
		// ERROR: SOMETHING ABOUT ARGUMENTS ARE INCONSISTENT!
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API,
				GLHandler.ES() ? GLFW.GLFW_EGL_CONTEXT_API : GLFW.GLFW_NATIVE_CONTEXT_API);
		GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLHandler.ES() ? GLFW.GLFW_OPENGL_ES_API : GLFW.GLFW_OPENGL_API);

		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		boolean core = GLHandler.ES()
				|| (openGL_version_major >= 3 && (openGL_version_major != 3 || openGL_version_minor >= 3));
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,
				core ? GLFW.GLFW_OPENGL_CORE_PROFILE : GLFW.GLFW_OPENGL_COMPAT_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE);

		// Create the window
		windowHandle = glfwCreateWindow(this.width, this.height, title, NULL, NULL);
		if (windowHandle == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		setPosition((int) this.x, (int) this.y);

		glfwMakeContextCurrent(windowHandle);
		glfwSwapInterval(1);

		if (GLHandler.ES())
			glescaps = GLES.createCapabilities();
		else
			glcaps = GL.createCapabilities();

		if (Window.DEBUG_OUTPUT) {
			glEnable(GL43.GL_DEBUG_OUTPUT);
			if (GLHandler.ES())
				GLES32.glDebugMessageCallback(new org.lwjgl.opengles.GLDebugMessageCallback() {
					@Override
					public void invoke(int source, int type, int id, int severity, int length, long message,
							long userParam) {
						AppFolder.log.println(org.lwjgl.opengles.GLDebugMessageCallback.getMessage(length, message));
					}
				}, 1);
			else
				GL43.glDebugMessageCallback(new org.lwjgl.opengl.GLDebugMessageCallback() {
					@Override
					public void invoke(int source, int type, int id, int severity, int length, long message,
							long userParam) {
						AppFolder.log.println(org.lwjgl.opengl.GLDebugMessageCallback.getMessage(length, message));
					}
				}, 1);
		}
		glfwShowWindow(windowHandle);

		this.keyboard = new Keyboard(this);
		this.mouse = new GLFWMouse(this);

		open = true;

		glfwSetWindowPosCallback(windowHandle, (window, xpos, ypos) -> {
			if (creationFinished) {
				x = xpos;
				y = ypos;
				if (cfg != null) {
					cfg.setConfig("xpos", (int) x);
					cfg.setConfig("ypos", (int) y);
					cfg.save();
				}
			}
		});

		glfwSetWindowSizeCallback(windowHandle, (window, wi, he) -> {
			if (wi != width || he != height) {
				width = wi;
				height = he;
				ratio = width / (float) height;
				glViewport(0, 0, width, height);
				GLUtils.createProjectionMatrix(this, FOV, FAR_PLANE, NEAR_PLANE, projectionMatrix);
				windowResized();
				if (creationFinished) {
					if (cfg != null) {
						cfg.setConfig("width", width);
						cfg.setConfig("height", height);
						cfg.save();
					}
				}
			}
		});

		glfwSetMouseButtonCallback(windowHandle, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				mouse.processButton(action, button);
			}
		});
		glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouse.updatePosition(xpos, ypos);
			}
		});
		glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				mouse.processScroll(xoffset, yoffset);
			}
		});
		glfwSetCursorEnterCallback(windowHandle, new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				if (entered) {
					mouse.setGrabbed(!mouse.isGrabbed());
					mouse.setGrabbed(!mouse.isGrabbed());
				}
				mouse.mouseInWindow = entered;
			}
		});

		glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			try {
				if (key >= 0) {
					keyboard.processEvent(key, action);
				}
			} catch (ArrayIndexOutOfBoundsException a) {
				a.printStackTrace(AppFolder.log);
			}
		});
		glfwSetCharModsCallback(windowHandle, new GLFWCharModsCallback() {
			@Override
			public void invoke(long window, int codepoint, int mods) {
				char[] chars = Character.toChars(codepoint);
				keyboard.getPressedChars()
						.add(mods == GLFW.GLFW_MOD_SHIFT ? Character.toUpperCase(chars[0]) : chars[0]);
			}
		});

		glClearColor(0.4f, 0.7f, 0.8f, 1);
		glEnable(GL11.GL_DEPTH_TEST);
		lastFrame = System.currentTimeMillis();
		creationFinished = true;

	}

	@Override
	public void clear() {
		glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void update() {
		if (vsyncoff) {// && vsyncEnabled
			glfwSwapInterval(0);
//			vsyncEnabled = false;
//			if (debuggingOutput)
//				AppFolder.log.println("Vsync off!");
		} else if (!vsyncoff) {// && !vsyncEnabled
			glfwSwapInterval(1);
//			vsyncEnabled = true;
//			if (debuggingOutput)
//				AppFolder.log.println("Vsync on!");
		}
		vsyncoff = false;

		// this takes ages in benchmarking app ...
		glfwSwapBuffers(windowHandle); // swap the color buffers

		mouse.updateSomething();
		keyboard.updateSomething();

		float timesToPollEvents = Math.min((float) frameTimeMillis() / (float) 32, 5);
		if (debuggingOutput && (int) timesToPollEvents > 1)
			AppFolder.log.println("need to poll " + (int) timesToPollEvents + " times");
		do {
			// Poll for window events
			glfwPollEvents();
			timesToPollEvents--;
		} while (timesToPollEvents > 0);

		super.update();
	}

	@Override
	public boolean isCloseRequested() {
		return super.isCloseRequested() || glfwWindowShouldClose(windowHandle);
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		glfwSetWindowPos(windowHandle, x, y);
	}

	public void setSize(int w, int h) {
		glfwSetWindowSize(windowHandle, w, h);
	}

	public void resizable(boolean r) {
//		glfwWindowHint(GLFW_RESIZABLE, r ? 1 : 0);
		glfwSetWindowAttrib(windowHandle, GLFW_RESIZABLE, r ? 1 : 0);
	}

	@Override
	public void setWindowIcons(String... icons) {
		try {
			int w, h;
			GLFWImage[] gimgs = new GLFWImage[icons.length];
			for (int i = 0; i < icons.length; i++) {
				String relativePath = icons[i];
				InputStream in = Window.class.getClassLoader().getResourceAsStream(relativePath);
				PNGDecoder decoder;
				try {
					decoder = new PNGDecoder(in);
				} catch (NullPointerException n) {
					System.out.println(relativePath + " with jvm encoding " + Charset.defaultCharset());
					throw n;
				}
				w = decoder.getWidth();
				h = decoder.getHeight();
				ByteBuffer buffer = ByteBuffer.allocateDirect(4 * w * h);
				decoder.decode(buffer, w * 4, Format.RGBA);
				buffer.flip();
				gimgs[i] = GLFWImage.malloc();
				gimgs[i].set(w, h, buffer);
			}
			GLFWImage.Buffer buff = GLFWImage.malloc(icons.length);
			try {
				for (int i = 0; i < icons.length; i++) {
					buff.put(i, gimgs[i]);
				}
				GLFW.glfwSetWindowIcon(windowHandle, buff);
				for (int i = 0; i < gimgs.length; i++) {
					gimgs[i].free();
				}
			} finally {
				buff.free();
			}
		} catch (Exception e) {

		}
	}

}
