package graphics3D.terrain;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.*;
import openGlResources.shaders.uniforms.lights.PointLightUniformArray;
import tools.JarResources;

public class TerrainShader extends ShaderProgram {

	public static final String vertexFile = "/graphics3D/terrain/vertex.txt",
			fragmentFile = "/graphics3D/terrain/fragment.txt";

	protected Matrix4fUniform projViewMat = new Matrix4fUniform("projViewMat");

	protected MatLayUniformArray diffuse = new MatLayUniformArray("diffuse", 5);
	protected Vec3Uniform sunColor = new Vec3Uniform("sunColor");
	protected Vec3Uniform sunDir = new Vec3Uniform("sunDir");
	protected FloatUniform time = new FloatUniform("time");
	protected IntUniform numPointLights = new IntUniform("num_pointlights");
	protected PointLightUniformArray pointlights;
	protected Vec3Uniform cameraPos = new Vec3Uniform("cameraPos");
//	protected IntUniform shadowMap;
//	protected Matrix4fUniform toShadowSpace;
	protected ShadowMapUniform shadowMap;

	public TerrainShader(int num_lights, boolean shadows) {
		super(JarResources.loadJarTextFile(vertexFile), JarResources.loadJarTextFile(fragmentFile),
				new String[] { "MAX_LIGHTS " + num_lights, "useShadows " + shadows }, "pos", "tex", "norm");
		if (shadows) {
			shadowMap = new ShadowMapUniform("shadowmap");
		}
		if (num_lights > 0)
			pointlights = new PointLightUniformArray("pointlights", num_lights);
		getAllUniformLocations(projViewMat, diffuse, sunDir, sunColor, time, numPointLights, pointlights, cameraPos,
				shadowMap);
	}

	public Matrix4fUniform projViewMat() {
		return projViewMat;
	}

	public MatLayUniformArray diffuse() {
		return diffuse;
	}

	public Vec3Uniform sunBrightness() {
		return sunColor;
	}

	public Vec3Uniform sunDir() {
		return sunDir;
	}

	public Vec3Uniform sunColor() {
		return sunColor;
	}

	public FloatUniform time() {
		return time;
	}

}
