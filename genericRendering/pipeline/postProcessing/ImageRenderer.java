package genericRendering.pipeline.postProcessing;

import static openGlResources.CommonGL.*;

import org.lwjgl.opengl.GL11;

import openGlResources.buffers.Fbo;

public class ImageRenderer {

	private Fbo fbo;

	public ImageRenderer(int width, int height) {
		this.fbo = Fbo.createStandardFbo(width, height, true, false);
	}

	public ImageRenderer() {

	}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bind();
//			GL11.glClearColor(0, 0, 0, 1);
			fbo.clearBuffers();
			fbo.bindToWrite();
		} else {
			glClearColor(0, 0, 0, 1);
			glClear(GL11.GL_COLOR_BUFFER_BIT);
		}
		glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbind();
		}
	}

	public int getOutputTexture() {
		return fbo.getColorTexture(0);
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.delete();
		}
	}

	public Fbo fbo() {
		return fbo;
	}

}
