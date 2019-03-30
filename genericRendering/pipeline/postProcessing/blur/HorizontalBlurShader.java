package genericRendering.pipeline.postProcessing.blur;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.FloatUniform;
import openGlResources.shaders.uniforms.IntUniform;

public class HorizontalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/genericRendering/pipeline/postProcessing/blur/horizontalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "/genericRendering/pipeline/postProcessing/blur/blurFragment.txt";

	private FloatUniform targetWidth = new FloatUniform("targetWidth");
	private IntUniform originalTexture = new IntUniform("originalTexture");

	protected HorizontalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, new String[] { "position" });
		getAllUniformLocations(targetWidth, originalTexture);
//		AppFolder.log.println("HorizontalBlurShader program log: ");
//		AppFolder.log.println(CommonGL.glGetProgramInfoLog(programID));
	}

	protected void loadTex(int i) {
		originalTexture.set(i);
	}

	protected void loadTargetWidth(float width) {
		this.targetWidth.set(width);
	}

}
