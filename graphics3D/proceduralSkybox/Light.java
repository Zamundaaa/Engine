package graphics3D.proceduralSkybox;

import org.joml.Vector3f;

public class Light {

	private Vector3f position, colour, attenuation = new Vector3f(1, 0, 0);

	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.colour = color;
	}

	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.colour = color;
		this.attenuation = attenuation;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setColour(Vector3f color) {
		this.colour.set(color);
	}

	public void setPosition(float x, float y, int z) {
		position.set(x, y, z);
	}

	public void setColour(float x, float y, float z) {
		colour.set(x, y, z);
	}
}
