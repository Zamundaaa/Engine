package models;

import collectionsStuff.ArrayListFloat;
import collectionsStuff.ArrayListInt;
import loaders.Loader;
import openGlResources.buffers.VAO;

public class ListModelData {

	protected ArrayListFloat positions;
	protected ArrayListFloat textureCoordinates;
	protected ArrayListFloat normals;
	protected ArrayListInt indices;
	protected String name;

	public ListModelData(ArrayListFloat positions, ArrayListFloat textureCoordinates, ArrayListFloat normals,
			ArrayListInt indices) {
		this.positions = positions;
		this.textureCoordinates = textureCoordinates;
		this.normals = normals;
		this.indices = indices;
	}

	public VAO loadToVAO() {
		VAO ret = Loader.loadToVAO(indices, new int[] { 3, 2, 3 }, positions, textureCoordinates, normals);
		ret.setName(name);
		return ret;
	}

	public ArrayListFloat positions() {
		return positions;
	}

	public void setPositions(ArrayListFloat vertices) {
		this.positions = vertices;
	}

	public ArrayListFloat textureCoords() {
		return textureCoordinates;
	}

	public void setTextureCoords(ArrayListFloat textureCoordinates) {
		this.textureCoordinates = textureCoordinates;
	}

	public ArrayListFloat normals() {
		return normals;
	}

	public void setNormals(ArrayListFloat normals) {
		this.normals = normals;
	}

	public ArrayListInt indices() {
		return indices;
	}

	public void setIndices(ArrayListInt indices) {
		this.indices = indices;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void clear() {
		positions.clear();
		textureCoordinates.clear();
		normals.clear();
		indices.clear();
	}

	public void addVertex(float x, float y, float z, float nx, float ny, float nz, float tx, float ty) {
		positions.add(x);
		positions.add(y);
		positions.add(z);
		normals.add(nx);
		normals.add(ny);
		normals.add(nz);
		textureCoordinates.add(tx);
		textureCoordinates.add(ty);
	}

}
