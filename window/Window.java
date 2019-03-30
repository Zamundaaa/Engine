package window;

import static openGlResources.CommonGL.glClearColor;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengles.GLESCapabilities;

import graphics3D.Camera;
import openGlResources.GLUtils;
import openGlResources.shaders.ShaderProgram;
import tools.Config;
import window.input.Keyboard;
import window.input.Mouse;

public abstract class Window {

	public static boolean DEBUG_OUTPUT = false;

	protected String title, windowName;
	protected GLCapabilities glcaps;// entweder
	protected GLESCapabilities glescaps;// oder
	protected int openGL_version_major, openGL_version_minor;
	protected long windowHandle;
	protected boolean open;
	private boolean fullscreen;
	protected Keyboard keyboard;
	protected Mouse mouse;
	protected float x, y;
	protected int width, height;
	protected float ratio;
	protected long ftm = 16;
	protected float[] lastFps = new float[60];
	protected float avgFps;
	protected long lastFrame;
	protected boolean vsyncoff, vsyncEnabled;
	protected long minFTM = 0;
	protected Config cfg;
	protected boolean creationFinished = false, closeRequested = false;
	protected boolean debuggingOutput = false;

	protected Camera cam = new Camera();

	protected Matrix4f projectionMatrix = new Matrix4f(), calcMat = new Matrix4f();
	protected float FOV = 90, FAR_PLANE = 1_000_000, NEAR_PLANE = 0.05f;

	protected Vector3f mouseRay = new Vector3f();

	protected Window(String title, int startXPos, int startYPos, int w, int h, int openGL_version_major,
			int openGL_version_minor, String internalWindowName) {
		this.windowName = internalWindowName;
		if (internalWindowName != null) {
			cfg = new Config("config/" + internalWindowName + ".windowcfg");
			startXPos = cfg.getIntConfig("xpos", startXPos);
			startYPos = cfg.getIntConfig("ypos", startYPos);
			int ow = w, oh = h;
			w = cfg.getIntConfig("width", w);
			h = cfg.getIntConfig("height", h);
			if (w == 0 || h == 0) {
				w = ow;
				h = oh;
			}
		}
		width = w;
		height = h;
		ratio = w / (float) h;
		this.openGL_version_major = openGL_version_major;
		this.openGL_version_minor = openGL_version_minor;
		GLUtils.createProjectionMatrix(this, FOV, FAR_PLANE, NEAR_PLANE, projectionMatrix);
		x = startXPos;
		y = startYPos;
		this.title = title;
		if (this.keyboard == null)
			this.keyboard = new Keyboard(this);
		if (this.mouse == null)
			this.mouse = new Mouse(this);

		ShaderProgram.revertShadersToLegacyVersions(openGL_version_major < 3);

		if (openGL_version_major < 3) {
			if (GLHandler.ES())
				ShaderProgram.versionString = "#version 100";
			else
				ShaderProgram.versionString = "#version 110";
		} else if (openGL_version_major == 3 && !GLHandler.ES()) {
			ShaderProgram.versionString = "#version 330";
			if (GLHandler.ES())
				ShaderProgram.versionString += " es";
		} else {
			ShaderProgram.versionString = "#version " + openGL_version_major + "" + openGL_version_minor + "0";
			if (GLHandler.ES())
				ShaderProgram.versionString += " es";
		}

	}

	public abstract void clear();

	public void clearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	/**
	 * for inheritence: CALL THIS METHOD AFTER YOUR ACTUAL UPDATING! here you have
	 * to 1st manage vsync (vsyncoff describes the current state the user wants and
	 * vsyncEnabled describes the current actual VSync state; don't forget to reset
	 * that to false), swap the buffers, update mouse and keyboard
	 * ("updateSomething" HAS to be called before your actual updating MANUALLY).
	 * Fps are already calculated and possibly limited in super.update() too.
	 */
	public void update() {
		ftm = System.currentTimeMillis() - lastFrame;
		while (ftm < minFTM) {
			try {
				Thread.sleep(0, 100);
			} catch (Exception e) {
			}
			ftm = System.currentTimeMillis() - lastFrame;
		}
		lastFrame = System.currentTimeMillis();
		avgFps = 0;
		for (int i = lastFps.length - 1; i > 0; i--) {
			lastFps[i] = lastFps[i - 1];
			avgFps += lastFps[i];
		}
		lastFps[0] = fps();
		avgFps = (avgFps + lastFps[0]) / lastFps.length;

		// calculate mouse picker / mouse ray
		calculateRay(cam, mouse.getAX(), mouse.getAY(), calcMat, mouseRay);

	}

