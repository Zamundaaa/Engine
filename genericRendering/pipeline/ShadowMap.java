package genericRendering.pipeline;

import org.joml.Matrix4f;

import openGlResources.buffers.Fbo;

public class ShadowMap extends PipelineResult {
	
	public Matrix4f toShadowSpace;
	
	public ShadowMap(String name, Fbo fbo, Matrix4f toShadowSpace) {
		super(name);
		this.fbo = fbo;
		this.toShadowSpace = toShadowSpace;
	}

}
