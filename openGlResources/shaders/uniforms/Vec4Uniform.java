package openGlResources.shaders.uniforms;

import org.joml.Vector4f;

import openGlResources.CommonGL;
import openGlResources.shaders.Uniform;

public class Vec4Uniform extends Uniform {

	protected Vector4f value;

	public Vec4Uniform(String name) {
		super(name);
	}

	public void set(Vector4f v) {
		if (this.value == null) {
			this.value = new Vector4f(v);
			_set(value);
		} else if (v == null) {
			_set(value.set(0));
		} else if (!value.equals(v)) {
			_set(value.set(v));
		}
	}

	private void _set(Vector4f v) {
		CommonGL.glUniform4f(location, v.x, v.y, v.z, v.w);
	}

}