	public void setCamera(Camera c) {
		this.cam = c;
	}

	public Camera camera() {
		return cam;
	}

	public void close() {
		if (open) {
			glfwDestroyWindow(windowHandle);
			open = false;
		}
	}

	/**
	 * if this is called before the frame, then it will not limit the fps anymore.
	 * So call this only if you're not currently rendering a menu
	 */
	public void vSyncOffThisFrame() {
		vsyncoff = true;
	}

	/**
	 * @return width/heighth
	 */
	public float ratio() {
		return ratio;
	}

	public float fps() {
		return 1000f / frameTimeMillis();
	}

	public float avgFps() {
		return avgFps;
	}

	public float frameTimeSeconds() {
		return ftm * 0.001f;
	}

	/**
	 * can be used for the first frame if you have something going on there!
	 */
	public void setFrameTimeSeconds(float f) {
		ftm = (long) (f * 1000);
	}

	public long frameTimeMillis() {
		return ftm;
	}

	public Keyboard keyboard() {
		return keyboard;
	}

	public Mouse mouse() {
		return mouse;
	}

	public boolean fullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean f) {
		// TODO
	}

	public boolean open() {
		return open;
	}

	public long getWindowID() {
		return windowHandle;
	}

	public int height() {
		return height;
	}

	public int width() {
		return width;
	}

	public void show() {
		glfwShowWindow(windowHandle);
	}

	public void hide() {
		glfwHideWindow(windowHandle);
	}

	public abstract void windowResized();

	public boolean isCloseRequested() {
		if (closeRequested || !open)
			return true;
		else
			return false;
	}

	public void setMaxFPS(float f) {
		setMinFTM((long) (1000 / f));
	}

	public void setMinFTM(long millis) {
		this.minFTM = millis;
	}

	public abstract void setPosition(int x, int y);

	public abstract void resizable(boolean b);

	public abstract void setSize(int width, int height);

	public abstract void setWindowIcons(String... icons);

	public float FOV() {
		return FOV;
	}

	public void FOV(float FOV) {
		this.FOV = FOV;
	}

	public float FAR_PLANE() {
		return FAR_PLANE;
	}

	public void FAR_PLANE(float FAR_PLANE) {
		this.FAR_PLANE = FAR_PLANE;
	}

	public float NEAR_PLANE() {
		return NEAR_PLANE;
	}

	public void NEAR_PLANE(float NEAR_PLANE) {
		this.NEAR_PLANE = NEAR_PLANE;
	}

	/**
	 * @return gets you a projectionMatrix fit to this window. Updates every time
	 *         this Window is resized
	 */
	public Matrix4f projectionMatrix() {
		return projectionMatrix;
	}

	public void requestClose() {
		closeRequested = true;
	}

	/**
	 * assumes you're using the projection matrix from this window!
	 */
	public Vector3f mouseRay() {
		return mouseRay;
	}

	/**
	 * assumes you're using the projection matrix from this window!
	 */
	public Vector3f calculateRay(Camera c, float x, float y, Matrix4f calcMat, Vector3f dest) {
		x = 2 * x - 1;
		y = -2 * y + 1;
		calcMat.set(projectionMatrix).invert().transformPosition(x, y, -1, dest);
		calcMat.set(c.viewMat()).invert().transformDirection(dest.x, dest.y, -1, dest);
		return dest.normalize();
	}

	// capabilities

	public boolean supportsMultisampling() {
		return !GLHandler.ES() && openGL_version_major >= 3;
	}

}
