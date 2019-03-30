package openGlResources.shaders.uniforms.lights;

import static openGlResources.CommonGL.glUniform3f;

import org.joml.Vector3f;

import genericRendering.lights.Light;
import openGlResources.shaders.Uniform;

public abstract class LightUniform extends Uniform {

	protected Light l;

	public LightUniform(String name) {
		super(name + ".color");
		l = new Light(0);
	}

	public void set(float x, float y, float z, float r, float g, float b) {
		if (r != l.r() || g != l.g() || b != l.b()) {
			glUniform3f(location, r, g, b);
		}
	}

	public void set(Vector3f pos, float r, float g, float b) {
		if (r != l.r() || g != l.g() || b != l.b()) {
			glUniform3f(location, r, g, b);
		}
	}

	public void set(Vector3f pos, Vector3f rgb) {
		if (!l.color().equals(rgb)) {
			glUniform3f(location, rgb.x, rgb.y, rgb.z);
		}
	}

	public void set(float r, float g, float b) {
		if (r != l.r() || g != l.g() || b != l.b()) {
			glUniform3f(location, r, g, b);
		}
	}

	public void set(Vector3f rgb) {
		set(rgb.x, rgb.y, rgb.z);
	}

}
