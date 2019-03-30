package openGlResources.shaders.uniforms;

import models.material.MaterialLayer;
import openGlResources.shaders.Uniform;

public class MatLayUniformArray extends Uniform {

	protected MatLayUniform[] array;

	public MatLayUniformArray(String name, int length) {
		super(name);
		array = new MatLayUniform[length];
		for (int i = 0; i < length; i++) {
			array[i] = new MatLayUniform(name + "[" + i + "]");
		}
	}

	@Override
	public void getLocation(int program) {
		for (int i = 0; i < array.length; i++)
			array[i].getLocation(program);
	}

	public MatLayUniform get(int i) {
		return array[i];
	}

	public void set(MaterialLayer[] layers) {
		for (int i = 0; i < array.length && i < layers.length; i++) {
			array[i].set(layers[i], i);
		}
	}

}
