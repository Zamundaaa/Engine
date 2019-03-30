package openGlResources.shaders.uniforms.lights;

import org.joml.Vector3f;

import openGlResources.shaders.uniforms.Vec3Uniform;

/**
 * treats the position of the internal light as a directional vector
 * 
 * @author xaver
 *
 */
public class DirectionalLightUniform extends LightUniform {

	protected Vec3Uniform dir;

	public DirectionalLightUniform(String name) {
		super(name);
		dir = new Vec3Uniform(name + ".dir");
	}

	@Override
	public void getLocation(int program) {
		dir.getLocation(program);
		super.getLocation(program);
	}

	public void set(Vector3f dir, Vector3f rgb) {
		set(dir, rgb.x, rgb.y, rgb.z);
	}

	@Override
	public void set(Vector3f dir, float r, float g, float b) {
		super.set(dir, r, g, b);
		this.dir.set(dir);
	}

	@Override
	public void set(float x, float y, float z, float r, float g, float b) {
		super.set(x, y, z, r, g, b);
		this.dir.set(x, y, z);
	}

	public void setDirection(Vector3f d) {
		dir.set(d);
	}

	public void setDirection(float x, float y, float z) {
		dir.set(x, y, z);
	}

}
