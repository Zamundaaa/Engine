package genericRendering.lights;

import org.joml.Vector3f;

public class DirectionalLight extends Light {

	public DirectionalLight(float x, float y, float z, float r, float g, float b) {
		super(x, y, z, r, g, b);
	}

	public DirectionalLight(float r, float g, float b) {
		super(r, g, b);
	}

	public Vector3f direction() {
		return position;
	}

	public DirectionalLight(DirectionalLight toClone) {
		super(toClone);
	}

	@Override
	public DirectionalLight clone() {
		return new DirectionalLight(this);
	}

}
