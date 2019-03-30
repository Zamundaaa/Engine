package genericRendering.pipeline.postProcessing.blur;

import static openGlResources.CommonGL.glActiveTexture;
import static openGlResources.CommonGL.glBindTexture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import genericRendering.pipeline.postProcessing.ImageRenderer;
import openGlResources.buffers.Fbo;

public class HorizontalBlur {

	private ImageRenderer renderer;
	private HorizontalBlurShader shader;

	public HorizontalBlur(int targetFboWidth, int targetFboHeight) {
		shader = new HorizontalBlurShader();
		shader.start();
		shader.loadTargetWidth(targetFboWidth);
		shader.stop();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
	}

	public void render(int texture) {
		// returns an empty (black) texture...
		shader.start();
		glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL11.GL_TEXTURE_2D, texture);
		shader.loadTex(0);
		renderer.renderQuad();
		shader.stop();
	}

	public int getOutputTexture() {
		return renderer.getOutputTexture();
	}

	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}

	public Fbo fbo() {
		return renderer.fbo();
	}

}
