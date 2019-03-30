package genericRendering.pipeline;

import openGlResources.buffers.Fbo;

/**
 * 
 * a result from one renderer that others may use. It also could just be
 * displayed for debugging or something.
 * 
 * @author xaver
 *
 */
public class PipelineResult {

	// perhaps there's more stuff necessary. For that reason not just a named Fbo

	public final String name;
	public Fbo fbo;

	public PipelineResult(String name) {
		this.name = name;
	}

}
