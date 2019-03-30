package graphics3D.proceduralSkybox;

import static graphics3D.proceduralSkybox.SunKeeper.*;

import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import generic.Thing;
import genericRendering.Renderer;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;
import loaders.Loader;
import models.material.Material;
import openGlResources.GLUtils;
import openGlResources.buffers.Fbo;
import openGlResources.buffers.VAO;
import openGlResources.textures.Texture;
import tools.Meth;
import tools.misc.Vects;
import vr.VRHandler;

public class SkyRenderer extends Renderer<Skybox, SkyboxShader> {

	// TODO make this class a "Renderer". And make it more OO. And intruduce some
	// kind of better control over the sun. Like make the direction built into this
	// SkyRenderer. And/or, even better, make an option to rotate the earth and the
	// camera instead of the sun! This also enables different sun directions,
	// possible 2 suns etc

	private static final float SIZE = Meth.sqrt(VRHandler.farClipPlane) - 1;

	// public static boolean SKYBOXPIC = false;

	private static final float[] VERTICES = { -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,
			-SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE,
			-SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE,
			-SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE };

	// private static String[] TEXTUREFILES = { "right", "left",
	// "topwithoutsun", "bottom", "back", "front" };
	// private static String[] NIGHTTEXTUREFILES = { "nightRight", "nightLeft",
	// "nightTop", "nightBottom", "nightBack",
	// "nightFront" };

	// private static String[] RAINTEXTUREFILES = {"clouds", "clouds", "clouds",
	// "clouds", "clouds", "clouds"};

	private VAO cube;

	// private int tex, nighttex;// CUBEMAP TEXTURES!
	private Texture moonTex, horizon, glow;// normal Textures
	private Fbo stars;
	private static float time = 14f;

	public SkyRenderer() {
		super(new SkyboxShader());
//		time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		renderStars(1024);// GPrefs.starQuality()
		moonTex = Texture.loadJarTexture("res/textures/sky/moon.png");

		horizon = Texture.loadJarTexture("res/textures/sky/skycolorsselfmade.png");
		glow = Texture.loadJarTexture("res/textures/sky/stolenglow.png");
		cube = Loader.loadToVAO(VERTICES, 3);
		
		add(new Skybox(cube, new Material()));

		moonTex.bind();
		GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, new float[] { 0, 0, 0, 0 });
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		moonTex.unbind();

		shader.start();
		shader.connectTextureUnits();
		shader.loadMoonStuff(ms);
		shader.loadShowMoon(true);
		shader.stop();
		
