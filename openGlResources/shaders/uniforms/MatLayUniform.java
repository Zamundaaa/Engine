package openGlResources.shaders.uniforms;

import static openGlResources.CommonGL.glGetUniformLocation;
import static openGlResources.CommonGL.glUniform1i;

import org.lwjgl.opengl.GL13;

import models.material.MaterialLayer;
import openGlResources.shaders.Uniform;

/**
 * 
 * represents a MaterialLayer as a Uniform. For this to work, you'll have to get
 * this in the fragment shader:
 * 
 * <pre>
 * struct MaterialLayer{
 * int hasTexture;
 * sampler2D Tex;
 * vec4 Color;
 * };
 * 
 * vec4 getColor(vec2 texCoords, MaterialLayer m){
 * 	if(m.hasTexture == 1)
 * 		return texture(m.Tex, texCoords);
 * 	else
 * 		return m.Color;
 * }
 * 
 * uniform MaterialLayer name;
 * </pre>
 * 
 * the struct and the function is automatically added if you put #include
 * MaterialLayer on the top of your shader
 * 
 * @author xaver
 *
 */
public class MatLayUniform extends Uniform {

	protected IntUniform tex;
	protected Vec4Uniform color;
	protected boolean value;

	public MatLayUniform(String name) {
		super(name);
		tex = new IntUniform(name + ".Tex");
		color = new Vec4Uniform(name + ".color");
	}

	@Override
	public void getLocation(int program) {
		location = glGetUniformLocation(program, name + ".hasTexture");
		tex.getLocation(program);
		color.getLocation(program);
	}

	/**
	 * also binds and activates the texture to
	 * {@link GL13#GL_TEXTURE0}+textureIndex, if a texture is available
	 */
	public void set(MaterialLayer m, int textureIndex) {
		if (value != m.hasTexture()) {
			value = m.hasTexture();
			glUniform1i(location, value ? 1 : 0);
		}
		if (m.hasTexture()) {
			m.tex().bindAndActivateTo(textureIndex);
			tex.set(textureIndex);
		} else {
			color.set(m.color());
		}
	}

}
