package openGlResources.shaders;

import static openGlResources.CommonGL.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengles.GLES20;

import openGlResources.CommonGL;
import tools.AppFolder;
import tools.JarResources;
import window.GLHandler;

public abstract class ShaderProgram {

	/**
	 * should be changed according to your needs. If you would only want to use
	 * certain shaders with different versions, use the respective constructor.
	 * Creating a Window will change the global version string to the GL version the
	 * window uses
	 */
	public static String versionString = "#version 330";
	/**
	 * may or may not work. Replaces all the stuff from OpenGL (ES) 3+ like "out"
	 * and "in" with the counterparts from old OpenGL like "attribute" and "varying"
	 */
	private static Map<Thread, Boolean> revertShadersToLegacyVersions = new HashMap<>();
	public static String floatPrecision = "mediump";

	/**
	 * adds a "#define key value" for every shader. If there's already a #define X
	 * then it's gonna be replaced. Defines per shader will overwrite this default
	 * definitions. It's best if you test for conditional defines with #ifdef X.
	 * <p>
	 * gammaCorrection is set to true by default!
	 * </p>
	 */
	public static Map<String, String> defaultDefs = new HashMap<>();
	static {
		defaultDefs.put("gammaCorrection", "true");
	}

	/**
	 * use like this: put in a name and a value (for example a file filled with
	 * constants) and then you can #include the value in your shaders. Note that
	 * this won't accept paths to files, you'll have to read them out yourself. It
	 * also currently doesn't support #include in include values (may come later).
	 * Some includes (from openGlResources/shaders/uniforms/includes/) are in this
	 * map per default, for example: PointLight (pointlight.txt) and MaterialLayer
	 * (materiallayer.txt)
	 */
	public static Map<String, String> includes = new HashMap<>();
	static {
		final String[] defIncludes = new String[] { "pointlight.txt", "materiallayer.txt", "directionallight.txt",
				"shadowmap.txt" };
		for (int i = 0; i < defIncludes.length; i++) {
			String content = AppFolder.readJarFile("openGlResources/shaders/uniforms/includes/" + defIncludes[i]);
			if (content != null) {
				String[] lines = content.split("\n");
				String name = lines[0];
				StringBuilder b = new StringBuilder();
				for (int i2 = 1; i2 < lines.length; i2++) {
					b.append(lines[i2]);
					b.append('\n');
				}
				includes.put(name, b.toString());
			}
		}
	}

	protected int programID;
	protected int vertexShaderID;
	protected int fragmentShaderID;

	public Matrix4f calcMat = new Matrix4f();

	public ShaderProgram(String vertexFile, String fragmentFile, String... vertexAttributes) {
		this(JarResources.loadJarTextFile(vertexFile), JarResources.loadJarTextFile(fragmentFile), null,
				vertexAttributes);
	}

	public ShaderProgram(String[] parts, String vertexFile, String fragmentFile, String... vertexAttributes) {
		this(JarResources.loadJarTextFile(vertexFile), JarResources.loadJarTextFile(fragmentFile), parts,
				vertexAttributes);
	}

	/**
	 * @param vertexShader     source
	 * @param fragmentShader   source
	 * @param defs             sets every #define X to true (1) that's contained in
	 *                         this array. If there's none for a member, it creates
	 *                         one
	 * @param vertexAttributes
	 * @param uniforms
	 */
	public ShaderProgram(String vertexShader, String fragmentShader, String[] defs, String... vertexAttributes) {
		this(versionString, vertexShader, fragmentShader, defs, vertexAttributes);
	}

	/**
	 * @param versionString
	 * @param vertexShader     source
	 * @param fragmentShader   source
	 * @param defs             sets every #define X to true (1) that's contained in
	 *                         this array. If there's none for a member, it creates
	 *                         one
	 * @param vertexAttributes
	 * @param uniforms
	 */
	public ShaderProgram(String versionString, String vertexShader, String fragmentShader, String[] defs,
			String... vertexAttributes) {
		Map<String, String> defMap = new HashMap<>();
		if (defs != null)
			for (int i = 0; i < defs.length; i++) {
				if (defs[i] != null) {
					String[] p = defs[i].trim().split(" ");
					defMap.put(p[0], p[1]);
				}
			}
		vertexShaderID = loadShader(versionString, vertexShader, GL20.GL_VERTEX_SHADER, defMap, false,
				revertShadersToLegacyVersions.get(Thread.currentThread()));
		fragmentShaderID = loadShader(versionString, fragmentShader, GL20.GL_FRAGMENT_SHADER, defMap, true,
				revertShadersToLegacyVersions.get(Thread.currentThread()));
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes(vertexAttributes);
		glLinkProgram(programID);
		glValidateProgram(programID);
		if (glGetProgrami(programID, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
			AppFolder.log.println("shader program failed to link! ProgramInfoLog: ");
			AppFolder.log.println(glGetProgramInfoLog(programID));
			new Exception().printStackTrace(AppFolder.log);
			AppFolder.log.println();
			System.exit(-1);
		}
		stop();
	}

	protected void getAllUniformLocations(Uniform... uniforms) {
		for (int i = 0; i < uniforms.length; i++)
			if (uniforms[i] != null)
				uniforms[i].getLocation(programID);
	}

	private void bindAttributes(String[] attributes) {
		for (int i = 0; i < attributes.length; i++)
			if (attributes[i] != null)
				bindAttribute(i, attributes[i]);
	}

	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programID, attribute, variableName);
	}

	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}

	public void getUniformLocation(Uniform u) {
		u.getLocation(programID);
	}

	public void start() {
		glUseProgram(programID);
	}

	public void stop() {
		glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		if (GLHandler.ES()) {
			GLES20.glDetachShader(programID, vertexShaderID);
			GLES20.glDetachShader(programID, fragmentShaderID);
			GLES20.glDeleteShader(vertexShaderID);
			GLES20.glDeleteShader(fragmentShaderID);
			GLES20.glDeleteProgram(programID);
		} else {
			GL20.glDetachShader(programID, vertexShaderID);
			GL20.glDetachShader(programID, fragmentShaderID);
			GL20.glDeleteShader(vertexShaderID);
			GL20.glDeleteShader(fragmentShaderID);
			GL20.glDeleteProgram(programID);
		}
	}

	public int programID() {
		return programID;
	}

