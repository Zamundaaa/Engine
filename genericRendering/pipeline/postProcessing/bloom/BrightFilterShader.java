package genericRendering.pipeline.postProcessing.bloom;

import openGlResources.shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "bloom/brightFilterFragment.txt";

	public BrightFilterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, new String[] { "position" });
	}

}
