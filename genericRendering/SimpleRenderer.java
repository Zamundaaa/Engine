package genericRendering;

import genericRendering.pipeline.postProcessing.PostProcessor;
import graphics3D.Camera;
import graphics3D.meshRenderer.MeshRenderer;
import graphics3D.particles.ParticleMaster;
import graphics3D.proceduralSkybox.SkyRenderer;
import graphics3D.terrain.TerrainRenderer;
import graphics3D.text.TextRenderer;
import models.Asset;
import openGlResources.buffers.Fbo;
import openGlResources.shaders.ShaderProgram;
import window.GLHandler;
import window.Window;

public class SimpleRenderer {

	protected boolean bloom, HDR;
	public Window w;
	public MasterRenderer mr;
	public Camera c;

	public SimpleRenderer(String windowTitle, String internalWindowName, boolean HDR, boolean bloom, boolean skybox) {
		this(3, 3, windowTitle, internalWindowName, HDR, bloom, skybox);
	}

	public SimpleRenderer(int openGL_major, int openGL_minor, String windowTitle, String internalWindowName,
			boolean HDR, boolean bloom, boolean skybox) {
		if (!GLHandler.initialized())
			GLHandler.init(false);
		// does NOT work without bloom ?!?
		this.bloom = bloom;
		ShaderProgram.defaultDefs.put("bloom", "" + bloom);
		this.HDR = HDR;
		w = GLHandler.createPlatformWindow(windowTitle, 500, 500, openGL_major, openGL_minor, internalWindowName,
				() -> {
					if (mr != null) {
						mr.renderTarget.delete();
						mr.setRenderTarget(createFbo());
						mr.post.delete();
						mr.post = new PostProcessor(w);
					}
				});
		c = w.camera();
		mr = new MasterRenderer(new PostProcessor(w), createFbo(), new ParticleMaster(true),
				skybox ? new SkyRenderer() : null, new TerrainRenderer(), new MeshRenderer(), new TextRenderer());
	}

	public void add(Asset a) {
		mr.add(a);
	}

	public void remove(Asset a) {
		mr.remove(a);
	}

	public void render() {
		Fbo ret = mr.renderAll(c, w.projectionMatrix(), null);
		ret.blitToScreen(0, w);
		w.update();
	}

	protected Fbo createFbo() {
		return Fbo.createMultiTargetsFbo(w.width(), w.height(), bloom ? 2 : 1, false, true, HDR);
	}

	public void cleanUp() {
		mr.cleanUp();
		w.close();
	}

}
