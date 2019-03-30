package openGlResources.buffers;

import static openGlResources.CommonGL.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import collectionsStuff.ArrayListInt;
import openGlResources.CommonGL;
import tools.AppFolder;
import window.Window;

/**
 * 
 * 
 * 
 * @author xaver
 *
 */
public class Fbo extends OpenGLBuffer {
	// completely OpenGL ES compliant!

	protected ArrayListInt colorBuffers = new ArrayListInt(), colorTextures = new ArrayListInt();
	protected int width, height;
	protected int depthBuffer, depthTexture;
	protected boolean stencil = false, multisampled;

	/**
	 * this fbo is empty! Create attachments on your own!
	 */
	public Fbo(int width, int height, int nrOfDrawBuffers) {
		super(CommonGL.glGenFramebuffers());
		colorClear = new float[nrOfDrawBuffers][4];
		for (int i = 0; i < nrOfDrawBuffers; i++)
			colorClear[i][3] = 1;
		bind();
		this.width = width;
		this.height = height;
		determineDrawBuffers(nrOfDrawBuffers);
		bind();
	}

	/**
	 * this fbo is empty! It's ready for a single color buffer! Create attachments
	 * on your own!
	 */
	public Fbo(int width, int height) {
		this(width, height, 1);
	}

	private Fbo(int width, int height, int[] drawBuffers) {
		super(CommonGL.glGenFramebuffers());
		colorClear = new float[drawBuffers.length][4];
		for (int i = 0; i < drawBuffers.length; i++)
			colorClear[i][3] = 1;
		bind();
		this.width = width;
		this.height = height;
		if (drawBuffers.length > 0) {
			IntBuffer IdrawBuffers = BufferUtils.createIntBuffer(drawBuffers.length);
			for (int i = 0; i < drawBuffers.length; i++)
				IdrawBuffers.put(drawBuffers[i]);
			IdrawBuffers.flip();
			glDrawBuffers(IdrawBuffers);
		} else {
			glDrawBuffer(GL11.GL_NONE);
		}
		bind();
	}
//
//	public int createColorAttatchment(int attachment, boolean multisampled) {
//		this.multisampled = multisampled;
//		int colourBuffer = GL30.glGenRenderbuffers();
//		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
//		if (multisampled)
//			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
//		else
//			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_RGBA8, width, height);
//		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment,
//				GL30.GL_RENDERBUFFER, colourBuffer);
//		if (colorBuffers.size() == attachment) {
//			colorBuffers.add(colourBuffer);
//		} else {
//			colorBuffers.set(attachment, colourBuffer);
//		}
//		return colourBuffer;
//	}
//
//	/*
//	 * Creates a texture and sets it as the colour buffer attachment for this FBO.
//	 * 
//	 * @param type
//	 */
//	public int createTextureAttachment(int attachment, boolean multisampled) {
//		this.multisampled = multisampled;
//		int colourTexture = GL11.glGenTextures();
//		int t = multisampled ? GL32.GL_TEXTURE_2D_MULTISAMPLE : GL11.GL_TEXTURE_2D;
//		GL11.glBindTexture(t, colourTexture);
//		if (multisampled)
//			GL32.glTexImage2DMultisample(colourTexture, 4, GL11.GL_RGBA8, width, height, false);
//		else
//			GL11.glTexImage2D(t, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
//					(ByteBuffer) null);
//
//		GL11.glTexParameteri(t, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(t, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(t, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//		GL11.glTexParameteri(t, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
//		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment, t, colourTexture, 0);
//		if (colorTextures.size() == attachment) {
//			colorTextures.add(colourTexture);
//		} else {
//			colorTextures.set(attachment, colourTexture);
//		}
//		return colourTexture;
//	}

	public int createColorAttachment(int attachment, boolean multisampled) {
		return createColorAttachment(attachment, GL11.GL_RGBA8, multisampled);
	}

