package graphics3D.text;

import graphics3D.meshRenderer.MeshShader;
import openGlResources.shaders.uniforms.IntUniform;

public class TextShader extends MeshShader {

	public static final String vertexFile = "graphics3D/meshRenderer/vertex.txt",
			fragmentFile = "graphics3D/text/textfragment.txt";
	protected IntUniform atlasTexture = new IntUniform("atlasTexture");

	public TextShader() {
		super(vertexFile, fragmentFile, 0);
		getUniformLocation(atlasTexture);
	}

	public IntUniform atlasTexture() {
		return atlasTexture;
	}

}
