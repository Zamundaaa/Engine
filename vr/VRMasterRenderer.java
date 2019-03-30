package vr;

import org.joml.Matrix4f;

import genericRendering.EmptyRenderer;
import genericRendering.MasterRenderer;
import genericRendering.pipeline.postProcessing.PostProcessor;
import graphics3D.Camera;
import graphics3D.particles.ParticleMaster;
import openGlResources.GLUtils;
import openGlResources.buffers.Fbo;
import tools.misc.interfaces.NotifierInterface;

@SuppressWarnings("rawtypes")
public class VRMasterRenderer extends MasterRenderer {

	protected Fbo rightRenderTarget;
	protected PostProcessor rightPost;

	public VRMasterRenderer(PostProcessor leftPost, PostProcessor rightPost, Fbo leftRenderTarget,
			Fbo rightRenderTarget, EmptyRenderer... renderers) {
		for (int i = 0; i < renderers.length; i++)
			addRenderer(renderers[i]);
		this.post = leftPost;
		this.rightPost = rightPost;
		this.renderTarget = leftRenderTarget;
		this.rightRenderTarget = rightRenderTarget;
	}

	public VRMasterRenderer(PostProcessor leftPost, PostProcessor rightPost, Fbo leftRenderTarget,
			Fbo rightRenderTarget, ParticleMaster pm, EmptyRenderer... renderers) {
		this(leftPost, rightPost, leftRenderTarget, rightRenderTarget, renderers);
		this.pm = pm;
	}

	/**
	 * copies the references to all the renderers, the ParticleMaster and creates
	 * the new postprocessor for each eye. Also copies the map referencing model
	 * classes to Renderers, so this is more efficient than creating a new
	 * MasterRenderer with the same stuff as in m
	 */
	public VRMasterRenderer(MasterRenderer m, Fbo left, Fbo right) {
		this(m, left, right, new PostProcessor(VRHandler.renderWidth(), VRHandler.renderHeight()),
				new PostProcessor(VRHandler.renderWidth(), VRHandler.renderHeight()));
	}

	/**
	 * copies the references to all the renderers, the ParticleMaster. Also copies
	 * the map referencing model classes to Renderers, so this is more efficient
	 * than creating a new MasterRenderer with the same stuff as in m
	 */
	public VRMasterRenderer(MasterRenderer m, Fbo left, Fbo right, PostProcessor leftPost, PostProcessor rightPost) {
		super(m);
		this.renderTarget = left;
		this.rightRenderTarget = right;
		this.post = leftPost;
		this.rightPost = rightPost;
	}

	public Fbo rightRenderTarget() {
		return rightRenderTarget;
	}

