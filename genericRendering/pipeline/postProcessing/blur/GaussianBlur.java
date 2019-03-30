package genericRendering.pipeline.postProcessing.blur;

import openGlResources.buffers.Fbo;

public class GaussianBlur {

	protected VerticalBlur vblur;
	protected HorizontalBlur hblur;

	public GaussianBlur(int targetFboWidth, int targetFboHeight) {
		vblur = new VerticalBlur(targetFboWidth, targetFboHeight);
		hblur = new HorizontalBlur(targetFboWidth, targetFboHeight);
	}

	public Fbo render(int texture) {
		hblur.render(texture);
		vblur.render(hblur.getOutputTexture());
		return vblur.fbo();
	}

	public int tex() {
		return vblur.fbo().getColorTexture(0);
	}

	public Fbo fbo() {
		return vblur.fbo();
	}

	public void delete() {
		vblur.cleanUp();
		hblur.cleanUp();
	}

}
