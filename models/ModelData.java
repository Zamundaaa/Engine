package models;

import static java.lang.Float.isFinite;
import static java.lang.Float.isNaN;

import loaders.Loader;
import openGlResources.buffers.VAO;

public class ModelData {

	protected float[] positions;
	protected float[] textureCoordinates;
	protected float[] normals;
	protected int[] indices;
	protected String name;
	protected VAO vao;

	public ModelData(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
		this.positions = positions;
		this.textureCoordinates = textureCoordinates;
		this.normals = normals;
		this.indices = indices;
	}

	public VAO loadToVAO() {
		VAO ret = Loader.loadToVAO(indices, new int[] { 3, 2, 3 }, positions, textureCoordinates, normals);
		ret.setName(name);
		float max = findMax(positions, 0, 3), mix = findMin(positions, 0, 3);
		float d = max - mix;
		if (!isNaN(d))
			ret.size().x = d;
		float may = findMax(positions, 1, 3), miy = findMin(positions, 1, 3);
		d = may - miy;
		if (!isNaN(d))
			ret.size().y = d;
		float maz = findMax(positions, 2, 3), miz = findMin(positions, 2, 3);
		d = maz - miz;
		if (!isNaN(d))
			ret.size().z = d;
		float mx = (max + mix) * 0.5f, my = (may + miy) * 0.5f, mz = (maz + miz) * 0.5f;
		if (isFinite(mx) && isFinite(my) && isFinite(mz)) {
			ret.middle().set(mx, my, mz);
		}
		return ret;
	}
	
	public VAO getVAO() {
		if(vao == null) {
			vao = loadToVAO();
		}
		return vao;
	}
	
	private static float findMax(float[] values, int start, int step) {
		float m = Float.NEGATIVE_INFINITY;
		for (int i = start; i < values.length; i += step) {
			if (values[i] > m) {
				m = values[i];
			}
		}
		return m;
	}

	private static float findMin(float[] values, int start, int step) {
		float m = Float.POSITIVE_INFINITY;
		for (int i = start; i < values.length; i += step) {
			if (values[i] < m) {
				m = values[i];
			}
		}
		return m;
	}

	public float[] positions() {
		return positions;
	}

	public void setPositions(float[] vertices) {
		this.positions = vertices;
	}

	public float[] textureCoords() {
		return textureCoordinates;
	}

	public void setTextureCoords(float[] textureCoordinates) {
		this.textureCoordinates = textureCoordinates;
	}

	public float[] normals() {
		return normals;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public int[] indices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

}
