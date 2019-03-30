package openGlResources.shaders;

import static openGlResources.CommonGL.glGetUniformLocation;

public abstract class Uniform {

	protected int location;
	protected final String name;

	public Uniform(String name) {
		this.name = name;
	}

	public void getLocation(int program) {
		location = glGetUniformLocation(program, name);
	}

	public int location() {
		return location;
	}

}
