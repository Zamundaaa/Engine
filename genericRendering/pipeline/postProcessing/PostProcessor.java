package genericRendering.pipeline.postProcessing;

import genericRendering.pipeline.postProcessing.bloom.CombineFilter;
import genericRendering.pipeline.postProcessing.blur.GaussianBlur;
import graphics3D.proceduralSkybox.SkyRenderer;
import graphics3D.proceduralSkybox.SunKeeper;
import loaders.Loader;
import openGlResources.GLUtils;
import openGlResources.buffers.Fbo;
import openGlResources.buffers.VAO;
import tools.misc.Vects;
import window.Window;

public class PostProcessor {

	private static final float[] FINPOSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	public static VAO quad;

	protected int width, height;
	/**
	 * outputFbo is just the last one in your PostProcessing line. May be
	 * dynamically set
	 */
	protected Fbo outputFbo, zeroBlit, oneBlit;
//	protected HorizontalBlur hBlur;
//	protected VerticalBlur vBlur;
	protected GaussianBlur blur;
	protected CombineFilter cmb;

	public PostProcessor(Window w) {
		this(w.width(), w.height());
	}

	public PostProcessor(int targetWidth, int targetHeight) {
		if (quad == null)
			quad = Loader.loadToVAO(FINPOSITIONS, 2);
		this.width = targetWidth;
		this.height = targetHeight;
		int d = 2;
//		hBlur = new HorizontalBlur(targetWidth / d, targetHeight / d);
//		vBlur = new VerticalBlur(targetWidth / d, targetHeight / d);
		blur = new GaussianBlur(targetWidth / d, targetHeight / d);
		cmb = new CombineFilter(targetWidth, targetHeight);
		zeroBlit = Fbo.createStandardFbo(targetWidth, targetHeight, true, false);
		oneBlit = Fbo.createStandardFbo(targetWidth, targetHeight, true, false);
		outputFbo = cmb.outputFbo();
	}

	public Fbo render(Fbo input) {
		GLUtils.stopWireFrame();
		if (input.multisampled() || input.numColorTextures() < 2) {
			input.blitTo(zeroBlit, 0, false);
			input.blitTo(oneBlit, 1, false);
			_render(zeroBlit.getColorTexture(0), oneBlit.getColorTexture(0));
		} else {
			_render(input.getColorTexture(0), input.getColorTexture(1));
		}
		return outputFbo;
	}

	private void _render(int colorTexture, int bloomTexture) {
		GLUtils.disableDepthTest();
		quad.bind();
//		hBlur.render(bloomTexture);
//		vBlur.render(hBlur.getOutputTexture());
		blur.render(bloomTexture);
		cmb.render(colorTexture, blur.tex(), 0, false,
				SunKeeper.getSunDirection(Vects.calcVect, SkyRenderer.getTime()));
		GLUtils.enableDepthTest();
		quad.unbind();
	}

	public static void deleteQuad() {
		quad.delete();
	}

	public Fbo finalOutputFbo() {
		return outputFbo;
	}

	public void delete() {
		blur.delete();
		cmb.cleanUp();
	}

}
