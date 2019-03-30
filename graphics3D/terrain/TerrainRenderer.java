package graphics3D.terrain;

import generic.Thing;
import genericRendering.MultiRenderer;

public class TerrainRenderer extends MultiRenderer<Terrain, _TerrainRenderer> {

	protected static int lightsteps = 1;

	public TerrainRenderer() {
		this(8);
	}

	public TerrainRenderer(int MAX_LIGHTS) {
		this.maxpointlights = MAX_LIGHTS;
		add(new _TerrainRenderer(this.maxpointlights, this));

		if (lightsteps > 0) {
			MAX_LIGHTS = this.maxpointlights;
			while (MAX_LIGHTS > lightsteps) {
				// step down the amount of lights!
				MAX_LIGHTS -= lightsteps;// 2, 3, 1?
				add(new _TerrainRenderer(MAX_LIGHTS, this));
			}
		}
		if (this.maxpointlights > 0)
			add(new _TerrainRenderer(0, this));

//		for (int i = 0; i < renderers.size(); i++) {
//			AppFolder.log.println("" + renderers.get(i));
//		}

	}

	@Override
	public int priority(Thing m) {
		return m instanceof Terrain ? 1 : 0;
	}

}
