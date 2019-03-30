package graphics3D.meshRenderer;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.Renderer;
import genericRendering.lights.PointLight;
import genericRendering.pipeline.PipelineResult;
import genericRendering.pipeline.ShadowMap;
import graphics3D.Camera;
import graphics3D.proceduralSkybox.SkyRenderer;
import graphics3D.proceduralSkybox.SunKeeper;
import models.components.AnimationComponent;
import openGlResources.GLUtils;
import tools.misc.Vects;

public class _MeshRenderer extends Renderer<Thing, MeshShader> {

	// TODO perhaps shadows as an argument / renderer type: those that use shadows
	// and those that don't or something. Also test the performance hit (using the
	// benchmarker). Then: ShadowBox rendering (pointlight shadows)

	private boolean translucents;
	private Matrix4f projView = new Matrix4f();
	private MeshRenderer parent;
	private boolean shadows = false;

//	public _MeshRenderer(boolean translucents, int lights, boolean animations) {
//		this(translucents, lights, animations, null);
//	}

	public _MeshRenderer(boolean translucents, int lights, boolean animations, MeshRenderer parent) {
		super(translucents ? new MeshShader(lights, animations, "showNormals false")
				: new MeshShader(lights, animations));
		this.maxpointlights = lights;
		this.translucents = translucents;
//		autoStartShader = translucents;
		autoStopShader = false;
		this.parent = parent;
	}

	@Override
	public void sortList(List<Thing> toRender, Camera c) {
		if (translucents)
			sortBackToFrontAndVAOs(toRender, c.position());
		else
			sortVAOs(toRender);
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScalingMat,
			Map<String, PipelineResult> pipelineResults) {
		if (projMat != null && c != null)
			shader.projViewMat().set(projView.set(projMat).mul(c.viewMat()));
		shadows = false;
		if (pipelineResults != null) {
			PipelineResult shadowMap = pipelineResults.get("sunShadow");
			if (shadowMap != null && shadowMap instanceof ShadowMap) {
				ShadowMap s = (ShadowMap) shadowMap;
				shader.shadowmap.set(s, 2);
				shadows = true;
			}
		}
		shader.sun().set(SunKeeper.getSunDirection(Vects.calcVect, SkyRenderer.getTime()), SunKeeper.sun.color());
		if (shader.cameraPos != null && c != null)
			shader.cameraPos.set(c.position());
		if (translucents) {
			GLUtils.enableTranslucency();
			GLUtils.disableDepthWrite();
		}
		return true;
	}

	@Override
	protected boolean prepareFor(Thing t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScalingMat,
			Map<String, PipelineResult> pipelineResults) {
		// before model culling, so it can be detected again (absolute position etc...)
		t.createTransformationMatrix(shader.calcMat);
		if (t.material() == null)
			return true;
		if (parent != null && parent.cullModels() && t.cullModel() && t.drawPerspective()) {
			if (!t.visible(c.fi())) {
				return true;
			}
		}
		shader.transMat().set(shader.calcMat);
		shader.diffuse().set(t.material().diffuse(), 0);
		shader.bloomMat().set(t.material().bloomMat(), 1);
		shader.fakeLight().set(t.fakeLight());
		shader.reflectivity.set(t.material().reflectivity());
//		shader.reflectDampening.set(t.material().reflectDampening());
		if (t.drawPerspective()) {
			shader.projViewMat().set(projView);
		} else if (t.drawView()) {
			shader.projViewMat().set(c.viewMat());
		} else if (t.drawScaled()) {
			shader.projViewMat().set(inverseViewportScalingMat);
		} else {
			shader.projViewMat().identity();
		}
		if (t.drawPerspective()) {
			t.nearLights(PointLight.getMostRelevantPointLights(c, t, parent.lightList(), relevantLights,
					parent.maxpointlights() + (parent.autoIncreaseLights ? MeshRenderer.lightsteps : 0)));
			if (shader.pointlights != null) {
				shader.pointlights.set(relevantLights);
			}
		} else {
			t.nearLights(0);
		}
		if (shader.jointTransforms != null) {
			AnimationComponent ac = t.animation();
			if (ac.updateAutomatically() && !shadows) {
				ac.updateAnimation();
			}
			shader.jointTransforms.set(ac.getPose());
		}
//		if (t.vao().getIndicesArray() != null)
//			parent.indicesRendered += t.vao().getIndicesArray().count();
		return false;
	}

	@Override
	protected void stopRendering() {
		if (translucents) {
			GLUtils.disableTranslucency();
			GLUtils.enableDepthWrite();
		}
	}

	@Override
	public int priority(Thing m) {
		boolean tfits = (m.material() != null && m.material().translucent()) == translucents;
		if (!tfits)
			return -100;
		if (MeshRenderer.lightsteps > 0) {
			if (m.nearLights() > this.maxpointlights && this.maxpointlights < parent.maxpointlights())
				return -100;
			if (m.nearLights() < this.maxpointlights) {
				int lowerstep = Math.max(0, this.maxpointlights - MeshRenderer.lightsteps);
				if (m.nearLights() <= lowerstep)
					return -100;
			}
		} else {
			if ((m.nearLights() > 0) != (this.maxpointlights > 0))
				return -100;
		}
		// here we can safely assume that no non-Mesh will ever come in here!
		return ((m.animation() != null) == (shader.jointTransforms != null)) ? 100 : -50;
	}

	@Override
	public String toString() {
		return (shader.jointTransforms != null ? "animated " : "") + (translucents ? "translucent " : "")
				+ "MeshRenderer with " + maxpointlights + " lights";
	}

}