	/**
	 * @param format a constant like GL11.GL_RGBA8
	 */
	public int createColorAttachment(int attachment, int format, boolean multisampled) {
		this.multisampled = multisampled;
		int colourBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer);
		if (multisampled)
			glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, format, width, height);
		else
			glRenderbufferStorage(GL30.GL_RENDERBUFFER, format, width, height);
		glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment, GL30.GL_RENDERBUFFER,
				colourBuffer);
		if (colorBuffers.size() == attachment) {
			colorBuffers.add(colourBuffer);
		} else {
			colorBuffers.set(attachment, colourBuffer);
		}
		return colourBuffer;
	}

	/*
	 * Creates a texture and sets it as the colour buffer attachment for this FBO.
	 * 
	 * @param type
	 */
	public int createTextureAttachment(int attachment, boolean multisampled) {
		return createTextureAttachment(attachment, GL11.GL_RGBA8, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, multisampled);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this FBO.
	 * 
	 * @param multisampled NOT SUPPORTED in OpenGL ES
	 */
	public int createTextureAttachment(int attachment, int internalformat, int format, int type, boolean multisampled) {
		this.multisampled = multisampled;
		int colourTexture = glGenTextures();
		int t = multisampled ? GL32.GL_TEXTURE_2D_MULTISAMPLE : GL11.GL_TEXTURE_2D;
		glBindTexture(t, colourTexture);
		if (multisampled)
			glTexImage2DMultisample(colourTexture, 4, internalformat, width, height, false);
		else
			glTexImage2D(t, 0, internalformat, width, height, 0, format, type, (ByteBuffer) null);
		glTexParameteri(t, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		glTexParameteri(t, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		glTexParameteri(t, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(t, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment, t, colourTexture, 0);
		if (colorTextures.size() == attachment) {
			colorTextures.add(colourTexture);
		} else {
			colorTextures.set(attachment, colourTexture);
		}
		return colourTexture;
	}

	/**
	 * Creates 6 textures for the cubemap and sets it as the colour buffer
	 * attachment for this FBO.
	 * 
	 * @param multisampled NOT SUPPORTED in OpenGL ES
	 */
	public int createCubeMapTextureAttachment(int attachment, boolean multisampled) {
		int texID = glGenTextures();
		glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		if (multisampled) {
			for (int i = 0; i < 6; i++)
				glTexImage2DMultisample(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 4, GL11.GL_RGBA8, width, width, true);
		} else {
			for (int i = 0; i < 6; i++)
				glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, width, width, 0, GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		}
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		if (colorTextures.size() == attachment) {
			colorTextures.add(texID);
		} else {
			colorTextures.set(attachment, texID);
		}
		return texID;
	}

	/**
	 * Adds a depth buffer to the FBO in the form of a render buffer. This can't be
	 * used for sampling in the shaders.
	 */
	public int createDepthBufferAttachment(boolean multisampled) {
		this.multisampled = multisampled;
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (multisampled) {
			glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, width, height);
		} else {
			glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		}
		glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		return depthBuffer;
	}

	/**
	 * @param multisampled doesn't really work. Idk if it even can be made working!
	 * @return
	 */
	public int createDepthBufferTextureAttachment(boolean multisampled) {
		int texture = glGenTextures();
		glBindTexture(GL11.GL_TEXTURE_2D, texture);
		if (multisampled)
			glTexImage2DMultisample(GL11.GL_TEXTURE_2D, 4, GL14.GL_DEPTH_COMPONENT16, width, height, false);
		else
			glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0, GL11.GL_DEPTH_COMPONENT,
					GL11.GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL13.GL_TEXTURE_BORDER, GL11.GL_COLOR);
		glTexParameterfv(GL11.GL_TEXTURE_2D, GL13.GL_TEXTURE_BORDER_COLOR, new float[] { 1, 1, 1, 1 });
		glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, texture, 0);
		depthTexture = texture;
		return texture;
	}

	/**
	 * Adds a depth & stencil buffer to the FBO in the form of a render buffer. This
	 * can't be used for sampling in the shaders.
	 */
	public int createDepthStencilBufferAttachment(boolean multisampled) {
		this.multisampled = multisampled;
		stencil = true;
		depthBuffer = GL30.glGenRenderbuffers();
		glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (multisampled) {
			glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL30.GL_DEPTH24_STENCIL8, width, height);
		} else {
			glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
		}
		glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
		return depthBuffer;
	}

	/**
	 * for cubemaps!
	 * 
	 * @param side 0-5
	 */
	public void createSideForRendering(int side, int attachment) {
		glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment,
				GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, colorTextures.get(attachment), 0);
	}

	private void determineDrawBuffers(int nrOfTargets) {
		IntBuffer drawBuffers = BufferUtils.createIntBuffer(nrOfTargets);
		for (int i = 0; i < nrOfTargets; i++)
			drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0 + i);
		drawBuffers.flip();
		glDrawBuffers(drawBuffers);
	}

	private float[] depthClear = new float[] { 1 };
	private float[][] colorClear;

	public void setClearColor(float r, float g, float b, float a) {
		setClearColor(0, r, g, b, a);
	}

	public void setClearColor(int renderTarget, float r, float g, float b, float a) {
		colorClear[renderTarget][0] = r;
		colorClear[renderTarget][1] = g;
		colorClear[renderTarget][2] = b;
		colorClear[renderTarget][3] = a;
	}

	/**
	 * BIND IT FIRST!
	 */
	public void clearColorBuffer(int i) {
		glClearBufferfv(GL11.GL_COLOR, i, colorClear[i]);
	}

	/**
	 * BIND IT FIRST!
	 */
	public void clearDepthBuffer() {
		glClearBufferfv(GL11.GL_DEPTH, 0, depthClear);
	}

	/*
	 * BIND IT FIRST!!!
	 */
	public void clearStencil() {
		glClearStencil(0);
		glClear(GL11.GL_STENCIL_BUFFER_BIT);
		// GL30.glClearBufferfv(GL11.GL_STENCIL, 0, stencilClear);
	}

	/**
	 * BIND IT FIRST!!!
	 */
	public void clearBuffers() {
		for (int i = 0; i < colorBuffers.size(); i++)
			clearColorBuffer(i);
		clearDepthBuffer();
		if (stencil) {
			clearStencil();
		}
	}

	@Override
	public void bind() {
		glBindFramebuffer(GL30.GL_FRAMEBUFFER, ID);
		glViewport(0, 0, width, height);
	}

	@Override
	public void unbind() {
		glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void delete() {
		glDeleteFramebuffers(ID);
		for (int i = 0; i < colorBuffers.size(); i++)
			glDeleteRenderbuffers(colorBuffers.get(0));
		for (int i = 0; i < colorTextures.size(); i++)
			glDeleteTextures(colorTextures.get(i));
		if (depthBuffer != 0)
			glDeleteRenderbuffers(depthBuffer);
		if (depthTexture != 0)
			glDeleteTextures(depthTexture);
	}

	public int getColorTexture(int i) {
		return colorTextures.get(i);
	}

	public int getDepthBuffer() {
		return depthBuffer;
	}

	public int getDepthTexture() {
		return depthTexture;
	}

	public int numColorTextures() {
		return colorTextures.size();
	}

	public int getColorBuffer(int i) {
		return colorBuffers.get(i);
	}

	public int numColorBuffers() {
		return colorBuffers.size();
	}

	public boolean isComplete() {
		return status() == GL30.GL_FRAMEBUFFER_COMPLETE;
	}

	public int status() {
		return glFramebufferStatus(GL30.GL_FRAMEBUFFER);
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void bindToRead(int attachment) {
		glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.ID);
		glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
	}

	public void bindToWrite() {
		glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.ID);
	}

	public void bindToWrite(int attachment) {
		bindToWrite();
		glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0 + attachment);
	}

