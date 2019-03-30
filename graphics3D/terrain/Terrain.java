package graphics3D.terrain;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector4f;

import collectionsStuff.ArrayListInt;
import generic.Thing;
import loaders.Loader;
import models.material.Material;
import tools.Meth;
import tools.misc.Vects;

public class Terrain extends Thing {

	// TODO refine this heavily. List<Material> and automatic generation of vao
	// mesh, setting of single vertices' material (weights-> 3x ?), setting of mesh
	// granularity, setting of size, etc

	public static interface HeightGetter {

		public float getHeight(float x, float z);

	}

	public static interface MaterialGetter {

		/**
		 * defines the material index at the specified (vertex) position. 0 being the
		 * default material and index 1+ being secondaryMaterials.get(i-1)
		 */
		public int getMaterial(float x, float y, float z);

	}

	protected float spacing;
	protected float SIZE;
	protected float[][] heights;
	protected float[] meshVertexData, meshNormals, materials;
	protected ArrayListInt indices;
	protected HeightGetter hg;
	protected MaterialGetter materialGetter;
	protected int minHeightI, minHeightJ, maxHeightI, maxHeightJ;
	protected float minHeight, maxHeight;
	protected ArrayList<Material> secondaryMaterials = new ArrayList<>();

	public Terrain(float size, float spacing, HeightGetter hg) {
		this(new Vector3f(), size, spacing, hg, true, true);
	}

	public Terrain(float x, float y, float z, float size, float spacing, HeightGetter hg) {
		this(new Vector3f(x, y, z), size, spacing, hg, true, true);
	}

