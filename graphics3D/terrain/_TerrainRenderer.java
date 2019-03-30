package graphics3D.terrain;

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
import openGlResources.GLUtils;
import tools.misc.Vects;

class _TerrainRenderer extends Renderer<Terrain, TerrainShader> {

	private TerrainRenderer parent;

	public _TerrainRenderer(int MAX_LIGHTS, TerrainRenderer parent) {
		super(new TerrainShader(MAX_LIGHTS, true));// vary shadow map thing?
		this.maxpointlights = MAX_LIGHTS;
		this.parent = parent;
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f proj, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		PipelineResult r = pipelineResults.get("sunShadow");
		if (r != null) {
			if (r instanceof ShadowMap) {
				ShadowMap s = (ShadowMap) r;
				if (shader.shadowMap == null) {
					shader.cleanUp();
					shader = new TerrainShader(this.maxpointlights, true);
				}
				shader.shadowMap.set(r.fbo.getDepthTexture(), 5, s.toShadowSpace);
			}
		} else {
			if (shader.shadowMap != null) {
				shader.cleanUp();
				shader = new TerrainShader(this.maxpointlights, false);
			}
		}
		shader.projViewMat.set(shader.calcMat.set(proj).mul(c.viewMat()));
		shader.sunDir.set(SunKeeper.getSunDirection(Vects.calcVect, SkyRenderer.getTime()));
		shader.sunColor.set(SunKeeper.sun.color());
		shader.time.set(SkyRenderer.getTime());
		if (GLUtils.doWireframe)
			GLUtils.startWireFrame();
		return true;
	}

	@Override
	public boolean prepareFor(Terrain t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		t.createTransformationMatrix(shader.calcMat);
		if (!t.visible(c.fi()))
			return true;
		for (int i = 0; i < 5 && i <= t.secondaryMaterials.size(); i++)
			shader.diffuse.get(i).set(t.material(i).diffuse(), i);
		t.nearLights(PointLight.getMostRelevantPointLights(c, t, parent.lightList(), relevantLights,
				parent.maxpointlights()));
		if (this.maxpointlights > 0) {
//			AppFolder.log.println(t.nearLights() + " -> " + this.maxpointlights);
//			shader.numPointLights.set(relevantLights.size());
			shader.pointlights.set(relevantLights);
			shader.cameraPos.set(c.position());
		}
		return false;
	}

	@Override
	protected void stopRendering() {

	}

	@Override
	public int priority(Thing m) {
		if (TerrainRenderer.lightsteps > 0) {
			// potentially even create new renderers as necessary. In steps of course!
//			if (m.nearLights() > this.maxpointlights && this.maxpointlights < parent.maxpointlights())
//				return -100;
//			if (m.nearLights() < this.maxpointlights) {
//				int lowerstep = Math.max(0, this.maxpointlights - TerrainRenderer.lightsteps);
//				if (m.nearLights() <= lowerstep)
//					return -100;
//			}
			if (m.nearLights() > this.maxpointlights && this.maxpointlights < parent.maxpointlights())
				return -100;
			if (m.nearLights() < this.maxpointlights) {
				int lowerstep = Math.max(0, this.maxpointlights - TerrainRenderer.lightsteps);
				if (m.nearLights() <= lowerstep)
					return -100;
			}
		} else {
			if ((m.nearLights() > 0) != (this.maxpointlights > 0))
				return -100;
		}
		return 100;
	}

	@Override
	public String toString() {
		return "Terrainrenderer with " + this.maxpointlights + " lights";
	}

}
