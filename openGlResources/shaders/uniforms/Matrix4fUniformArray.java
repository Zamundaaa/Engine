package openGlResources.shaders.uniforms;

import org.joml.Matrix4f;

import openGlResources.shaders.Uniform;

public class Matrix4fUniformArray extends Uniform {

	protected Matrix4fUniform[] array;

	public Matrix4fUniformArray(String name, int length) {
		super(name);
		array = new Matrix4fUniform[length];
		for (int i = 0; i < length; i++) {
			array[i] = new Matrix4fUniform(name + "[" + i + "]");
		}
	}

	@Override
	public void getLocation(int program) {
		for (int i = 0; i < array.length; i++)
			array[i].getLocation(program);
	}

	public Matrix4fUniform get(int i) {
		return array[i];
	}

	public void set(Matrix4f[] matrices) {
		if (matrices != null) {
			for (int i = 0; i < array.length && i < matrices.length; i++) {
				array[i].set(matrices[i]);
			}
			for(int i = matrices.length; i < array.length; i++) {
				array[i].identity();
			}
		} else {
			for (int i = 0; i < array.length; i++)
				array[i].identity();
		}
	}

}