	public Terrain(Vector3f pos, float size, float spacing, HeightGetter hg, boolean generateInConstructor,
			boolean loadInConstructor) {
		super(pos);
		doPhysics = false;
		this.SIZE = size;
		this.spacing = spacing;
		setIntrinsicSize(size);
		scale(1);
//		intrinsicMiddle
		this.hg = hg;
		int count = Meth.floor(SIZE / spacing) + 1;
		heights = new float[count][count];
		meshVertexData = new float[heights.length * heights.length * 3];
		meshNormals = new float[meshVertexData.length];
		materials = new float[heights.length * heights.length];

		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				meshVertexData[i * 3 * heights.length + j * 3] = i * spacing + position.x - SIZE / 2f;
				meshVertexData[i * 3 * heights.length + j * 3 + 2] = j * spacing + position.z - SIZE / 2f;
			}
		}
		if (generateInConstructor) {
			generateHeights();
			updateModelData();
		}
		indices = new ArrayListInt((heights.length - 1) * (heights.length - 1) * 6);
		for (int i = 0; i < heights.length - 1; i++) {
			for (int j = 0; j < heights.length - 1; j++) {
				indices.add(i * heights.length + j + 1);
				indices.add((i + 1) * heights.length + j);
				indices.add(i * heights.length + j);

				indices.add(i * heights.length + j + 1);
				indices.add((i + 1) * heights.length + j + 1);
				indices.add((i + 1) * heights.length + j);
			}
		}
		material = new Material(new Vector4f(1));
		if (loadInConstructor)
			updateModel();
	}

	public void setHeightGetter(HeightGetter hg) {
		this.hg = hg;
	}

	public boolean generateHeights() {
		boolean change = false;
		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				float hbefore = heights[i][j];
				heights[i][j] = position.y
						+ hg.getHeight(position.x + i * spacing - SIZE / 2f, position.z + j * spacing - SIZE / 2f);
				if (heights[i][j] != hbefore) {
					change = true;
					if (heights[i][j] < minHeight) {
						minHeight = heights[i][j];
						minHeightI = i;
						minHeightJ = j;
					} else if (heights[i][j] > maxHeight) {
						maxHeight = heights[i][j];
						maxHeightI = i;
						maxHeightJ = j;
					}
				}
			}
		}
		return change;
	}

	public boolean generateHeightsOfCascadedTerrain(int spacingMulXM, int spacingMulXP) {
		boolean change = false;
		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				float hbefore = heights[i][j];
				if (i == 0 && spacingMulXM != 1 && j % spacingMulXM != 0) {
					int j1 = (j / spacingMulXM) * spacingMulXM;
					int j2 = (j / spacingMulXM) * spacingMulXM + 1;
					float f1 = (j % spacingMulXM) / (float) spacingMulXM;
					heights[i][j] = position.y
							+ (hg.getHeight(position.x - SIZE / 2f, position.z + j1 * spacing - SIZE / 2f) * f1
									+ hg.getHeight(position.x - SIZE / 2f, position.z + j2 * spacing - SIZE / 2f)
											* (1 - f1));
				} else if (i == heights.length && spacingMulXP != 1 && j % spacingMulXP != 0) {
					int j1 = (j / spacingMulXP) * spacingMulXP;
					int j2 = (j / spacingMulXP) * spacingMulXP + 1;
					float f1 = (j % spacingMulXP) / (float) spacingMulXP;
					heights[i][j] = position.y
							+ (hg.getHeight(position.x - SIZE / 2f, position.z + j1 * spacing - SIZE / 2f) * f1
									+ hg.getHeight(position.x - SIZE / 2f, position.z + j2 * spacing - SIZE / 2f)
											* (1 - f1));
				} else {
					heights[i][j] = position.y
							+ hg.getHeight(position.x + i * spacing - SIZE / 2f, position.z + j * spacing - SIZE / 2f);
				}
				if (heights[i][j] != hbefore) {
					change = true;
					if (heights[i][j] < minHeight) {
						minHeight = heights[i][j];
						minHeightI = i;
						minHeightJ = j;
					} else if (heights[i][j] > maxHeight) {
						maxHeight = heights[i][j];
						maxHeightI = i;
						maxHeightJ = j;
					}
				}
			}
		}
		return change;
	}

	/**
	 * puts all the height values into the mesh and recalculates its normals. Also
	 * gets the per-vertex materials Nothing is uploaded here!
	 */
	public void updateModelData() {
		minHeight = Float.POSITIVE_INFINITY;
		maxHeight = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				float h = heights[i][j];
				meshVertexData[i * 3 * heights.length + j * 3 + 1] = h;
				if (h < minHeight)
					minHeight = h;
				if (h > maxHeight)
					maxHeight = h;

				Vects.calcVect.set(spacing, getChecked(i + 1, j) - h, 0).cross(0, getChecked(i, j + 1) - h, spacing);

				Vects.calcVect.normalize();
				if (Vects.calcVect.y < 0)
					Vects.calcVect.negate();

				meshNormals[i * 3 * heights.length + j * 3 + 0] = Vects.calcVect.x;
				meshNormals[i * 3 * heights.length + j * 3 + 1] = Vects.calcVect.y;
				meshNormals[i * 3 * heights.length + j * 3 + 2] = Vects.calcVect.z;

				int m = materialGetter == null ? 0
						: materialGetter.getMaterial(i * spacing + position.x, h, j * spacing + position.z);
				materials[i * heights.length + j] = m;
			}
		}
		intrinsicSize.y = maxHeight - minHeight;
		intrinsicSize.y *= 2;
		intrinsicMiddle.y = (maxHeight + minHeight) * 0.5f;
	}

	public void updateXZPositions() {
		for (int i = 0; i < heights.length; i++) {
			for (int j = 0; j < heights.length; j++) {
				meshVertexData[i * 3 * heights.length + j * 3] = i * spacing + position.x;
				meshVertexData[i * 3 * heights.length + j * 3 + 2] = j * spacing + position.z;
			}
		}
	}

	/**
	 * updates the model data generated by {@link Terrain#updateModelData()}. If no
	 * VAO is present it will be created.
	 */
	public void updateModel() {

		// TODO FIXME add textureCoords, dependent on world-space coordinates and the
		// texture-> materials with weights mapping stuff. Those maps best be easily
		// procedurally generatable, so you can use something similar to "HeightGetter"!
		// possibly add LOD mechanics ?
		// TODO also, pretty straightforward, add an array of Materials to the renderer
		// (and this...). Should make for much more interesting landscapes if there's
		// actually variation in color
		// TODO also add a class "ProceduralTerrainGenerator" that has a Camera and a
		// TerrainRenderer attached to it and automagically adds Terrain objects as the
		// Camera moves. HeightGetter could be renamed to "2DValueGenerator" and shall
		// be used here as a HeightGetter for the whole world. Also there should be a
		// HeightMap class that extends the "2DValueGenerator" and returns interpolated
		// height values. OR of course use an actual heightmap (that can also be
		// generated using a HeightGetter). Probably a bit faster and easier to
		// implement, too
		if (vao == null) {
			vao = Loader.loadToVAO(indices, new int[] { 3, 1, 3 }, meshVertexData, materials, meshNormals);
		} else {
			vao.getIndicesArray().updateIndices(indices);
			vao.getVbo(0).update(meshVertexData);
			vao.getVbo(1).update(materials);
			vao.getVbo(2).update(meshNormals);
		}
	}

	public float minHeight() {
		return minHeight;
	}

	public float maxHeight() {
		return maxHeight;
	}

	private float getChecked(int i, int j) {
		if (i >= 0 && i < heights.length && j >= 0 && j < heights.length)
			return heights[i][j];
		else
			return hg.getHeight(position.x + i * spacing - SIZE / 2f, position.z + j * spacing - SIZE / 2f);
	}

	public void setMaterialGetter(MaterialGetter mg) {
		this.materialGetter = mg;
	}

	public Material material(int i) {
		if (i == 0 || i > secondaryMaterials.size())
			return material;
		else
			return secondaryMaterials.get(i - 1);
	}

	public void addSecondaryMaterial(Material m) {
		secondaryMaterials.add(m);
	}

}
