package graphics3D.proceduralSkybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.*;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/graphics3D/proceduralSkybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "/graphics3D/proceduralSkybox/skyboxFragmentShader.txt";

	private Matrix4fUniform projMat = new Matrix4fUniform("projectionMatrix");
	private Matrix4fUniform viewMat = new Matrix4fUniform("viewMatrix");
	private Matrix4fUniform starCoordTransform = new Matrix4fUniform("starCoordTransform");
	private Vec3Uniform fogColor = new Vec3Uniform("fogColor"), WC = new Vec3Uniform("WC"),
			timeColor = new Vec3Uniform("timeColor"), sunDir = new Vec3Uniform("sunDirection"),
			moonStuff = new Vec3Uniform("moonStuff");
	private IntUniform starTex = new IntUniform("startex"), moonTex = new IntUniform("moonTex"),
			horizon = new IntUniform("horizon"), glow = new IntUniform("glow");
	private FloatUniform blendFactor = new FloatUniform("blendFactor"), bF = new FloatUniform("bF"),
			time = new FloatUniform("TIME"), showMoon = new FloatUniform("showMoon"), PIC = new FloatUniform("PIC");
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, new String[] { "pos" });
		super.getAllUniformLocations(projMat, viewMat, fogColor, starTex, moonTex, blendFactor, bF, WC, timeColor, time,
				sunDir, moonStuff, showMoon, horizon, glow, starCoordTransform);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		projMat.set(matrix);
	}

	private Matrix4f VM = new Matrix4f();

	public void loadViewMatrix(Matrix4f viewMat) {
		VM.set(viewMat);
		VM.m30(0);
		VM.m31(0);
		VM.m32(0);
		this.viewMat.set(VM);
	}

	public void loadFogColor(float r, float g, float b) {
		fogColor.set(r, g, b);
	}

	public void loadBlendFactor(float bf) {
		blendFactor.set(bf);
	}

	public void connectTextureUnits() {
		horizon.set(0);
		glow.set(1);
		moonTex.set(2);
		starTex.set(3);
	}

	public void loadMoonStuff(Vector3f ms) {
		moonStuff.set(ms);
	}

	public void loadStarCoordTransform(Matrix4f mat) {
		starCoordTransform.set(mat);
	}

	public void loadTimeColor(Vector3f color) {
		timeColor.set(color);
	}

	public void loadWeatherFactor(float bF) {
		this.bF.set(bF);
	}

	public void loadWeatherColor(Vector3f c) {
		WC.set(c);
	}

	public void loadSkyPic(boolean PIC) {
		this.PIC.set(PIC ? 0 : 1);
	}

	public void loadTIME(float time) {
		this.time.set(time);
	}

	public void loadSunDirection(float x, float y, float z) {
		sunDir.set(x, y, z);
	}

	public void loadSunDirection(Vector3f dir) {
		sunDir.set(dir);
	}

	public void loadShowMoon(boolean b) {
		showMoon.set(b ? 0 : 1);
	}



}