//	public void blitToTex(int attachment, int tex) {
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
//		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.ID);
//		GL11.glReadBuffer(attachment);
//		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 0, 0, width, height, 0);
//	}

	public void blitTo(Fbo other, int colorAttachment, boolean depthBufferToo) {
		bindToRead(colorAttachment);
		other.bindToWrite(0);
		glBlitFramebuffer(0, 0, width, height, 0, 0, other.width(), other.height(),
				(depthBufferToo ? 0 : GL11.GL_DEPTH_BUFFER_BIT) | GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}

	public void blitToScreen(int i, Window w) {
		glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
//		glDrawBuffer(GL11.GL_BACK);
		bindToRead(i);
		glBlitFramebuffer(0, 0, width, height, 0, 0, w.width(), w.height(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbind();
	}

	public void blitToScreen(int i, Window main, boolean keepAspectRatio) {
		int xoffset = 0;
		int yoffset = 0;
		int width = main.width();
		int height = main.height();
		if (keepAspectRatio) {
			float ratio = (this.width / (float) this.height);
			float fact = (main.width() / (float) main.height()) / ratio;
			if (main.width() < main.height() * ratio) {
				yoffset = (int) (main.height() * (1 - fact) * 0.5f);
				xoffset = 0;
			} else {
				yoffset = 0;
				xoffset = (int) (main.width() * (1 - (1 / fact)) * 0.5f);
			}
			width -= xoffset;
			height -= yoffset;
		}
		glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		bindToRead(i);
		glBlitFramebuffer(0, 0, this.width, this.height, xoffset, yoffset, width, height, GL11.GL_COLOR_BUFFER_BIT,
				GL11.GL_NEAREST);
		this.unbind();
	}

//	public void blitDepthToScreen(Window w) {
//		glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
//		glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.ID);
//		glReadBuffer(GL30.GL_DEPTH_ATTACHMENT);
//		glBlitFramebuffer(0, 0, width, height, 0, 0, w.width(), w.height(), GL11.GL_DEPTH_BITS, GL11.GL_NEAREST);
//		this.unbind();
//	}

	public boolean multisampled() {
		return multisampled;
	}

	/**
	 * @param width
	 * @param height
	 * @param textureAttachment if you want to read this from shaders, true.
	 *                          Otherwise false is fine. There's often problems with
	 *                          this and multisampling enabled in a fbo at the same
	 *                          time
	 * @param multisampled
	 * @return
	 */
	public static Fbo createStandardFbo(int width, int height, boolean textureAttachment, boolean multisampled) {
		Fbo ret = new Fbo(width, height, 1);
		ret.createColorAttachment(0, multisampled);
		if (textureAttachment)
			ret.createTextureAttachment(0, multisampled);
		ret.createDepthBufferAttachment(multisampled);
		if (!ret.isComplete()) {
			new Exception("Fbo incomplete! Status: " + ret.status()).printStackTrace(AppFolder.log);
		}
		return ret;
	}

	/**
	 * @param window            gets width and height from it
	 * @param targets
	 * @param textureAttachment if you want to read this from shaders, true.
	 *                          Otherwise false is fine. There's often problems with
	 *                          this and multisampling enabled in a fbo at the same
	 *                          time
	 * @param multisampled
	 * @return
	 */
	public static Fbo createMultiTargetsFbo(Window window, int targets, boolean textureAttachments,
			boolean multisampled) {
		return createMultiTargetsFbo(window.width(), window.height(), targets, textureAttachments, multisampled);
	}

	/**
	 * @param width
	 * @param height
	 * @param targets
	 * @param textureAttachment if you want to read this from shaders, true.
	 *                          Otherwise false is fine. There's often problems with
	 *                          this and multisampling enabled in a fbo at the same
	 *                          time
	 * @param multisampled
	 * @return
	 */
	public static Fbo createMultiTargetsFbo(int width, int height, int targets, boolean textureAttachments,
			boolean multisampled) {
		return createMultiTargetsFbo(width, height, targets, textureAttachments, multisampled, false);
	}

	/**
	 * @param width
	 * @param height
	 * @param targets
	 * @param textureAttachment if you want to read this from shaders, true.
	 *                          Otherwise false is fine. There's often problems with
	 *                          this and multisampling enabled in a fbo at the same
	 *                          time
	 * @param multisampled
	 * @param HDR
	 * @return
	 */
	public static Fbo createMultiTargetsFbo(int width, int height, int targets, boolean textureAttachments,
			boolean multisampled, boolean HDR) {
		Fbo ret = new Fbo(width, height, targets);
		for (int i = 0; i < targets; i++) {
			if (textureAttachments)
				ret.createTextureAttachment(i, HDR ? GL30.GL_RGBA16F : GL11.GL_RGBA8, GL11.GL_RGBA,
						HDR ? GL11.GL_FLOAT : GL11.GL_UNSIGNED_BYTE, multisampled);
			ret.createColorAttachment(i, HDR ? GL30.GL_RGBA16F : GL11.GL_RGBA8, multisampled);
		}
		ret.createDepthBufferAttachment(multisampled);
		if (!ret.isComplete()) {
			new Exception("Fbo incomplete! Status: " + ret.status()).printStackTrace(AppFolder.log);
		}
		return ret;
	}

	public static Fbo createCubeMapFbo(int width) {
		Fbo ret = new Fbo(width, width);
//		ret.createColorAttatchment(0, false);
		ret.createCubeMapTextureAttachment(0, false);
		ret.createDepthBufferAttachment(false);
		return ret;
	}

	public static Fbo createShadowMapFbo(int width, int height) {
		Fbo ret = new Fbo(width, height, new int[0]);
		ret.createDepthBufferTextureAttachment(false);
		return ret;
	}

}
