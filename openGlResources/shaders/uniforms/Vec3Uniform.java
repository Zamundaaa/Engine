package openGlResources.shaders.uniforms;

import static openGlResources.CommonGL.glUniform3f;

import org.joml.Vector3f;

import openGlResources.shaders.Uniform;

public class Vec3Uniform extends Uniform {

	protected Vector3f value;

	public Vec3Uniform(String name) {
		super(name);
	}

	public void set(Vector3f v) {
		if (v == null && value != null && (value.x != 0 || value.y != 0 || value.z != 0)) {
			value.set(0);
			_set(value);
		} else {
			set(v.x, v.y, v.z);
		}
	}

	public void set(float x, float y, float z) {
		if (this.value == null) {
			_set(value = new Vector3f(x, y, z));
		} else if (value.x != x || value.y != y || value.z != z) {
			_set(value.set(x, y, z));
		}
	}

	private void _set(Vector3f v) {
		glUniform3f(location, v.x, v.y, v.z);
	}

}
