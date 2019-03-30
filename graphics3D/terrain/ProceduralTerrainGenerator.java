package graphics3D.terrain;

import java.util.*;

import org.joml.Vector2i;
import org.joml.Vector3f;

import graphics3D.Camera;
import graphics3D.terrain.Terrain.HeightGetter;
import graphics3D.terrain.Terrain.MaterialGetter;
import models.material.Material;
import tools.*;
import tools.misc.Vects;
import tools.misc.interfaces.BoolInterface;

public class ProceduralTerrainGenerator {

	protected TerrainRenderer tr;
	protected Camera c;
	protected Map<Vector2i, Terrain> map = new HashMap<>();
	protected ArrayList<Terrain> terrains = new ArrayList<>();
	protected ArrayList<Vector2i> keys = new ArrayList<>();
	protected float max_load_dist = 200;
	protected float min_dist_unload_in_view = max_load_dist * 2, min_dist_unload_out_of_view = max_load_dist * 1.1f;
	protected float terrainSize = 30, spacing = 1f;
	protected HeightGetter hg;
	protected MaterialGetter mg;
	protected Vector2i key = new Vector2i();
	protected GameThread generator;
	protected ArrayDeque<Terrain> toGenerate, finished;
	protected ArrayList<Material> materials = new ArrayList<>();

	/**
	 * @param generateMultiThreaded
	 * @param stopper               only required if generateMultiThreaded is true
	 */
	public ProceduralTerrainGenerator(Camera c, TerrainRenderer r, HeightGetter hg, boolean generateMultiThreaded,
			BoolInterface stopper) {
		this.tr = r;
		this.c = c;
		this.hg = hg;
		if (generateMultiThreaded) {
			toGenerate = new ArrayDeque<>();
			finished = new ArrayDeque<>();
			generator = new GameThread("Procedural Terrain Generator", 60) {
				@Override
				public void loop() {
					if (stopper.getBool()) {
						super.stopThread();
						return;
					}
					if (toGenerate.isEmpty())
						return;
					Terrain t = toGenerate.pop();
//					int i = terrains.indexOf(t);
//					Vector2i k = keys.get(i);

					t.generateHeightsOfCascadedTerrain(1, 1);
					t.updateModelData();
					finished.add(t);
				}
			};
			generator.start();
		}
	}

	/**
	 * @param generateMultiThreaded
	 * @param stopper               only required if generateMultiThreaded is true
	 */
	public ProceduralTerrainGenerator(Camera c, TerrainRenderer r, HeightGetter hg, boolean generateMultiThreaded,
			BoolInterface stopper, Material... materials) {
		this.tr = r;
		this.c = c;
		this.hg = hg;
		for (int i = 0; i < materials.length; i++)
			this.materials.add(materials[i]);
		if (generateMultiThreaded) {
			toGenerate = new ArrayDeque<>();
			finished = new ArrayDeque<>();
			generator = new GameThread("Procedural Terrain Generator", 60) {
				@Override
				public void loop() {
					if (stopper.getBool()) {
						super.stopThread();
						return;
					}
					if (toGenerate.isEmpty())
						return;
					Terrain t = toGenerate.pop();
//					int i = terrains.indexOf(t);
//					Vector2i k = keys.get(i);

					t.generateHeightsOfCascadedTerrain(1, 1);
					t.updateModelData();
					finished.add(t);
				}
			};
			generator.start();
		}
	}

	public void setLoadingDistance(float ld) {
		this.max_load_dist = ld;
		this.min_dist_unload_in_view = ld * 2;
		this.min_dist_unload_out_of_view = ld * 1.1f;
	}

	public void setMaterialGetter(MaterialGetter mg) {
		this.mg = mg;
	}

	public void addMaterial(Material m) {
		materials.add(m);
	}

	public void update(float frameTimeSeconds) {
		while (!finished.isEmpty()) {
			Terrain t = finished.pop();
			if (t != null) {
				terrains.add(t);
//				keys.add(e)
				t.updateModel();
			}
		}
		for (int i = 0; i < terrains.size(); i++) {
			Terrain t = terrains.get(i);
//			if (!c.fi().testAab(t.position().x, t.minHeight(), t.position().z, t.position().x + t.SIZE, t.maxHeight(),
//					t.position().z + t.SIZE)) {
//				if (t.shown()) {
//					t.shown(false);
//					tr.remove(t);
//				}
//			} else {
//				if (!t.shown()) {
//					t.shown(true);
//					tr.add(t);
//				}
//			}
			Vects.calcVect.set(c.position()).y = 0;
			float d = Vects.calcVect.distanceSquared(t.position().x + t.SIZE * 0.5f, 0, t.position().z + t.SIZE * 0.5f);
			if (d > min_dist_unload_in_view * min_dist_unload_in_view
					|| (t.shown() && d > min_dist_unload_out_of_view * min_dist_unload_out_of_view)) {
				if (t.vao() != null)
					t.vao().delete();
				terrains.remove(i);
				// IndexOutOfBounds: wenn man außerhalb der Weltgrenzen steht ?!?
				try {
					map.remove(keys.remove(i));
				} catch (Exception e) {
					AppFolder.log.println("some exception occurred whilst removing terrain from map!");
					e.printStackTrace(AppFolder.log);
					break;
				}
				i--;
			}
		}

		int cx = Meth.toInt(c.position().x / terrainSize), cz = Meth.toInt(c.position().z / terrainSize);
		outer: for (int r = 0; r < (max_load_dist / terrainSize); r++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					if (!map.containsKey(key.set(x + cx, z + cz))) {
						Vector2i k = new Vector2i(key);
						Terrain t = new Terrain(
								new Vector3f(Meth.toInt((x + cx) * terrainSize), 0, Meth.toInt((z + cz) * terrainSize)),
								terrainSize, spacing, hg, false, false);
						t.setMaterialGetter(mg);
						if (materials.size() > 0)
							t.setMaterial(materials.get(0));
						for (int i = 1; i < materials.size(); i++) {
							t.addSecondaryMaterial(materials.get(i));
						}
						map.put(k, t);
//						terrains.add(t);
						keys.add(k);
						tr.add(t);
						if (generator != null) {
							toGenerate.add(t);
						}
						break outer;
					}
				}
			}
		}
	}

	public void cleanUp() {
		for (int i = 0; i < terrains.size(); i++) {
			if (terrains.get(i).vao() != null)
				terrains.get(i).vao().delete();
		}
		terrains.clear();
		keys.clear();
		map.clear();
	}

	public float heightAt(float x, float z) {
		return hg.getHeight(x, z);
	}

	public TerrainRenderer getRenderer() {
		return tr;
	}

//	public Vector3f rayTrace(Vector3f start, Vector3f ray, float maxlength, Vector3f dest) {
//		dest.set(start);
//		
//		return dest;
//	}

}
