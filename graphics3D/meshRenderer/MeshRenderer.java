package graphics3D.meshRenderer;

import generic.Thing;
import genericRendering.MultiRenderer;

public class MeshRenderer extends MultiRenderer<Thing, _MeshRenderer> {

	protected static int lightsteps = 1;

	protected boolean autoIncreaseLights = true;

	public MeshRenderer() {
		this(8, true);
	}

	public MeshRenderer(int MAX_LIGHTS, boolean animations) {
		this.maxpointlights = MAX_LIGHTS;

		// oh. The translucent f*ckers have to be rendered last. That sh*t will decrease
		// performance...

		addAllRenderers(false, false);
		if (animations)
			addAllRenderers(false, true);

		addAllRenderers(true, false);
		if (animations)
			addAllRenderers(true, true);

//		for (int i = 0; i < renderers.size(); i++) {
//			AppFolder.log.println("" + renderers.get(i));
//		}

	}

	private void addAllRenderers(boolean translucent, boolean animations) {
		add(new _MeshRenderer(translucent, this.maxpointlights, animations, this));

		if (lightsteps > 0) {
			int MAX_LIGHTS = this.maxpointlights;
			while (MAX_LIGHTS > lightsteps) {
				// step down the amount of lights!
				MAX_LIGHTS -= lightsteps;// 2, 3, 1?
				add(new _MeshRenderer(translucent, MAX_LIGHTS, animations, this));
			}
		}

		if (this.maxpointlights > 0) {
			add(new _MeshRenderer(translucent, 0, animations, this));
		}
	}

	@Override
	protected int getBestRenderer(Thing t) {
		if (t == null)
			return -1;
		if (autoIncreaseLights && t.nearLights() > this.maxpointlights) {
			this.maxpointlights += lightsteps;
			add(new _MeshRenderer(false, this.maxpointlights, true, this));
			add(new _MeshRenderer(false, this.maxpointlights, false, this));

			add(new _MeshRenderer(true, this.maxpointlights, true, this));
			add(new _MeshRenderer(true, this.maxpointlights, false, this));
			// FIXME this will improve performance by a bit! sort by translucency first,
			// because duh, then animations, then max lights (shouldn't do much but wayne)
//			renderers.sort(new Comparator<Renderer<Mesh, ? extends ShaderProgram>>() {
//				@Override
//				public int compare(Renderer<Mesh, ? extends ShaderProgram> o1,
//						Renderer<Mesh, ? extends ShaderProgram> o2) {
//					return 0;
//				}
//			});
		}
		return super.getBestRenderer(t);
	}

	@Override
	public int renderTiming() {
		return 100;
	}

	@Override
	public int priority(Thing m) {
		return 1;
	}

}
