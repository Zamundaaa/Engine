package openGlResources.shaders.uniforms;

import openGlResources.shaders.Uniform;

public class FloatUniformArray extends Uniform {

	protected FloatUniform[] array;

	public FloatUniformArray(String name, int length) {
		super(name);
		array = new FloatUniform[length];
		for (int i = 0; i < length; i++) {
			array[i] = new FloatUniform(name + "[" + i + "]");
		}
	}

	@Override
	public void getLocation(int program) {
		for (int i = 0; i < array.length; i++)
			array[i].getLocation(program);
	}

	public FloatUniform get(int i) {
		return array[i];
	}

	public void set(float[] values) {
		for (int i = 0; i < array.length && i < values.length; i++)
			array[i].set(values[i]);
	}
	
	public void set(int i, float value) {
		array[i].set(value);
	}

}
