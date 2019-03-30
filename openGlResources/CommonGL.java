package openGlResources;

import static window.GLHandler.ES;

import java.nio.*;

import org.lwjgl.opengl.*;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES30;

import window.GLHandler;

/**
 * 
 * this class contains commonly used openGL functions so you don't have to do
 * if(GLHandler.ES()) ... else ... yourself. The GL/ES version of the function
 * is documented in Javadoc so you can keep to your boundaries
 * 
 * @author xaver
 *
 */
public class CommonGL {

	/**
	 * GL11/GLES20
	 */
	public static void glViewport(int x, int y, int width, int height) {
		if (ES())
			GLES20.glViewport(x, y, width, height);
		else
			GL11.glViewport(x, y, width, height);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glClearColor(float r, float g, float b, float a) {
		if (ES())
			GLES20.glClearColor(r, g, b, a);
		else
			GL11.glClearColor(r, g, b, a);
	}

	/**
	 * GL11/GLES20. Just use the constants from either one
	 */
	public static void glEnable(int target) {
		if (ES())
			GLES20.glEnable(target);
		else
			GL11.glEnable(target);
	}

	/*
	 * GL11/GLES20. Just use the constants from either one
	 */
	public static void glDisable(int target) {
		if (ES())
			GLES20.glDisable(target);
		else
			GL11.glDisable(target);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glDepthMask(boolean flag) {
		if (ES())
			GLES20.glDepthMask(flag);
		else
			GL20.glDepthMask(flag);
	}

	/**
	 * GL11. NOT AVAILABLE ON GLES!!!
	 */
	public static void glPolygonMode(int face, int mode) {
		if (ES())
			;
		else
			GL11.glPolygonMode(face, mode);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		if (ES())
			GLES20.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
		else
			GL20.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	public static void glCullFace(int mode) {
		if (ES())
			GLES20.glCullFace(mode);
		else
			GL20.glCullFace(mode);
	}

	/**
	 * GL11/GLES20. Just use the constants from either one
	 */
	public static void glClear(int bitmask) {
		if (ES())
			GLES20.glClear(bitmask);
		else
			GL11.glClear(bitmask);
	}

	/**
	 * GL30/GLES20
	 */
	public static int glGenFramebuffers() {
		return GLHandler.ES() ? GLES20.glGenFramebuffers() : GL30.glGenFramebuffers();
	}

	/**
	 * GL30/GLES20
	 */
	public static void glBindRenderbuffer(int target, int renderbuffer) {
		if (ES())
			GLES20.glBindRenderbuffer(target, renderbuffer);
		else
			GL30.glBindRenderbuffer(target, renderbuffer);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width,
			int height) {
		if (ES())
			GLES30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
		else
			GL30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glRenderbufferStorage(int target, int internalformat, int width, int height) {
//		AppFolder.log.println("renderbuffer ID: " + glGetInteger(GL30.GL_RENDERBUFFER_BINDING));
//		new Exception().printStackTrace(AppFolder.log);
		if (ES())
			GLES20.glRenderbufferStorage(target, internalformat, width, height);
		else
			GL30.glRenderbufferStorage(target, internalformat, width, height);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
		if (ES())
			GLES20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
		else
			GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		if (ES())
			GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
		else
			GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glDrawBuffers(IntBuffer drawBuffers) {
		if (ES())
			GLES30.glDrawBuffers(drawBuffers);
		else
			GL30.glDrawBuffers(drawBuffers);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glClearBufferfv(int buffer, int drawbuffer, float[] value) {
		if (ES())
			GLES30.glClearBufferfv(buffer, drawbuffer, value);
		else
			GL30.glClearBufferfv(buffer, drawbuffer, value);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glClearStencil(int value) {
		if (ES())
			GLES20.glClearStencil(value);
		else
			GL11.glClearStencil(value);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glBindFramebuffer(int target, int framebuffer) {
		if (ES())
			GLES20.glBindFramebuffer(target, framebuffer);
		else
			GL30.glBindFramebuffer(target, framebuffer);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glDeleteFramebuffers(int framebuffer) {
		if (ES())
			GLES20.glDeleteFramebuffers(framebuffer);
		else
			GL30.glDeleteFramebuffers(framebuffer);
	}

	/**
	 * GL30/GLES20
	 */
	public static void glDeleteRenderbuffers(int framebuffer) {
		if (ES())
			GLES20.glDeleteRenderbuffers(framebuffer);
		else
			GL30.glDeleteRenderbuffers(framebuffer);
	}

	/**
	 * GL30/GLES20
	 */
	public static int glFramebufferStatus(int target) {
		return ES() ? GLES20.glCheckFramebufferStatus(target) : GL30.glCheckFramebufferStatus(target);
	}

	/**
	 * GL11/GLES30
	 */
	public static void glReadBuffer(int src) {
		if (ES())
			GLES30.glReadBuffer(src);
		else
			GL11.glReadBuffer(src);
	}

	/**
	 * GL11. This apparently isn't a thing in OpenGL ES
	 */
	public static void glDrawBuffer(int target) {
		if (ES())
			;// GLES30.glDrawBuffers(target)
		else
			GL11.glDrawBuffer(target);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1,
			int dstY1, int mask, int filter) {
		if (ES())
			GLES30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
		else
			GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	/**
	 * GL30/GLES20
	 */
	public static int glGenRenderbuffers() {
		return ES() ? GLES20.glGenRenderbuffers() : GL30.glGenRenderbuffers();
	}

	// framebuffer stuff end

	/**
	 * GL11/GLES20
	 */
	public static void glDeleteTextures(int framebuffer) {
		if (ES())
			GLES20.glDeleteTextures(framebuffer);
		else
			GL11.glDeleteTextures(framebuffer);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glBindTexture(int target, int texID) {
		if (ES()) {
			GLES20.glBindTexture(target, texID);
		} else {
			GL11.glBindTexture(target, texID);
		}
	}

	/**
	 * GL11/GLES20
	 */
	public static void glTexParameteri(int texture_target, int target_value, int value) {
		if (ES()) {
			GLES20.glTexParameteri(texture_target, target_value, value);
		} else {
			GL11.glTexParameteri(texture_target, target_value, value);
		}
	}

	/**
	 * GL11/GLES20
	 */
	public static void glTexParameterf(int texture_target, int target_value, float value) {
		if (ES()) {
			GLES20.glTexParameterf(texture_target, target_value, value);
		} else {
			GL11.glTexParameterf(texture_target, target_value, value);
		}
	}
	
	/**
	 * GL11/GLES20
	 */
	public static void glTexParameterfv(int texture_target, int target_value, float[] values) {
		if (ES()) {
			GLES20.glTexParameterfv(texture_target, target_value, values);
		} else {
			GL11.glTexParameterfv(texture_target, target_value, values);
		}
	}

	/**
	 * GL11/GLES20
	 */
	public static int glGenTextures() {
		return ES() ? GLES20.glGenTextures() : GL11.glGenTextures();
	}

	/**
	 * ONLY GL32!!!
	 */
	public static void glTexImage2DMultisample(int target, int samples, int internalformat, int width, int height,
			boolean fixedsamplelocations) {
		if (ES())
			throw new IllegalStateException("glTexImage2DMultisample is not supported in OpenGL ES!");
//		if (ES())
//			GLES31.glTexImage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
//		else
		GL32.glTexImage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int format, int type, ByteBuffer pixels) {
		if (ES())
			GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		else
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int format, int type, FloatBuffer pixels) {
		if (ES())
			GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		else
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	/**
	 * GL30/GLES30
	 */
	public static int glGenVertexArrays() {
		return GLHandler.ES() ? GLES30.glGenVertexArrays() : GL30.glGenVertexArrays();
	}

	/**
	 * GL30/GLES30
	 */
	public static void glBindVertexArray(int array) {
		if (GLHandler.ES())
			GLES30.glBindVertexArray(array);
		else
			GL30.glBindVertexArray(array);
	}

	public static void glDeleteVertexArrays(int arrays) {
		if (GLHandler.ES())
			GLES30.glDeleteVertexArrays(arrays);
		else
			GL30.glDeleteVertexArrays(arrays);
	}

	/**
	 * GL11/GLES20
	 * 
	 * @param indices offset of indices
	 */
	public static void glDrawElements(int mode, int count, int type, long indices) {
		if (ES())
			GLES20.glDrawElements(mode, count, type, indices);
		else
			GL11.glDrawElements(mode, count, type, indices);
	}

	/**
	 * GL11/GLES20
	 */
	public static void glDrawArrays(int mode, int first, int count) {
		if (ES())
			GLES20.glDrawArrays(mode, first, count);
		else
			GL11.glDrawArrays(mode, first, count);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
			long pointer) {
		if (ES())
			GLES20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
		else
			GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glVertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
		if (ES())
			GLES30.glVertexAttribIPointer(index, size, type, stride, pointer);
		else
			GL30.glVertexAttribIPointer(index, size, type, stride, pointer);
	}

	/**
	 * GL33/GLES30
	 */
	public static void glVertexAttribDivisor(int index, int divisor) {
		if (ES())
			GLES30.glVertexAttribDivisor(index, divisor);
		else
			GL33.glVertexAttribDivisor(index, divisor);
	}

	/**
	 * GL30/GLES30
	 */
	public static void glEnableVertexAttribArray(int index) {
		if (ES())
			GLES30.glEnableVertexAttribArray(index);
		else
			GL30.glEnableVertexAttribArray(index);
	}

	/**
	 * GL15/GLES20
	 */
	public static int glGenBuffers() {
		return ES() ? GLES20.glGenBuffers() : GL15.glGenBuffers();
	}

	/**
	 * GL15/GLES20
	 */
	public static void glDeleteBuffers(int buffer) {
		if (ES())
			GLES20.glDeleteBuffers(buffer);
		else
			GL15.glDeleteBuffers(buffer);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBindBuffer(int target, int buffer) {
		if (ES())
			GLES20.glBindBuffer(target, buffer);
		else
			GL15.glBindBuffer(target, buffer);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, ByteBuffer data, int usage) {
		if (ES())
			GLES20.glBufferData(target, data, usage);
		else
			GL15.glBufferData(target, data, usage);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, FloatBuffer data, int usage) {
		if (ES())
			GLES20.glBufferData(target, data, usage);
		else
			GL15.glBufferData(target, data, usage);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, IntBuffer data, int usage) {
		if (ES())
			GLES20.glBufferData(target, data, usage);
		else
			GL15.glBufferData(target, data, usage);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, float[] data, int usage) {
		if (ES())
			GLES20.glBufferData(target, data, usage);
		else
			GL15.glBufferData(target, data, usage);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, int[] data, int usage) {
		if (ES())
			GLES20.glBufferData(target, data, usage);
		else
			GL15.glBufferData(target, data, usage);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferSubData(int target, long offset, FloatBuffer data) {
		if (ES())
			GLES20.glBufferSubData(target, offset, data);
		else
			GL15.glBufferSubData(target, offset, data);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferSubData(int target, long offset, IntBuffer data) {
		if (ES())
			GLES20.glBufferSubData(target, offset, data);
		else
			GL15.glBufferSubData(target, offset, data);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferSubData(int target, long offset, float[] data) {
		if (ES())
			GLES20.glBufferSubData(target, offset, data);
		else
			GL15.glBufferSubData(target, offset, data);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferSubData(int target, long offset, int[] data) {
		if (ES())
			GLES20.glBufferSubData(target, offset, data);
		else
			GL15.glBufferSubData(target, offset, data);
	}

	/**
	 * GL15/GLES20
	 */
	public static void glBufferData(int target, long size, int usage) {
		if (ES())
			GLES20.glBufferData(target, size, usage);
		else
			GL15.glBufferData(target, size, usage);
	}

	/**
	 * GL13/GLES20
	 * 
	 * @param texture GL_TEXTURE[0-31]
	 */
	public static void glActiveTexture(int texture) {
		if (GLHandler.ES())
			GLES20.glActiveTexture(texture);
		else
			GL13.glActiveTexture(texture);
	}

	public static void glGenerateMipmap(int target) {
		if (GLHandler.ES())
			GLES20.glGenerateMipmap(target);
		else
			GL30.glGenerateMipmap(target);
	}

	// shader stuff!

	/**
	 * GL20/GLES20
	 */
	public static int glCreateShader(int type) {
		return ES() ? GLES20.glCreateShader(type) : GL20.glCreateShader(type);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glShaderSource(int shader, String source) {
		if (ES())
			GLES20.glShaderSource(shader, source);
		else
			GL20.glShaderSource(shader, source);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glCompileShader(int shader) {
		if (ES())
			GLES20.glCompileShader(shader);
		else
			GL20.glCompileShader(shader);
	}

	/**
	 * GL20/GLES20
	 */
	public static int glGetShaderi(int shader, int pname) {
		return ES() ? GLES20.glGetShaderi(shader, pname) : GL20.glGetShaderi(shader, pname);
	}

	/**
	 * GL20/GLES20
	 */
	public static String glGetShaderInfoLog(int shader) {
		return ES() ? GLES20.glGetShaderInfoLog(shader) : GL20.glGetShaderInfoLog(shader);
	}

	/**
	 * GL20/GLES20
	 */
	public static int glCreateProgram() {
		return ES() ? GLES20.glCreateProgram() : GL20.glCreateProgram();
	}

	/**
	 * GL20/GLES20
	 */
	public static void glAttachShader(int program, int shader) {
		if (ES())
			GLES20.glAttachShader(program, shader);
		else
			GL20.glAttachShader(program, shader);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glLinkProgram(int program) {
		if (ES())
			GLES20.glLinkProgram(program);
		else
			GL20.glLinkProgram(program);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glValidateProgram(int program) {
		if (ES())
			GLES20.glValidateProgram(program);
		else
			GL20.glValidateProgram(program);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUseProgram(int program) {
//		if (program > 0 && program < 10 && program % 3 == 0) {
//			AppFolder.log.println(program);
//			new Exception().printStackTrace(AppFolder.log);
//		}
		if (ES())
			GLES20.glUseProgram(program);
		else
			GL20.glUseProgram(program);
	}

	/**
	 * GL20/GLES20
	 */
	public static int glGetUniformLocation(int program, String uniformName) {
		return ES() ? GLES20.glGetUniformLocation(program, uniformName)
				: GL20.glGetUniformLocation(program, uniformName);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniform1i(int location, int value) {
		if (ES())
			GLES20.glUniform1i(location, value);
		else
			GL20.glUniform1i(location, value);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniform1f(int location, float value) {
		if (ES())
			GLES20.glUniform1f(location, value);
		else
			GL20.glUniform1f(location, value);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniform2f(int location, float x, float y) {
		if (ES())
			GLES20.glUniform2f(location, x, y);
		else
			GL20.glUniform2f(location, x, y);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniform3f(int location, float x, float y, float z) {
		if (ES())
			GLES20.glUniform3f(location, x, y, z);
		else
			GL20.glUniform3f(location, x, y, z);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniform4f(int location, float x, float y, float z, float w) {
		if (ES())
			GLES20.glUniform4f(location, x, y, z, w);
		else
			GL20.glUniform4f(location, x, y, z, w);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glUniformMatrix4fv(int location, boolean transpose, float[] values) {
		if (ES())
			GLES20.glUniformMatrix4fv(location, transpose, values);
		else
			GL20.glUniformMatrix4fv(location, transpose, values);
	}

	/**
	 * GL20/GLES20
	 */
	public static int glGetProgrami(int program, int pname) {
		return ES() ? GLES20.glGetProgrami(program, pname) : GL20.glGetProgrami(program, pname);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glGetProgramiv(int program, int pname, int[] dest) {
		if (ES())
			GLES20.glGetProgramiv(program, pname, dest);
		else
			GL20.glGetProgramiv(program, pname, dest);
	}

	/**
	 * GL20/GLES20
	 */
	public static void glBindAttribLocation(int program, int index, String name) {
		if (ES())
			GLES20.glBindAttribLocation(program, index, name);
		else
			GL20.glBindAttribLocation(program, index, name);
	}

	/**
	 * GL20/GLES20
	 */
	public static String glGetProgramInfoLog(int program) {
		return ES() ? GLES20.glGetProgramInfoLog(program) : GL20.glGetProgramInfoLog(program);
	}

	public static int glGetInteger(int pname) {
		return ES() ? GLES20.glGetInteger(pname) : GL20.glGetInteger(pname);
	}

}