		noWireframe = true;

	}

	public static final float MOONSIZEMIN = 2;

	// private static float moonAngleInDegrees =
	// Tools.loadFloatPreference("moonAngleInDegrees", 3);
	private static float moonAngleInDegrees = 2.5f;// data.chunkLoading.NormalGenerator.getG().genThing(0) + MOONSIZEMIN;
	private static final Vector3f ms = new Vector3f((float) (1 - Meth.cos(moonAngleInDegrees * Meth.angToRad)),
			(float) Meth.sin(moonAngleInDegrees * Meth.angToRad),
			0.5f / ((float) Meth.sin(moonAngleInDegrees * Meth.angToRad)));

	public static float getMoonSizeInDegrees() {
		return moonAngleInDegrees;
	}

	public static void setMoonThings() {
		moonAngleInDegrees = 5;// data.chunkLoading.NormalGenerator.getG().genThing(0) + MOONSIZEPM1;
		ms.x = (float) (1 - Meth.cos(moonAngleInDegrees * Meth.angToRad));
		ms.y = (float) Meth.sin(moonAngleInDegrees * Meth.angToRad);
		ms.z = 0.5f / ms.y;
	}

	public static void addToTime(float add) {
		time += add;
		time %= 24f;
	}

	public static void setTime(float t) {
		time = t;
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat, Map<String, PipelineResult> pipelineResults) {
		if (GLUtils.doWireframe)// doesn't make sense for skybox!
			GLUtils.stopWireFrame();
		shader.loadMoonStuff(ms);
		shader.loadProjectionMatrix(projMat);
		shader.loadViewMatrix(c.viewMat());
		prepareSomeThings();
		return true;
	}

	@Override
	protected boolean prepareFor(Skybox t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat, Map<String, PipelineResult> pipelineResults) {
		loadTextures();
		return false;
	}

	@Override
	protected void stopRendering() {

	}

	@Override
	public int priority(Thing m) {
		return 0;
	}

	private void prepareSomeThings() {
		SunKeeper.updateClient();
		
		setUpFogThings();
		boolean inspace = false;// Camera.inSpace();
		if (inspace) {
			shader.loadBlendFactor(1);
		} else {
			shader.loadBlendFactor(blendFactor);
		}
		shader.loadShowMoon(!inspace);
		shader.loadWeatherFactor(0);
		shader.loadTIME(time);
		SunKeeper.getSunDirection(Vects.calcVect, time);
		shader.loadSunDirection(Vects.calcVect);
		Vects.setCalcVect(0);
		shader.loadTimeColor(timeColor);
		CM.identity();
		CM.rotate(SunKeeper.getSunAngle(time), 0, 0, 1);
		shader.loadStarCoordTransform(CM);
	}

	private Matrix4f CM = new Matrix4f();
	private static Vector3f timeColor = new Vector3f();
	// private int texture1;
	// private int texture2;
	private float blendFactor;

	public void loadTextures() {
		horizon.bindAndActivateTo(0);
		glow.bindAndActivateTo(1);
		moonTex.bindAndActivateTo(2);
		stars.bindToRead(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, stars.getColorTexture(0));
	}

	public void setUpFogThings() {
		if ((time >= night) || (time >= 0 && time < morningstart)) {
			// texture1 = nighttex;
			// texture2 = nighttex;
			blendFactor = 1;
		} else if (time >= morningstart && time < morning) {
			// texture1 = nighttex;
			// texture2 = tex;
			blendFactor = (time - morningstart) / (morning - morningstart);
			blendFactor = 1 - blendFactor;
		} else if (time >= morning && time < eveningstart) {
			// texture1 = tex;
			// texture2 = tex;
			blendFactor = 0;
		} else {
			// texture1 = tex;
			// texture2 = nighttex;
			blendFactor = (time - eveningstart) / (night - eveningstart);
		}

		timeColor.x = Meth.blend(0.698f, 0, blendFactor);
		timeColor.y = Meth.blend(1, 0, blendFactor);
		timeColor.z = Meth.blend(1, 0, blendFactor);

	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

//	public void renderMenu(float r, float g, float b) {
//		forMenu = true;
//		time += Loop.getFrameTimeSeconds() * 0.25f;
//		if (Keyboard.getSystemKeyboard().isKeyDown(GLFW.GLFW_KEY_KP_ADD)) {
//			time += Loop.getFrameTimeSeconds();
//		}
//		if (Keyboard.getSystemKeyboard().isKeyDown(GLFW.GLFW_KEY_KP_SUBTRACT)) {
//			time -= Loop.getFrameTimeSeconds();
//		}
//		time %= 24;
//		setUpFogThings();
//		render(MasterRenderer.viewMatrix, r, g, b);
//		forMenu = false;
//	}

	public static float getTime() {
		return time;
	}

//	/**
//	 * if you adjusted the quality then you can re-render here
//	 */
//	public void rerenderStars() {
//		renderStars(GPrefs.starQuality());
//	}

	private void renderStars(int quali) {
		GLUtils.stopWireFrame();
		if (stars != null)
			stars.delete();
//		if (quali == 0) {
//			quali = Meth.toInt(0.7f * Meth.sqrt(DisplayManager.HEIGHT * DisplayManager.WIDTH));
//		}
		stars = Fbo.createCubeMapFbo(quali);
		stars.bind();
//		stars.clearColorAndDepthBuffers();
		GLUtils.disableDepthTest();
		GLUtils.disableCulling();

		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		VAO mod = Loader.loadToVAO(positions, 2);
		StarShader s = new StarShader();
		s.start();
		mod.bind();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		float seed = Meth.randomFloat(0, 10000);
		s.loadSeed(seed);
		for (int i = 0; i < 6; i++) {
			stars.createSideForRendering(i, 0);
			s.loadSide(i);
			mod.drawArrays_Triangle_Strip();
		}

		s.stop();
		s.cleanUp();
		mod.delete();
		GLUtils.enableCulling();
		GLUtils.enableDepthTest();
		stars.unbind();
	}

	public void cleanUp() {
		stars.delete();
		shader.cleanUp();
	}

	public static float getTimeB() {
		return timeColor.z;
	}
	
	@Override
	public int renderTiming() {
		return -1000;
	}
	
}
