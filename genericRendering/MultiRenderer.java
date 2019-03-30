package genericRendering;

import java.util.*;

import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;
import openGlResources.shaders.ShaderProgram;

public abstract class MultiRenderer<ToRender extends Thing, ImplementedRenderer extends Renderer<ToRender, ? extends ShaderProgram>>
		extends EmptyRenderer<ToRender> {

	protected List<ImplementedRenderer> renderers = new ArrayList<>();
	protected List<ToRender> stillToAdd = new ArrayList<>();

	public MultiRenderer() {

	}

	protected void add(ImplementedRenderer r) {
		renderers.add(r);
		r.lights = this.lights;
	}

	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		return true;
	}

	@Override
	public void render(ToRender t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		if (renderers.size() == 0)
			return;
		renderers.get(getBestRenderer(t)).render(t, c, projMat, inverseViewportScaleMat, pipelineResults);
	}

	/*
	 * this method isn't implemented yet and probably never will!
	 */
	@Override
	public void renderAll(List<ToRender> l, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {

	}

	@Override
	public void renderAll(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		if (renderers.size() == 0)
			return;
		if (!startRendering(c, projMat, inverseViewportScaleMat, pipelineResults))
			return;
		for (int i = 0; i < renderers.size(); i++) {
			List<ToRender> l = renderers.get(i).toRender;
			for (int i2 = 0; i2 < l.size(); i2++) {
				int b = getBestRenderer(l.get(i2));
				if (b != i) {
					ToRender t = l.remove(i2);
					if (b > 0) {
						renderers.get(b).add(t);
					}
					i2--;
				}
			}
		}
		for (int i = stillToAdd.size() - 1; i >= 0; i--)
			add(stillToAdd.remove(i));
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).renderAll(c, projMat, inverseViewportScaleMat, pipelineResults);
		stopRendering(c, projMat, inverseViewportScaleMat, pipelineResults);
	}

	protected void stopRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {

	}

	@Override
	public void add(ToRender t) {
		renderers.get(getBestRenderer(t)).add(t);
	}

	@Override
	public boolean remove(ToRender t) {
		for (int i = 0; i < renderers.size(); i++)
			if (renderers.get(i).remove(t))
				return true;
		return false;
	}

	@Override
	public void cleanUp() {
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).cleanUp();
	}

	@Override
	public void clear() {
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).clear();
	}

	protected int getBestRenderer(ToRender t) {
		int maxprio = 0;
		int index = 0;
		for (int r = 0; r < renderers.size(); r++) {
			int p;
			if ((p = renderers.get(r).priority(t)) > maxprio) {
				maxprio = p;
				index = r;
			}
		}
		return index;
	}

	@Override
	public void forAll(ModelProcessor mp) {
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).forAll(mp);
	}

//	public void exportCode(StringBuilder b) {
//		for (int i = 0; i < renderers.size(); i++) {
//			renderers.get(i).exportCode(b);
//		}
//	}

}
