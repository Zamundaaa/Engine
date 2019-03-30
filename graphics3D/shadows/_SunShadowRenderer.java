package graphics3D.shadows;

import java.util.Map;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.Renderer;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;
import openGlResources.GLUtils;

public class _SunShadowRenderer extends Renderer<Thing, ShadowShader> {

	protected FrustumIntersection fi;
	public Matrix4f cm;
	
	protected _SunShadowRenderer(int width, int height, boolean animations, Matrix4f cm) {
		super(new ShadowShader(animations));
		this.cm = cm;
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
//		DirectionalLight sun = SunKeeper.sun;
		// shadow mapping tutorial 1/2: 14:45 if this doesn't work!
//		cm.setOrthoSymmetric(SHADOW_DISTANCE, SHADOW_DISTANCE, 0.01f, SHADOW_DISTANCE);
//		shader.calcMat.identity().lookAlong(sun.direction(), Vects.UP);
//		shader.calcMat.translate(Vects.calcVect().set(c.position()).add(0, SHADOW_DISTANCE * 0.5f, 0).negate());
//		cm.mul(shader.calcMat);
//		if (fi != null)
//			fi.set(cm);
		GLUtils.enableFrontFaceCulling();
		return true;
	}

	@Override
	protected boolean prepareFor(Thing t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		shader.projViewTransMat.set(cm.mul(t.createTransformationMatrix(shader.calcMat), shader.calcMat));
		if (shader.jointTransforms != null) {
			if (t.animation() != null) {
				if(t.animation().updateAutomatically())
					t.animation().updateAnimation();
				shader.jointTransforms.set(t.animation().getPose());
			}
		}
		return false;
	}

	@Override
	protected void stopRendering() {

	}

	@Override
	public int priority(Thing m) {
		return (m.animation() == null) == (shader.jointTransforms == null) ? 1 : 0;
	}

	@Override
	public int renderTiming() {
		return 0;
	}

	@Override
	public int pipeline() {
		return -1;// before the actual rendering!
	}

	@Override
	public void viewportResized(int width, int height) {
		super.viewportResized(width, height);
	}

}
