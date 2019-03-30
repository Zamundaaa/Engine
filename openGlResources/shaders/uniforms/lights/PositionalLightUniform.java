package openGlResources.shaders.uniforms.lights;

import org.joml.Vector3f;

import openGlResources.shaders.uniforms.Vec3Uniform;

public class PositionalLightUniform extends LightUniform {

	protected Vec3Uniform pos;

	public PositionalLightUniform(String name) {
		super(name);
		pos = new Vec3Uniform(name + ".position");
	}

	@Override
	public void getLocation(int program) {
		super.getLocation(program);
		pos.getLocation(program);
	}

	@Override
	public void set(Vector3f pos, Vector3f rgb) {
		super.set(pos, rgb);
		this.pos.set(pos);
	}

	@Override
	public void set(float x, float y, float z, float r, float g, float b) {
		super.set(x, y, z, r, g, b);
		this.pos.set(x, y, z);
	}

	@Override
	public void set(Vector3f pos, float r, float g, float b) {
		super.set(pos, r, g, b);
		this.pos.set(pos);
	}

}
