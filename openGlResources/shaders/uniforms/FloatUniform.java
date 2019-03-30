package openGlResources.shaders.uniforms;

import openGlResources.CommonGL;
import openGlResources.shaders.Uniform;

public class FloatUniform extends Uniform {

	protected float value;

	public FloatUniform(String name) {
		super(name);
	}

	public void set(float f) {
		if (f != value) {
			CommonGL.glUniform1f(location, value = f);
		}
	}

}
