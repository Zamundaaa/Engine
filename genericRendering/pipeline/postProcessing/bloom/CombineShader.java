package genericRendering.pipeline.postProcessing.bloom;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.*;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "genericRendering/pipeline/postProcessing/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "genericRendering/pipeline/postProcessing/bloom/combineFragment.txt";

	private IntUniform colorTexture = new IntUniform("colourTexture");
	private IntUniform highlightTexture = new IntUniform("highlightTexture");
	private FloatUniform brightness = new FloatUniform("brightness");
	private IntUniform GUI = new IntUniform("GUI");
	private IntUniform renderGUI = new IntUniform("renderGUI");

	private Vec3Uniform sunDir = new Vec3Uniform("sunDir");
	private Vec3Uniform sunC = new Vec3Uniform("sunColour");

	private Matrix4fUniform invProj = new Matrix4fUniform("invertedProjMat");
	private Matrix4fUniform invView = new Matrix4fUniform("invertedViewMat");
	private IntUniform renderOnlyGUI = new IntUniform("onlyGUI");
	private FloatUniform exposure = new FloatUniform("exposure");

	protected CombineShader() {
		super(new String[] {}, VERTEX_FILE, FRAGMENT_FILE, "position");
		getAllUniformLocations(colorTexture, highlightTexture, brightness, GUI, renderGUI, sunDir, sunC, invProj,
				invView, renderOnlyGUI, exposure);
	}

	protected void connectTextureUnits() {
		colorTexture.set(0);
		highlightTexture.set(1);
		GUI.set(2);
	}

	public void loadBrightness(float brightness) {
		this.brightness.set(brightness);
	}

	public void loadRenderGUI(boolean renderGUI) {
		this.renderGUI.set(renderGUI ? 1 : 0);
	}

	public void loadSunDir(Vector3f v) {
		sunDir.set(v);
	}

	public void loadInvView(Matrix4f mat) {
		invView.set(mat);
	}

	public void loadInvProj(Matrix4f mat) {
		invProj.set(mat);
	}

	public void loadSunColour(Vector3f c) {
		sunC.set(c);
	}

	public void loadRenderOnlyGUI(boolean b) {
		renderOnlyGUI.set(b ? 1 : 0);
	}
	
	public void setExposure(float f) {
		exposure.set(f);
	}
	
}
