package openGlResources.shaders.uniforms;

import org.joml.Matrix4f;

import genericRendering.pipeline.ShadowMap;

public class ShadowMapUniform extends IntUniform {

	protected Matrix4fUniform toShadowSpace;
	protected FloatUniform shadowDistance;

	public ShadowMapUniform(String name) {
		super(name + ".tex");
		toShadowSpace = new Matrix4fUniform(name + ".toShadowSpace");
		shadowDistance = new FloatUniform(name + ".shadowDistance");
	}

	@Override
	public void getLocation(int program) {
		toShadowSpace.getLocation(program);
		shadowDistance.getLocation(program);
		this.shadowDistance.set(50);
		super.getLocation(program);
	}

	public void setShadowDistance(float d) {
		this.shadowDistance.set(d);
	}

	public void set(int shadowTex, int textureIndex, Matrix4f toShadowSpace, float shadowDistance) {
		this.set(shadowTex, textureIndex);
		this.shadowDistance.set(shadowDistance);
		this.toShadowSpace.set(toShadowSpace);
	}

	public void set(int shadowTex, int textureIndex, Matrix4f toShadowSpace) {
		this.set(shadowTex, textureIndex);
		this.toShadowSpace.set(toShadowSpace);
	}

	
	public void set(ShadowMap s, int textureIndex) {
		set(s.fbo.getDepthTexture(), textureIndex, s.toShadowSpace);
	}

}
