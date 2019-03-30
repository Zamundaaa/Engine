package openGlResources.buffers;

import static openGlResources.CommonGL.*;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES20;

/**
 * represents an OpenGL VertexArrayObject. Has convenience methods for adding
 * VBOs, drawing etc
 * 
 * @author xaver
 */
public class VAO extends OpenGLBuffer {
	// completely OpenGL ES compliant!

	protected String name;
	protected ArrayList<VBO> vbos = new ArrayList<>(4);
	protected VBO indices;
	protected Vector3f middle = new Vector3f(), size = new Vector3f();

	/**
	 * creates a new VAO with an openGL handle. has to be deleted manually using
	 * {@link VAO#delete()}. Will be bound upon creation
	 */
	public VAO() {
		super(glGenVertexArrays());
		bind();
	}

	/**
	 * creates a new VAO with an openGL handle. has to be deleted manually using
	 * {@link VAO#delete()}. Will be bound upon creation
	 */
	public VAO(String name) {
		super(glGenVertexArrays());
		bind();
		this.name = name;
	}

	public void setIndicesArray(VBO v) {
		indices = v;
		bind();
		v.bindIndices();
	}

	public VBO getIndicesArray() {
		return indices;
	}

	public void setVbo(int attributeNumber, VBO v) {
		while (vbos.size() <= attributeNumber)
			vbos.add(null);
		bind();
		vbos.set(attributeNumber, v);
		v.setAttribPointer(attributeNumber);
		// AppFolder.log.println(
		// v.ID() + " is bound to attribute NR. " + attributeNumber + " & has
		// coord size " + v.coordinateSize() + " & is filled with " + v.count()
		// + " X");
	}

	public VBO getVbo(int attributeNumber) {
		return vbos.get(attributeNumber);
	}

	public void bind() {
		glBindVertexArray(ID);
	}

	/**
	 * binding the next VAO also unbinds the last, so this shouldn't be really
	 * necessary to call
	 */
	public void unbind() {
		glBindVertexArray(0);
	}

	public void delete() {
		glDeleteVertexArrays(ID);
	}

	public void drawElements_Triangles() {
		if (indices != null) {
			glDrawElements(GL11.GL_TRIANGLES, indices.count(), indices.dataType(), 0);
		}
	}

	public void drawArrays_Triangles() {
		VBO v = getVbo(0);
		if (v != null) {
			glDrawArrays(GLES20.GL_TRIANGLES, 0, v.count() / v.coordinateSize());
		}
	}

	public void drawArrays_Triangle_Strip() {
		VBO v = getVbo(0);
		if (v != null) {
			glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, v.count() / v.coordinateSize());
		}
	}

	public void addInstancedAtribute(VBO vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
		vbo.bind();
		bind();
		glVertexAttribPointer(attribute, dataSize, GLES20.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		glVertexAttribDivisor(attribute, 1);
		glEnableVertexAttribArray(attribute);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public Vector3f middle() {
		return middle;
	}

	public Vector3f size() {
		return size;
	}

}
