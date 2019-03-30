package openGlResources.shaders.uniforms;

import org.joml.Vector2f;

import openGlResources.CommonGL;
import openGlResources.shaders.Uniform;

public class Vec2Uniform extends Uniform {

	protected Vector2f value;

	public Vec2Uniform(String name) {
		super(name);
	}

	public void set(Vector2f v) {
		if (v == null && value != null && (value.x != 0 || value.y != 0)) {
			value.set(0);
			_set(value);
		} else {
			set(v.x, v.y);
		}
	}

	public void set(float x, float y) {
		if (this.value == null) {
			_set(value = new Vector2f(x, y));
		} else if (value.x != x || value.y != y) {
			_set(value.set(x, y));
		}
	}

	private void _set(Vector2f v) {
		CommonGL.glUniform2f(location, v.x, v.y);
	}

}