	public void setRightRenderTarget(Fbo f) {
		this.rightRenderTarget = f;
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the render Target rendered to. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. May be null (then it's rendered to the screen directly)
	 */
	public Fbo renderAll(Camera c, Matrix4f projectionMatrix, Matrix4f inverseViewportScaleMat, boolean updatePM,
			Fbo renderTarget, boolean left) {
		float fts = (System.currentTimeMillis() - lastRender) * 0.001f;
		if (lastRender == 0)
			fts = 0.01f;
		lastRender = System.currentTimeMillis();
		int renderStage;
		for (renderStage = 0; renderStage < otherStages.size(); renderStage++) {
			RenderStage r = otherStages.get(renderStage);
			if (r.renderTiming > 0)
				break;
			r.renderAll(c, projectionMatrix, inverseViewportScaleMat, pipelineResults);
		}
		if (renderTarget != null && bindFrameBuffer) {
			renderTarget.bind();
			renderTarget.clearBuffers();
			if (inverseViewportScaleMat == null) {
				if (this.inverseViewportScaleMat == null)
					this.inverseViewportScaleMat = new Matrix4f();
				else
					this.inverseViewportScaleMat.identity();
				inverseViewportScaleMat = this.inverseViewportScaleMat;
				inverseViewportScaleMat.scale(1, renderTarget.width() / (float) renderTarget.height(), 1);
			}
		}
//		Meth.stopTime();
		VRHandler.renderHiddenMesh(left);
//		AppFolder.log.println("hidden mesh: " + Meth.stoppedTime());
		if (doWireframe)
			GLUtils.startWireFrame();
		for (int i = 0; i < renderers.size(); i++) {
			EmptyRenderer<?> r = renderers.get(i);
			if (doWireframe && r.noWireframe())
				GLUtils.stopWireFrame();
//			Meth.stopTime();
			r.renderAll(c, projectionMatrix, inverseViewportScaleMat, pipelineResults);
//			AppFolder.log.println(r + " took " + Meth.stoppedTime() + "ms");
			if (doWireframe && r.noWireframe())
				GLUtils.startWireFrame();
		}
		if (doWireframe)
			GLUtils.stopWireFrame();
		if (pm != null) {
//			Meth.stopTime();
			if (updatePM)
				pm.update(c, fts);
			pm.renderParticles(c.viewMat(), projectionMatrix);
//			AppFolder.log.println("pm " + Meth.stoppedTime());
		}
		if (left) {
			if (post != null && renderTarget != null) {
//				Meth.stopTime();
				Fbo ret = post.render(renderTarget);
//				AppFolder.log.println("left post: " + Meth.stoppedTime());
				return ret;
			}
		} else {
			if (rightPost != null && renderTarget != null) {
//				Meth.stopTime();
				Fbo ret = rightPost.render(renderTarget);
//				AppFolder.log.println("right post: " + Meth.stoppedTime());
				return ret;
			}
		}
		return renderTarget;
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. If the right render target of this VRMasterRenderer is
	 *         multisampled then the VRHandler's un-multisampled Fbo is returned.
	 *         Multisampling makes blitting to the default framebuffer impossible
	 *         for example
	 */
	public Fbo renderAll(Camera c) {
		return renderAll(c, true);
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. If the right render target of this VRMasterRenderer is
	 *         multisampled then the VRHandler's un-multisampled Fbo is returned.
	 *         Multisampling makes blitting to the default framebuffer impossible
	 *         for example
	 */
	public Fbo renderAll(Camera c, boolean updatePM) {
		return renderAll(c, renderTarget, rightRenderTarget, updatePM);
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. May be null (then it's rendered to the screen directly)
	 */
	public Fbo renderAll(Camera c, Fbo leftRenderTarget, Fbo rightRenderTarget) {
		return renderAll(c, leftRenderTarget, rightRenderTarget, true);
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. May be null (then it's rendered to the screen directly)
	 */
	public Fbo renderAll(Camera c, Fbo leftRenderTarget, Fbo rightRenderTarget, boolean updatePM) {
//		Meth.stopTime();
		VRHandler.startRendering();
		Matrix4f m = VRHandler.getLeftProjMat();
		if (VRHandler.player != null)
			VRHandler.player.set(c, 0, m);
		Fbo left = renderAll(c, m, null, updatePM, leftRenderTarget, true);
		m = VRHandler.getRightProjMat();
		if (VRHandler.player != null)
			VRHandler.player.set(c, 1, m);
		Fbo right = renderAll(c, m, null, false, rightRenderTarget, false);
//		AppFolder.log.println("rendering without submit: " + Meth.stoppedTime());
//		Meth.stopTime();
		VRHandler.submitBothEyes(left, right);
//		AppFolder.log.println("submitting: " + Meth.stoppedTime());
		return right.multisampled() ? VRHandler.rightFbo() : right;
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. If the right render target of this VRMasterRenderer is
	 *         multisampled then the VRHandler's un-multisampled Fbo is returned.
	 *         Multisampling makes blitting to the default framebuffer impossible
	 *         for example
	 */
	public Fbo renderAll(Camera c, NotifierInterface renderedLeft, NotifierInterface renderedRight) {
		return renderAll(c, true, renderedLeft, renderedRight);
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. If the right render target of this VRMasterRenderer is
	 *         multisampled then the VRHandler's un-multisampled Fbo is returned.
	 *         Multisampling makes blitting to the default framebuffer impossible
	 *         for example
	 */
	public Fbo renderAll(Camera c, boolean updatePM, NotifierInterface renderedLeft, NotifierInterface renderedRight) {
		return renderAll(c, renderTarget, rightRenderTarget, updatePM, renderedLeft, renderedRight);
	}

	/**
	 * @param c
	 * @param projectionMatrix
	 * @param inverseViewportScaleMat pass null to generate it accordingly to the
	 *                                rendertarget automagically
	 * @return the right render Target. You'll have to unbind the current
	 *         framebuffer (may be the returned, may be some of your postprocessor)
	 *         yourself before rendering to the screen or maybe blit it to the
	 *         screen. May be null (then it's rendered to the screen directly)
	 */
	public Fbo renderAll(Camera c, Fbo leftRenderTarget, Fbo rightRenderTarget, boolean updatePM,
			NotifierInterface renderedLeft, NotifierInterface renderedRight) {
		VRHandler.startRendering();
		Matrix4f m = VRHandler.getLeftProjMat();
		if (VRHandler.player != null)
			VRHandler.player.set(c, 0, m);
		renderAll(c, m, null, updatePM, leftRenderTarget, true);
		renderedLeft.somethinghappened();
		m = VRHandler.getRightProjMat();
		if (VRHandler.player != null)
			VRHandler.player.set(c, 1, m);
		renderAll(c, m, null, false, rightRenderTarget, false);
		renderedRight.somethinghappened();
		VRHandler.submitBothEyes(leftRenderTarget, rightRenderTarget);
		return rightRenderTarget.multisampled() ? VRHandler.rightFbo() : rightRenderTarget;
	}

}
