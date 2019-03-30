package graphics3D.particles;

import org.joml.Matrix4f;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.*;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/graphics3D/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "/graphics3D/particles/particleFShader.txt";

	private IntUniform tex = new IntUniform("particleTexture");
	private Matrix4fUniform projectionMatrix = new Matrix4fUniform("projectionMatrix");
	private FloatUniform bright = new FloatUniform("bright");
	private FloatUniform colorMult = new FloatUniform("colorMult");
	private FloatUniform NOR = new FloatUniform("NOR");
	// private int location_TD;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE,
				new String[] { "position", "modelViewMatrix", null, null, null, "texOffset", "blendFactor" });
		getAllUniformLocations(NOR, tex, projectionMatrix, bright, colorMult);
	}

	public void setTexture(int i) {
		tex.set(i);
	}

	protected void loadBright(float brightness) {
		bright.set(brightness);
	}

	protected void loadNOR(int NOR) {
		this.NOR.set(NOR);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix.set(projectionMatrix);
	}

	public void loadColorMult(float particleColorMult) {
		colorMult.set(particleColorMult);
	}

	// public void loadTimeDarkening(boolean timeDarkening) {
	// super.loadBoolean(location_TD, timeDarkening);
	// }

}
