package graphics3D.shadows;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.Matrix4fUniform;
import openGlResources.shaders.uniforms.Matrix4fUniformArray;
import tools.AppFolder;

public class ShadowShader extends ShaderProgram {

	public static final String vertex = "graphics3D/shadows/shadowVertex.txt",
			fragment = "graphics3D/shadows/shadowFragment.txt";

	protected Matrix4fUniform projViewTransMat = new Matrix4fUniform("projViewTransMat");
	protected Matrix4fUniformArray jointTransforms;

	public ShadowShader(boolean animations) {
		this(animations ? 50 : 0, animations ? 3 : 0);
	}

	public ShadowShader(int MAX_JOINTS, int MAX_WEIGHTS) {
		super(AppFolder.readJarFile(vertex), AppFolder.readJarFile(fragment), new String[] { "MAX_JOINTS " + MAX_JOINTS, "MAX_WEIGHTS " + MAX_WEIGHTS }, "pos", "tex",
				"norm", "jointIndices", "weights");
		if (MAX_JOINTS > 0 && MAX_WEIGHTS > 0)
			jointTransforms = new Matrix4fUniformArray("jointTransforms", MAX_JOINTS);
		getAllUniformLocations(projViewTransMat, jointTransforms);
	}

}
