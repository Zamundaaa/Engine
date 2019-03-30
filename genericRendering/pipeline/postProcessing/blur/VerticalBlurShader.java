package genericRendering.pipeline.postProcessing.blur;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.FloatUniform;
import openGlResources.shaders.uniforms.IntUniform;

public class VerticalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/genericRendering/pipeline/postProcessing/blur/verticalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "/genericRendering/pipeline/postProcessing/blur/blurFragment.txt";

	private FloatUniform targetHeight = new FloatUniform("targetHeight");
	private IntUniform originalTexture = new IntUniform("originalTexture");

	protected VerticalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, new String[] { "position" });
		getAllUniformLocations(targetHeight, originalTexture);
		// instant GL_INVALID_OPERATION in glUniform(program not linked) BECAUSE I
		// DIDN'T USE THE PROGRAM! / START THE SHADER!
//		AppFolder.log
//				.println("VerticalBlurShader link status is " + CommonGL.glGetProgrami(programID, GL20.GL_LINK_STATUS));
//		start();
//		targetHeight.set(100);
//		stop();
//		AppFolder.log.println("link error?");

//		AppFolder.log.println("VerticalBlurShader program log: ");
//		AppFolder.log.println(CommonGL.glGetProgramInfoLog(programID));
	}

	protected void loadTex(int i) {
		originalTexture.set(i);
	}

	protected void loadTargetHeight(float height) {
		this.targetHeight.set(height);
	}

}
