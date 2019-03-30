package graphics3D.shadows;

import java.util.Map;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.MultiRenderer;
import genericRendering.lights.DirectionalLight;
import genericRendering.pipeline.PipelineResult;
import genericRendering.pipeline.ShadowMap;
import graphics3D.Camera;
import graphics3D.proceduralSkybox.SunKeeper;
import openGlResources.GLUtils;
import openGlResources.buffers.Fbo;
import tools.misc.Vects;
import window.Window;

public class SunShadowRenderer extends MultiRenderer<Thing, _SunShadowRenderer> {

	protected int shadowMapSize = 2048 * 4;
	protected float SHADOW_DISTANCE = 100;
	protected Fbo shadowMap;
	protected Matrix4f cm = new Matrix4f();
	protected Matrix4f cm2 = new Matrix4f();
	protected FrustumIntersection fi;
	protected float width, height, aspect;
	protected ShadowMap result;

	public SunShadowRenderer(Window w) {
		this(w.width(), w.height());
	}

	public SunShadowRenderer(int width, int height) {
		super();
		shadowMap = Fbo.createShadowMapFbo(shadowMapSize, shadowMapSize);// doesn't need to be w*h
		add(new _SunShadowRenderer(width, height, false, cm));
		add(new _SunShadowRenderer(width, height, true, cm));

		this.width = width;
		this.height = height;
		this.aspect = height / (float) width;
		result = new ShadowMap("sunShadow", shadowMap, new Matrix4f());
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		pipelineResults.put("sunShadow", result);
//		if (Meth.doChance(0.5f))// could potentially have a great performance benifit without much difference
//			return false;
		DirectionalLight sun = SunKeeper.sun;
		// shadow mapping tutorial 1/2: 14:45 if this doesn't work!
		cm.setOrthoSymmetric(SHADOW_DISTANCE, SHADOW_DISTANCE * 2, 0.01f, SHADOW_DISTANCE * 2f);
		// center of light box. For now you're in the middle (== stupid)
		cm2.identity().lookAlong(sun.direction(), Vects.UP);
		// (c.ray()).mul(SHADOW_DISTANCE * 0.5f).add
		// don't really add 0, d*0.5f, 0 but instead c.ray() times sun.direction ?
		cm2.translate(Vects.calcVect().set(sun.direction()).mul(-0.1f * SHADOW_DISTANCE).add(c.position())
				.add(0, SHADOW_DISTANCE * 0.5f, 0)
				.add(c.ray().x * sun.direction().x, c.ray().y * sun.direction().y, c.ray().z * sun.direction().z)
				.negate());
		cm.mul(cm2);

		if (fi != null)
			fi.set(cm);

		result.toShadowSpace.identity().translate(0.5f, 0.5f, 0.5f).scale(0.5f);
		result.toShadowSpace.mul(cm);
		shadowMap.bind();
		shadowMap.clearDepthBuffer();
		GLUtils.enableFrontFaceCulling();
		return true;
	}

	@Override
	protected void stopRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		shadowMap.unbind();
		GLUtils.enableCulling();
	}

//	@Override
//	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
//			Map<String, PipelineResult> pipelineResults) {
//		pipelineResults.put("sunShadow", result);
////		if (Meth.doChance(0.5f))// could potentially have a great performance benifit without much difference
////			return false;
//		DirectionalLight sun = SunKeeper.sun;
//		// shadow mapping tutorial 1/2: 14:45 if this doesn't work!
//		cm.setOrthoSymmetric(SHADOW_DISTANCE, SHADOW_DISTANCE, 0.01f, SHADOW_DISTANCE);
//		// center of light box. For now you're in the middle (== stupid)
//		shader.calcMat.identity().lookAlong(sun.direction(), Vects.UP);
//		// (c.ray()).mul(SHADOW_DISTANCE * 0.5f).add
//		shader.calcMat.translate(Vects.calcVect().set(c.position()).add(0, SHADOW_DISTANCE * 0.5f, 0).negate());
//		cm.mul(shader.calcMat);
//		if (fi != null)
//			fi.set(cm);
//
//		result.toShadowSpace.identity().translate(0.5f, 0.5f, 0.5f).scale(0.5f).mul(cm);
//		shadowMap.bind();
//		shadowMap.clearDepthBuffer();
//		return true;
//	}
//
//	@Override
//	protected boolean prepareFor(Thing t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
//			Map<String, PipelineResult> pipelineResults) {
//		shader.projViewTransMat.set(cm.mul(t.createTransformationMatrix(shader.calcMat), shader.calcMat));
//		return false;
//	}
//
//	@Override
//	protected void stopRendering() {
//		shadowMap.unbind();
//	}

	@Override
	public int priority(Thing m) {
		return 1;
	}

	@Override
	public void cleanUp() {
		shadowMap.delete();
		super.cleanUp();
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
		// TODO make projectionmatrix fitting?
		this.width = width;
		this.height = height;
		this.aspect = height / (float) width;
	}

}
