package genericRendering.pipeline.postProcessing.bloom;

import static openGlResources.CommonGL.glActiveTexture;
import static openGlResources.CommonGL.glBindTexture;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import genericRendering.pipeline.postProcessing.ImageRenderer;
import graphics3D.proceduralSkybox.SunKeeper;
import openGlResources.buffers.Fbo;
import tools.misc.Vects;

public class CombineFilter {

	private ImageRenderer renderer;
	private CombineShader shader; 

	public CombineFilter(int width, int height) {
		shader = new CombineShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
		renderer = new ImageRenderer(width, height);
	}

	public void render(int colourTexture, int highlightTexture, int GUI) {
		shader.start();
		shader.setExposure(0.1f);
		shader.loadBrightness(1);
		glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		glActiveTexture(GL13.GL_TEXTURE1);
		glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		glActiveTexture(GL13.GL_TEXTURE2);
		glBindTexture(GL11.GL_TEXTURE_2D, GUI);
		renderer.renderQuad();
		shader.stop();
	}

	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}

	public void render(int colourTexture, int highlightTexture, int GUI, boolean renderGUI, Vector3f v) {
		shader.start();
		shader.loadBrightness(1);
		shader.loadRenderGUI(renderGUI);
		shader.loadRenderOnlyGUI(false);
		glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		glActiveTexture(GL13.GL_TEXTURE1);
		glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		glActiveTexture(GL13.GL_TEXTURE2);
		glBindTexture(GL11.GL_TEXTURE_2D, GUI);
		shader.loadSunDir(v);
		// shader.loadInvProj(Vects.mat4.set(MasterRenderer.getProjectionMatrix()).invert());
		// shader.loadInvView(Vects.mat4.set(MasterRenderer.viewMatrix).invert());
		if (SunKeeper.sun != null)
			shader.loadSunColour(SunKeeper.sun.color());
		else
			shader.loadSunColour(Vects.ONE);

		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		renderer.renderQuad();
		// GL11.glDisable(GL11.GL_BLEND);
		shader.stop();
	}

	public void renderTextureToScreen(int texture) {
		shader.start();
		glActiveTexture(GL13.GL_TEXTURE2);
		glBindTexture(GL11.GL_TEXTURE_2D, texture);
		shader.loadBrightness(1);
		shader.loadRenderGUI(true);
		shader.loadRenderOnlyGUI(true);
		renderer.renderQuad();
		shader.stop();
	}
	
	public Fbo outputFbo() {
		return renderer.fbo();
	}

}
