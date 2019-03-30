package genericRendering;

import java.util.*;

import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.lights.Light;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;

/**
 * called empty because no storage of 'ToRender's is included, so you can
 * implement your own if you'd like to use something else than a list
 * 
 * @author xaver
 *
 * @param <ToRender>
 */
public abstract class EmptyRenderer<ToRender extends Thing> {
	
	protected List<Light> lights = new ArrayList<>(), relevantLights = new ArrayList<>();
	protected int maxpointlights = 5;
	protected boolean noWireframe = false, cullModels = true;

 	public EmptyRenderer() {

	}

	public abstract void render(ToRender t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat, Map<String, PipelineResult> pipelineResults);

	public abstract void renderAll(List<ToRender> l, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults);

	public abstract void renderAll(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults);

	public abstract void add(ToRender t);

	public abstract boolean remove(ToRender t);

	public abstract int priority(Thing m);

	/**
	 * @return a number indicating if this renderer should be used more in the
	 *         beginning of the rendering process (lower number) or in the end
	 *         (higher number). default is 0
	 */
	public int renderTiming() {
		return 0;
	}

	/**
	 * @return a number indicating which pipeline this belongs to. 0 means that it's
	 *         part of the standard rendering pipeline - models in, picture out.
	 *         after that there's postprocessing - usually 1, but could be multiple
	 *         stages. Before it there could be some stages as well, like -1 for
	 *         shadows. The difference between {@link EmptyRenderer#RenderTiming()}
	 *         is that every model gets inserted in every stage on its own. That is
	 *         necessary for shadows for example to be even possible. Or a grass
	 *         renderer. Or whatever. Be aware that
	 */
	public int pipeline() {
		return 0;
	}

	public boolean addLight(Light l) {
		lights.add(l);
		return true;
	}

	public boolean removeLight(Light l) {
		return lights.remove(l);
	}

	public List<Light> lightList() {
		return lights;
	}

	public abstract void cleanUp();

	/**
	 * -1 if this EmptyRenderer doesn't store models itself. If it does usually the
	 * count of models stored (for example in a list)
	 */
	public int modelCount() {
		return -1;
	}

	public void noWireframe(boolean noWireframe) {
		this.noWireframe = noWireframe;
	}

	public boolean noWireframe() {
		return noWireframe;
	}

	public abstract void clear();

	public int maxpointlights() {
		return this.maxpointlights;
	}

	public boolean cullModels() {
		return cullModels;
	}

	/**
	 * you can use this to resize Fbos etc
	 */
	public void viewportResized(int width, int height) {

	}

	public void addPipelineResults(List<PipelineResult> results) {

	}

	public static interface ModelProcessor {
		public void process(Thing m);
	}

	public abstract void forAll(ModelProcessor mp);

//	public abstract void exportCode(StringBuilder b);

}
