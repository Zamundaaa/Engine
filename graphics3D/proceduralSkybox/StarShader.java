package graphics3D.proceduralSkybox;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.FloatUniform;
import openGlResources.shaders.uniforms.IntUniform;

public class StarShader extends ShaderProgram {

	private static final String vertex = "/graphics3D/proceduralSkybox/starVertex.txt", fragment = "/graphics3D/proceduralSkybox/starFragment.txt";

	private IntUniform side = new IntUniform("side");
	private FloatUniform seed = new FloatUniform("seed");

	public StarShader(){
		super(vertex, fragment, new String[] { "pos" });
		getAllUniformLocations(seed, side);
	}

	public void loadSeed(float seed) {
		this.seed.set(seed);
	}

	public void loadSide(int i) {
		this.side.set(i);
	}

}
