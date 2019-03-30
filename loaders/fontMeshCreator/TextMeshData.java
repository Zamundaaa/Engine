package loaders.fontMeshCreator;

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 * 
 * @author Karl
 *
 */
public class TextMeshData {

	private float[] vertexPositions;
	private float[] textureCoords;
	protected float modelLength, modelHeight;

	protected TextMeshData(float[] vertexPositions, float[] textureCoords, float modelLength, float modelHeight) {
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
		this.modelLength = modelLength;
		this.modelHeight = modelHeight;
	}

	public float[] getVertexPositions() {
		return vertexPositions;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public int getVertexCount() {
		return vertexPositions.length / 3;
	}

	public float modelLength() {
		return modelLength;
	}

	public float modelHeight() {
		return modelHeight;
	}

}
