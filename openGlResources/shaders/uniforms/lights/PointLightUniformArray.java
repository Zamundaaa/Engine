package openGlResources.shaders.uniforms.lights;

import java.util.List;

import genericRendering.lights.Light;
import openGlResources.shaders.Uniform;

public class PointLightUniformArray extends Uniform {

	public final int length;
	protected PointLightUniform[] array;

	public PointLightUniformArray(String name, int count) {
		super(name);
		array = new PointLightUniform[count];
		length = count;
		for (int i = 0; i < count; i++) {
			array[i] = new PointLightUniform(name + "[" + i + "]");
		}
	}

	@Override
	public void getLocation(int program) {
		for (int i = 0; i < array.length; i++) {
			array[i].getLocation(program);
		}
	}

	public PointLightUniform get(int i) {
		return array[i];
	}

	/**
	 * @param lights assumed all are PointLights. They're all treated as such
	 */
	public void set(List<Light> pointLights) {
		for (int i = 0; i < array.length && i < pointLights.size(); i++) {
			array[i].set(pointLights.get(i));
		}
//		AppFolder.log.println("set " + pointLights.size() + " lights");
		for (int i = pointLights.size(); i < array.length; i++) {
			array[i].set((Light) null);
		}
	}

	/**
	 * @param lights assumed all are PointLights. They're all treated as such
	 */
	public void set(Light[] lights) {
		for (int i = 0; i < array.length && i < lights.length; i++) {
			array[i].set(lights[i]);
		}
		for (int i = lights.length; i < array.length; i++) {
			array[i].set((Light) null);
		}
	}

}
