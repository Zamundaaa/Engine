package genericRendering.pipeline.postProcessing.blur;

import openGlResources.buffers.Fbo;

public class CombinedBlur {

	protected VerticalBlur vblur;
	protected HorizontalBlur hblur;

	public CombinedBlur(int targetFboWidth, int targetFboHeight) {
		vblur = new VerticalBlur(targetFboWidth, targetFboHeight);
		hblur = new HorizontalBlur(targetFboWidth, targetFboHeight);
	}

	public Fbo render(int texture) {
		hblur.render(texture);
		vblur.render(hblur.getOutputTexture());
		return vblur.fbo();
	}
	
	public void delete() {
		vblur.cleanUp();
		hblur.cleanUp();
	}

}
