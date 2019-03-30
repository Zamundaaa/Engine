package openGlResources.buffers;

import static openGlResources.CommonGL.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengles.GLES20;

import collectionsStuff.ArrayListFloat;
import collectionsStuff.ArrayListInt;
import window.GLHandler;

/**
 * represents an OpenGL VertexBufferObject. Has convenience methods for
 * uploading etc. Is completely OpenGL ES compliant!
 * 
 * @author xaver
 */
public class VBO extends OpenGLBuffer {

	protected int count;
	protected int coordinateSize;
	protected int dataType;

	/**
	 * creates a new VBO with an openGL handle. has to be deleted manually using
	 * {@linkplain VBO#delete()}
	 */
	public VBO() {
		super(glGenBuffers());
	}

	public void delete() {
		glDeleteBuffers(ID);
	}

	/**
	 * automatically set when created through static createX(...)
	 */
	public void setCount(int count) {
		this.count = count;
	}

	public int count() {
		return count;
	}

	public void setDataType(int type) {
		dataType = type;
	}

	public int dataType() {
		return dataType;
	}

	public void setCoordinateSize(int dimensions) {
		coordinateSize = dimensions;
	}

	public int coordinateSize() {
		return coordinateSize;
	}

	public void bindIndices() {
		glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ID);
	}

	public void unbindIndices() {
		glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void bind() {
		glBindBuffer(GL15.GL_ARRAY_BUFFER, ID);
	}

	public void unbind() {
		glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void setAttribPointer(int attributeNumber) {
		if (dataType == GL11.GL_FLOAT) {// || dataType == GLES20.GL_FLOAT) {
			glVertexAttribPointer(attributeNumber, coordinateSize(), GL11.GL_FLOAT, false, 0, 0);
		} else {
			glVertexAttribIPointer(attributeNumber, coordinateSize(), GL11.GL_INT, 0, 0);
		}
		glEnableVertexAttribArray(attributeNumber);
	}

	public VBO update(float[] data) {
		bind();
		if (data.length == count)
			glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
		else
			glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STREAM_DRAW);
		setDataType(GLHandler.ES() ? GLES20.GL_FLOAT : GL15.GL_FLOAT);
		setCount(data.length);
		return this;
	}

	public VBO update(float[] data, int length) {
		bind();
		if (length == count && data.length == length) {
			glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
		} else {
			FloatBuffer d = BufferBuffer.getFloatBuffer(length);
			d.clear();
			d.put(data, 0, length);
			d.flip();
			if (length == count)
				glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, d);
			else
				glBufferData(GL15.GL_ARRAY_BUFFER, d, GL15.GL_STREAM_DRAW);
//			glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STREAM_DRAW);
		}
		setDataType(GLHandler.ES() ? GLES20.GL_FLOAT : GL15.GL_FLOAT);
		setCount(data.length);
		return this;
	}

	public VBO updateIndices(ArrayListInt indices) {
		return updateIndices(indices.getArray(), indices.size());
	}

	public VBO updateIndices(int[] indices, int length) {
		bindIndices();
		if (indices.length == length && length == count) {
			glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indices);
		} else {
			IntBuffer d = BufferBuffer.getIntBuffer(length);
			d.clear();
			d.put(indices, 0, length);
			d.flip();
			if (length == count)
				glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, d);
			else
				glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, d, GL15.GL_STREAM_DRAW);
		}
		setDataType(GLHandler.ES() ? GLES20.GL_UNSIGNED_INT : GL15.GL_UNSIGNED_INT);
		setCount(length);
		setCoordinateSize(1);
		return this;
	}

	// STATIC STUFF!

	public static VBO createEmptyVBO(int size, int usage) {
		VBO ret = new VBO();
		ret.bind();
		glBufferData(GL15.GL_ARRAY_BUFFER, size, usage);
		return ret;
	}

	public static VBO createStaticVertexDataVBO(float[] data, int dimensions) {
		return createVertexDataVBO(data, dimensions, data.length,
				GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createStaticVertexDataVBO(float[] data, int length, int dimensions) {
		return createVertexDataVBO(data, dimensions, length,
				GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createVertexDataVBO(float[] data, int dimensions, int usage) {
		return createVertexDataVBO(data, dimensions, data.length, usage);
	}

	public static VBO createVertexDataVBO(float[] data, int dimensions, int length, int usage) {
		VBO ret = new VBO();
		ret.bind();
		FloatBuffer d = BufferBuffer.getFloatBuffer(length);
		d.put(data, 0, length);
		d.flip();
		glBufferData(GL15.GL_ARRAY_BUFFER, d, usage);
		ret.setDataType(GLHandler.ES() ? GLES20.GL_FLOAT : GL15.GL_FLOAT);
		ret.setCount(length);
		ret.setCoordinateSize(dimensions);
		return ret;
	}

	public static VBO createStaticVertexDataVBO(int[] data, int dimensions) {
		return createVertexDataVBO(data, dimensions, data.length,
				GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createStaticVertexDataVBO(int[] data, int length, int dimensions) {
		return createVertexDataVBO(data, dimensions, length,
				GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createStaticVertexDataVBO(ArrayListFloat data, int dimensions) {
		return createStaticVertexDataVBO(data.getArray(), data.size(), dimensions);
	}

	public static VBO createVertexDataVBO(int[] data, int dimensions, int usage) {
		return createVertexDataVBO(data, dimensions, data.length, usage);
	}

	public static VBO createVertexDataVBO(int[] data, int dimensions, int length, int usage) {
		VBO ret = new VBO();
		ret.bind();
		IntBuffer d = BufferBuffer.getIntBuffer(length);
		d.put(data);
		d.flip();
		glBufferData(GL15.GL_ARRAY_BUFFER, d, usage);
		ret.setDataType(GLHandler.ES() ? GLES20.GL_INT : GL15.GL_INT);
		ret.setCount(length);
		ret.setCoordinateSize(dimensions);
		return ret;
	}

	public static VBO createStaticIndicesVBO(int[] data) {
		return createIndicesVBO(data, data.length, GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createStaticIndicesVBO(int[] data, int length) {
		return createIndicesVBO(data, length, GLHandler.ES() ? GLES20.GL_STATIC_DRAW : GL15.GL_STATIC_DRAW);
	}

	public static VBO createStaticIndicesVBO(ArrayListInt indices) {
		return createStaticIndicesVBO(indices.getArray(), indices.size());
	}

	public static VBO createIndicesVBO(int[] data, int usage) {
		return createIndicesVBO(data, data.length);
	}

	public static VBO createIndicesVBO(int[] data, int length, int usage) {
		VBO ret = new VBO();
		ret.bindIndices();
		// TODO enable optimization of GL_UNSIGNED_SHORT as a way of saving on bandwidth
		// (and VRAM)
//		if (length < Short.MAX_VALUE) {
//			for(int i = 0; i < data.length; i++) {
//				
//			}
//		}
		IntBuffer d = BufferBuffer.getIntBuffer(length);
		d.clear();
		d.put(data, 0, length);
		d.flip();
		glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, d, usage);
		ret.setDataType(GLHandler.ES() ? GLES20.GL_UNSIGNED_INT : GL15.GL_UNSIGNED_INT);
		ret.setCount(length);
		ret.setCoordinateSize(1);
		return ret;
	}

}