//	public static final String MODULAR_SPLITTER = "###", INVERTED_MODULAR_SPLITTER = "##~";

	private static int loadShader(String versionString, String source, int type, Map<String, String> defines,
			boolean fragment, boolean revertToLegacyVersions) {
		try {
			Map<String, String> defs = new HashMap<>();
			defs.putAll(defaultDefs);
			defs.putAll(defines);

			String[] lines = source.split("\n");

			StringBuilder s = new StringBuilder();
			s.append(versionString);
			s.append('\n');
			s.append("#define true 1\n");
			s.append("#define false 0\n");
			if (fragment)
				s.append("#ifdef GL_ES\nprecision " + floatPrecision + " float;\n#endif\n");
			for (String def : defs.keySet()) {
				s.append("#define ");
				s.append(def);
				s.append(" ");
				s.append(defs.get(def));
				s.append('\n');
			}
			String output = null;
			for (int i = 0; i < lines.length; i++) {
				String o = processLine(s, lines[i], defines, fragment, revertToLegacyVersions, output);
				if (o != null) {
					output = o;
//					AppFolder.log.println("shader output variable is " + output);
				}
			}
			String finalShader = s.toString();

			int shaderID = glCreateShader(type);
			glShaderSource(shaderID, finalShader);
			glCompileShader(shaderID);
			if (glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
				AppFolder.log.println("Could not compile shader !");
				AppFolder.log.println("source:\n" + source);
				AppFolder.log.println("final:\n");
				String[] split = finalShader.split("\n");
				int counter = 0;
				for (int i = 0; i < split.length; i++) {
					if (!split[i].trim().isEmpty()) {
						AppFolder.log.printDirectToFile(counter + " " + split[i] + "\n");
						counter++;
					}
				}
				AppFolder.log.println("\nShader info log:\n" + CommonGL.glGetShaderInfoLog(shaderID));
				new Exception().printStackTrace(AppFolder.log);
				AppFolder.log.println();
				AppFolder.log.println("EXIT!");
				System.exit(-1);
			}
			return shaderID;
		} catch (Exception e) {
			// FIXME should not do this but instead just throw the or throw another
			// exception!
			AppFolder.log.println("failed to load shader!");
			e.printStackTrace(AppFolder.log);
			System.exit(-1);
			return -1;
		}
	}

	private static String processLine(StringBuilder s, String line, Map<String, String> defs, boolean fragment,
			boolean revertToLegacyVersions, String output) {
		if (line.startsWith("#define ")) {
			String def = line.substring(8).split(" ")[0];
			if (defs.keySet().contains(def)) {
				return null;
			}
		} else if (line.startsWith("#include ")) {
			String name = line.substring(9).trim();
			String a = includes.get(name);
			if (a != null) {
				String v[] = a.split("\n");
				for (int i = 0; i < v.length; i++) {
					String o = processLine(s, v[i], defs, fragment, revertToLegacyVersions, output);
					if (output == null) {
						output = o;
					}
				}
			}
			return output;
		} else if (line.startsWith("#version ")) {
			return null;
		} else if (revertToLegacyVersions) {
			if (line.startsWith("in")) {
				line = line.replace("in", fragment ? "varying" : "attribute");
			} else if (line.startsWith("out") || (line.startsWith("layout") && line.contains("out"))) {
				if (fragment) {
					if (output != null)
						return null;
					line = line.substring(7);
					if (line.startsWith("(")) {
						int i = 1;
						for (; i < line.length(); i++) {
							if (line.charAt(i) == ')') {
								i++;
								break;
							}
						}
						line = line.substring(i);
						line = line.split(" ")[3];
						line = line.replace(";", "");
						return line;
					} else {
						line = line.split(" ")[1];
						line = line.replace(";", "");
						return line;
					}
				}
				line = line.replace("out", "varying");
			} else if (output != null) {
				line = line.replace(output, "gl_FragColor");
			}
			line = line.replace("texture", "texture2D");
		}
		s.append(line);
		s.append('\n');
		return output;
	}

	/**
	 * per thread
	 * 
	 * @param doIt
	 */
	public static void revertShadersToLegacyVersions(boolean doIt) {
		revertShadersToLegacyVersions.put(Thread.currentThread(), doIt);
	}

	/**
	 * per thread
	 * 
	 * @param doIt
	 */
	public static boolean revertShadersToLegacyVersions() {
		return revertShadersToLegacyVersions.get(Thread.currentThread());
	}

}