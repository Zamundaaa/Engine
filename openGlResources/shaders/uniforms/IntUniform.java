package openGlResources.shaders.uniforms;

import static openGlResources.CommonGL.glBindTexture;

import org.lwjgl.opengl.GL11;

import openGlResources.CommonGL;
import openGlResources.shaders.Uniform;
import openGlResources.textures.Texture;

public class IntUniform extends Uniform {

	protected int value;

	/**
	 * can be used for textures (sampler2D...), too
	 */
	public IntUniform(String name) {
		super(name);
	}

	public void set(int v) {
		if (v != value) {
			CommonGL.glUniform1i(location, v);
		}
		value = v;
	}

	public void set(Texture texture, int textureIndex) {
		if (texture != null) {
			texture.bindAndActivateTo(textureIndex);
			set(textureIndex);
		}
	}

	public void set(int tex, int textureIndex) {
		Texture.activeTexture(textureIndex);
		glBindTexture(GL11.GL_TEXTURE_2D, tex);
		set(textureIndex);
	}

}
