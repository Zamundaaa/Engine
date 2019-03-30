package openGlResources.shaders.uniforms;

import org.joml.Matrix4f;

import openGlResources.CommonGL;
import openGlResources.shaders.Uniform;

public class Matrix4fUniform extends Uniform {

	protected Matrix4f value;
	protected float[] buffer = new float[16];

	public Matrix4fUniform(String name) {
		super(name);
	}

	public Matrix4f getMatrix() {
		if (value == null)
			value = new Matrix4f();
		return value;
	}

	public void reupload() {
		if (value != null) {
			_set(value);
		}
	}

	public void identity() {
		if (value == null)
			value = new Matrix4f();
		else
			value.identity();
		set(value);
	}

	public boolean set(Matrix4f m) {
		if (value == null || (m == null && (value.properties() | Matrix4f.PROPERTY_IDENTITY) == 0)
				|| !value.equals(m)) {
			if (value == null)
				value = new Matrix4f();

			if (m == null)
				value.identity();
			else
				value.set(m);
			_set(value);
			return true;
		}
		return false;
	}

	private void _set(Matrix4f m) {
		m.get(buffer);
		CommonGL.glUniformMatrix4fv(location, false, buffer);
	}

}
