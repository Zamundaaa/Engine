package graphics3D.meshRenderer;

import openGlResources.shaders.ShaderProgram;
import openGlResources.shaders.uniforms.*;
import openGlResources.shaders.uniforms.lights.DirectionalLightUniform;
import openGlResources.shaders.uniforms.lights.PointLightUniformArray;
import tools.AppFolder;

public class MeshShader extends ShaderProgram {

	public static final String vertexShader = "graphics3D/meshRenderer/vertex.txt",
			fragmentShader = "graphics3D/meshRenderer/fragment.txt";

	// (mainly) vertex shader stuff
	protected Matrix4fUniform transMat = new Matrix4fUniform("transMat"),
			projViewMat = new Matrix4fUniform("projViewMat");
	protected Vec3Uniform cameraPos = new Vec3Uniform("cameraPos");

	protected Matrix4fUniformArray jointTransforms;

	protected IntUniform numlights;
	protected PointLightUniformArray pointlights;

	// fragment shader stuff
	protected FloatUniform discardUnderAlpha = new FloatUniform("discardUnderAlpha"),
			fakeLight = new FloatUniform("fakeLight");
	protected MatLayUniform diffuse = new MatLayUniform("diffuse");
	protected MatLayUniform bloomMat = new MatLayUniform("bloomMat");
	protected DirectionalLightUniform sun = new DirectionalLightUniform("sun");

	protected FloatUniform reflectivity = new FloatUniform("reflectivity");
	protected ShadowMapUniform shadowmap = new ShadowMapUniform("shadowmap");

	/**
	 * if MAX_LIGHTS is zero then lights are completely disabled
	 */
	public MeshShader(int MAX_LIGHTS, boolean animations, String... defines) {
		this(MAX_LIGHTS, animations ? 30 : 0, animations ? 4 : 0, defines);
	}

	/**
	 * if MAX_LIGHTS is zero then lights are completely disabled. Same for
	 * animations if MAX_JOINTS or MAX_WEIGHTS is zero
	 */
	public MeshShader(int MAX_LIGHTS, int MAX_JOINTS, int MAX_WEIGHTS, String... defines) {
		super(AppFolder.readJarFile(vertexShader), AppFolder.readJarFile(fragmentShader),
				combine(new String[] { "MAX_LIGHTS " + MAX_LIGHTS, "MAX_JOINTS " + MAX_JOINTS,
						"MAX_WEIGHTS " + MAX_WEIGHTS, "useShadows true" }, defines),
				"pos", "tex", "norm", "jointIndices", "weights");
		if (MAX_JOINTS > 0 && MAX_WEIGHTS > 0)
			jointTransforms = new Matrix4fUniformArray("jointTransforms", MAX_JOINTS);
		if (MAX_LIGHTS > 0) {
			numlights = new IntUniform("num_pointlights");
			pointlights = new PointLightUniformArray("pointlights", MAX_LIGHTS);
		}
		getAllUniformLocations(transMat, projViewMat, cameraPos, jointTransforms, numlights, pointlights,
				discardUnderAlpha, fakeLight, reflectivity, diffuse, bloomMat, sun, shadowmap);
		discardUnderAlpha.set(0.05f);
	}

	protected MeshShader(String vertexFile, String fragmentFile, int MAX_LIGHTS) {
		super(AppFolder.readJarFile(vertexFile), AppFolder.readJarFile(fragmentFile),
				new String[] { "MAX_LIGHTS " + MAX_LIGHTS, "MAX_JOINTS 0", "MAX_WEIGHTS 0" }, "pos", "tex", "norm",
				"jointIndices", "weights");
		if (MAX_LIGHTS > 0) {
			numlights = new IntUniform("num_pointlights");
			pointlights = new PointLightUniformArray("pointlights", MAX_LIGHTS);
		}
		// something weird is going on with depth...
		getAllUniformLocations(transMat, projViewMat, cameraPos, jointTransforms, numlights, pointlights,
				discardUnderAlpha, fakeLight, reflectivity, diffuse, bloomMat, sun);
		discardUnderAlpha.set(0.05f);
	}

	private static String[] combine(String[] one, String[] two) {
		if (one == null)
			return two;
		if (two == null)
			return one;
		String[] c = new String[one.length + two.length];
		for (int i = 0; i < one.length; i++)
			c[i] = one[i];
		for (int j = one.length; j < two.length; j++)
			c[j] = two[j];
		return c;
	}

	public Matrix4fUniform transMat() {
		return transMat;
	}

	public Matrix4fUniform projViewMat() {
		return projViewMat;
	}

	public MatLayUniform diffuse() {
		return diffuse;
	}

	public DirectionalLightUniform sun() {
		return sun;
	}

	public MatLayUniform bloomMat() {
		return bloomMat;
	}

	public FloatUniform discardUnderAlpha() {
		return discardUnderAlpha;
	}

	public FloatUniform fakeLight() {
		return fakeLight;
	}

	public FloatUniform reflectivity() {
		return reflectivity;
	}

}
