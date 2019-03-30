package openGlResources.textures;

import static openGlResources.CommonGL.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.opengles.GLES20;

import collectionsStuff.ArrayListInt;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import openGlResources.buffers.OpenGLBuffer;
import tools.AppFolder;
import window.GLHandler;

public class Texture extends OpenGLBuffer {

	protected String name;
	protected boolean transparent, translucent;
	protected int clampMode = GL13.GL_LINEAR;

	public Texture(int openGLTextureID) {
		super(openGLTextureID);
	}

	public Texture(int openGLTextureID, boolean transparent, boolean translucent) {
		super(openGLTextureID);
		this.transparent = transparent;
		this.translucent = translucent;
	}

	public Texture(int openGLTextureID, String name) {
		super(openGLTextureID);
		this.name = name;
	}

	public Texture(int openGLTextureID, boolean transparent, boolean translucent, String name) {
		super(openGLTextureID);
		this.transparent = transparent;
		this.translucent = translucent;
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isTranslucent() {
		return translucent;
	}

	public void setTranslucent(boolean translucent) {
		this.translucent = translucent;
	}

	public void setTexID(int openGLTextureID) {
		this.ID = openGLTextureID;
	}

	public void delete() {
		deleteTexture(this);
	}

	public void bindAndActivateTo(int i) {
		activeTexture(i);
		bind();
	}

	@Override
	public void bind() {
		glBindTexture(GL11.GL_TEXTURE_2D, ID);
	}

	public void unbind() {
		glBindTexture(GL11.GL_TEXTURE_2D, ID);
	}

	/**
	 * @param texturekind
	 * @param mode        GL11.GL_REPEAT, GL13.GL_CLAMP_TO_EDGE etc
	 */
	public void setClampMode(int texturekind, int mode) {
		if (clampMode != mode) {
			clampMode = mode;
			glBindTexture(texturekind, ID);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mode);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mode);
		}
	}

	public static void activeTexture(int textureIndex) {
		glActiveTexture(GL13.GL_TEXTURE0 + textureIndex);
	}

	private static HashMap<Thread, ArrayListInt> textureIDs = new HashMap<>();

	private static ArrayListInt tIDs() {
		ArrayListInt i = textureIDs.get(Thread.currentThread());
		if (i != null) {
			return i;
		} else {
			synchronized (textureIDs) {
				if ((i = textureIDs.get(Thread.currentThread())) != null) {
					return i;
				} else {
					textureIDs.put(Thread.currentThread(), i = new ArrayListInt());
					return i;
				}
			}
		}
	}

	public static void deleteTexture(Texture t) {
		glDeleteTextures(t.ID);
	}

	/**
	 * has to be .png!
	 */
	public static Texture loadJarTexture(String relativePath) {
		TextureData t = loadTextureData(relativePath, true);
		if (t != null)
			return loadTexture(t);
		else
			return null;
	}

	public static Texture loadTextureFromAppFolder(String pathInFolder) {
		return loadExternalTexture(AppFolder.folder + pathInFolder);
	}

	public static Texture loadExternalTexture(String absolutePath) {
		TextureData t = loadTextureData(absolutePath, false);
		if (t != null)
			return loadTexture(t);
		else
			return null;
	}

	public static TextureData loadTextureData(String path, boolean relative) {
//		if (!path.endsWith(".png")) {
//			path += ".png";
//		}
		InputStream in = null;
		try {
			if (relative)
				in = Texture.class.getClassLoader().getResourceAsStream(path);
			else
				in = new FileInputStream(new File(path));
			if (in != null) {
//				System.out.println(path);
				return decodeTexture(in);
			}
		} catch (FileNotFoundException f) {

		} catch (IOException e) {
			e.printStackTrace(AppFolder.log);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	public static TextureData decodeTexture(InputStream in) throws IOException {
		int w, h;
		ByteBuffer buffer;
		PNGDecoder decoder = new PNGDecoder(in);
		w = decoder.getWidth();
		h = decoder.getHeight();
		buffer = ByteBuffer.allocateDirect(4 * w * h);
		decoder.decode(buffer, w * 4, Format.RGBA);
		buffer.flip();
		boolean transparent = false, translucent = false;
//		ArrayListInt list = new ArrayListInt();
		for (int i = 3; i < buffer.limit() && (!transparent || !translucent); i += 4) {
			int b = (int) buffer.get(i) & 0xFF;
//			if (!list.contains(b)) {
//				list.add(b);
//			}
			if (b == 0) {
				transparent = true;
			} else if (b < 255) {
				translucent = true;
			}
		}
//		list.quicksortUp();
//		System.out.println("max=" + list.maxValue());
		in.close();
		return new TextureData(w, h, buffer, transparent, translucent);
	}

	public static Texture loadTexture(TextureData t) {
		if (t != null) {
			int tex = 0;
			try {
				tex = GLHandler.ES() ? GLES20.glGenTextures() : GL11.glGenTextures();
				glBindTexture(GL11.GL_TEXTURE_2D, tex);
				glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, t.getWidth(), t.getHeight(), 0, GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE, t.getBuffer());
				glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				glGenerateMipmap(GL11.GL_TEXTURE_2D);
				glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				if (!GLHandler.ES()) {
					glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
					if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
						float amount = Math.min(4f,
								GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
						glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
								amount);
					} else {
						AppFolder.log.println("Anisotropic Filtering not supported!");
					}
				}
			} catch (Exception e) {
				AppFolder.log.println("failed loading a texture!");
				e.printStackTrace(AppFolder.log);
				System.exit(-1);
			}
			ArrayListInt tIDs = tIDs();
			synchronized (tIDs) {
				if (!tIDs.contains(tex)) {
					tIDs.add(tex);
				}
			}
			return new Texture(tex, t.isTransparent(), t.isTranslucent());
		} else {
			return null;
		}
	}

	public static Texture loadTexture(String path) {
		if (AppFolder.absolute(path)) {
			return loadExternalTexture(path);
		} else {
			return loadJarTexture(path);
		}
	}

	/**
	 * assumes a rgba texture
	 */
	public static Texture loadToTexture(float[] data) {
		int w = (int) Math.sqrt(data.length / 4);
		AppFolder.log.println(w);
		return loadToTexture(data, w, w);
	}

	public static Texture loadToTexture(float[] data, int width, int height) {
		FloatBuffer d = BufferUtils.createFloatBuffer(data.length);
		d.put(data);
		d.flip();
		boolean transparent = false, translucent = false;
		for (int i = 3; i < data.length && !(transparent && translucent); i += 4) {
			if (data[i] == Float.MIN_VALUE) {
				transparent = true;
			}
			// translucency detection doesn't seem to really work (?)
//			else if (data[i] < 0.9f) {
//				translucent = true;
//			}
		}
		int tex = 0;
		try {
			tex = GLHandler.ES() ? GLES20.glGenTextures() : GL11.glGenTextures();
			glBindTexture(GL11.GL_TEXTURE_2D, tex);
			glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, d);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			glGenerateMipmap(GL11.GL_TEXTURE_2D);
			glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			if (!GLHandler.ES()) {
				glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
				if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
					float amount = Math.min(4f,
							GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
							amount);
				} else {
					AppFolder.log.println("Anisotropic Filtering not supported!");
				}
			}
		} catch (Exception e) {
			AppFolder.log.println("failed loading a texture!");
			e.printStackTrace(AppFolder.log);
			System.exit(-1);
		}
		ArrayListInt tIDs = tIDs();
		synchronized (tIDs) {
			if (!tIDs.contains(tex)) {
				tIDs.add(tex);
			}
		}
		return new Texture(tex, transparent, translucent);
	}

	@Override
	public String toString() {
		return "Texture with name '" + name + "' and openGL ID " + ID + " transparent=" + transparent + ", translucent="
				+ translucent;
	}

}
