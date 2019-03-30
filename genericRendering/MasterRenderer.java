package genericRendering;

import java.util.*;

import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.EmptyRenderer.ModelProcessor;
import genericRendering.lights.Light;
import genericRendering.pipeline.PipelineResult;
import genericRendering.pipeline.postProcessing.PostProcessor;
import graphics3D.Camera;
import graphics3D.particles.ParticleMaster;
import models.Asset;
import openGlResources.GLUtils;
import openGlResources.buffers.Fbo;

@SuppressWarnings("rawtypes")
public class MasterRenderer {

	protected class RenderStage {

		public int renderTiming = -1;
		protected ArrayList<EmptyRenderer> renderers = new ArrayList<>();
		protected Map<Class, EmptyRenderer> modelMap = new HashMap<>();
//		protected long lastRender;
		protected List<Light> lights;

		private RenderStage(List<Light> lights, int renderTiming, EmptyRenderer<?>... renderers) {
			this.lights = lights;
			for (int i = 0; i < renderers.length; i++) {
				EmptyRenderer<?> r = renderers[i];
				this.renderers.add(r);
				if (!r.lights.isEmpty())
					lights.addAll(r.lights);
				r.lights = this.lights;
			}
			this.renderTiming = renderTiming;
		}

		public void renderAll(Camera c, Matrix4f projectionMatrix, Matrix4f inverseViewportScaleMat,
				Map<String, PipelineResult> pipelineResults) {
			for (int i = 0; i < renderers.size(); i++) {
				EmptyRenderer<?> r = renderers.get(i);
				r.renderAll(c, projectionMatrix, inverseViewportScaleMat, pipelineResults);
			}
		}

		@SuppressWarnings("unchecked")
		public boolean add(Asset a) {
			if (a == null)
				return false;
			if (a.assets() != null) {
				for (int i = 0; i < a.assets().size(); i++)
					add(a.assets().get(i));
				a.shown(true);
			}
			if (a instanceof Light) {
				addLight((Light) a);
				a.shown(true);
			} else if (a instanceof Thing) {
				Thing m = (Thing) a;
				EmptyRenderer<Thing> r = modelMap.get(m.getClass());
				if (r != null) {
					r.add(m);
					m.shown(true);
					return true;
				}
				int maxPrioRenderer = -1;
				int maxPrio = 0, prio;
				for (int i = 0; i < renderers.size(); i++) {
					if ((prio = (r = renderers.get(i)).priority(m)) > maxPrio) {
						maxPrio = prio;
						maxPrioRenderer = i;
					}
				}
				if (maxPrio > 0) {
					modelMap.put(m.getClass(), renderers.get(maxPrioRenderer));
					r = renderers.get(maxPrioRenderer);
					r.add(m);
					m.shown(true);
					return true;
				}
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public void remove(Asset a) {
			if (a == null)
				return;
			if (a instanceof Light) {
				lights.remove(a);
				a.shown(false);
			} else if (a instanceof Thing) {
				Thing m = (Thing) a;
				EmptyRenderer<Thing> r = modelMap.get(m.getClass());
				if (r != null) {
					r.remove(m);
				} // if the model has been added there's a map entry for it!+
			}
			if (a.assets() != null)
				for (int i = 0; i < a.assets().size(); i++)
					remove(a.assets().get(i));
			a.shown(false);
		}

	}

	protected ArrayList<RenderStage> otherStages = new ArrayList<>();

	public boolean doWireframe = false;

	protected ArrayList<EmptyRenderer> renderers = new ArrayList<>();
	protected Map<Class, EmptyRenderer> modelMap = new HashMap<>();
	protected PostProcessor post;
	protected ParticleMaster pm;
	protected Fbo renderTarget;
	protected Matrix4f inverseViewportScaleMat;
	protected boolean bindFrameBuffer = true;
	protected long lastRender;
	protected boolean seperateLights = false;
	protected List<Light> lights;
	protected Map<String, PipelineResult> pipelineResults = new HashMap<>();

	public MasterRenderer(EmptyRenderer... renderers) {
		for (int i = 0; i < renderers.length; i++) {
			addRenderer(renderers[i], false);
		}
		seperateLights = !seperateLights;
		setSeperateLights(!seperateLights);
	}

	public MasterRenderer(PostProcessor post, Fbo renderTarget, EmptyRenderer<?>... renderers) {
		for (int i = 0; i < renderers.length; i++) {
			addRenderer(renderers[i], false);
		}
		seperateLights = !seperateLights;
		setSeperateLights(!seperateLights);
		this.post = post;
		this.renderTarget = renderTarget;
	}

	public MasterRenderer(PostProcessor post, Fbo renderTarget, ParticleMaster pm, EmptyRenderer... renderers) {
		this(post, renderTarget, renderers);
		this.pm = pm;
	}

	/**
	 * copies all the references to the renderers, the ParticleMaster and the
	 * postprocessor and the fbo. Also copies the map referencing model classes to
	 * Renderers, so this is more efficient than creating a new MasterRenderer with
	 * the same stuff as in m
	 */
	public MasterRenderer(MasterRenderer m) {
		this.renderers.addAll(m.renderers);
		this.modelMap.putAll(modelMap);
		this.post = m.post;
		this.renderTarget = m.renderTarget;
		this.pm = m.pm;
		this.lights = m.lights;
	}

	public void addRenderStage(int renderTiming, EmptyRenderer... renderers) {
		RenderStage r = new RenderStage(lights, renderTiming, renderers);
		otherStages.add(r);
		otherStages.sort(new Comparator<RenderStage>() {
			@Override
			public int compare(RenderStage o1, RenderStage o2) {
				return o2.renderTiming - o1.renderTiming;
			}
		});
		forAll((a) -> {
			r.add(a);
		});

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
	public Fbo renderAll(Camera c, Matrix4f projectionMatrix, Matrix4f inverseViewportScaleMat) {
		return renderAll(c, projectionMatrix, inverseViewportScaleMat, this.renderTarget);
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
	public Fbo renderAll(Camera c, Matrix4f projectionMatrix, Matrix4f inverseViewportScaleMat, boolean updatePM) {
		return renderAll(c, projectionMatrix, inverseViewportScaleMat, updatePM, this.renderTarget);
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
	public Fbo renderAll(Camera c, Matrix4f projectionMatrix, Matrix4f inverseViewportScaleMat, Fbo renderTarget) {
		return renderAll(c, projectionMatrix, inverseViewportScaleMat, true, renderTarget);
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
			Fbo renderTarget) {
		c.update(projectionMatrix);
		float fts = (System.currentTimeMillis() - lastRender) * 0.001f;
		if (lastRender == 0)
			fts = 0.01f;
		lastRender = System.currentTimeMillis();
		int renderStage;
		pipelineResults.clear();
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
		GLUtils.disableCulling();
		if (doWireframe)
			GLUtils.startWireFrame();
		for (int i = 0; i < renderers.size(); i++) {
			EmptyRenderer<?> r = renderers.get(i);
			if (doWireframe && r.noWireframe())
				GLUtils.stopWireFrame();
			r.renderAll(c, projectionMatrix, inverseViewportScaleMat, pipelineResults);
			if (doWireframe && r.noWireframe())
				GLUtils.startWireFrame();
		}
		if (doWireframe)
			GLUtils.stopWireFrame();
		if (pm != null) {
			if (updatePM)
				pm.update(c, fts);
			pm.renderParticles(c.viewMat(), projectionMatrix);
		}
		if (post != null && renderTarget != null) {
			return post.render(renderTarget);
		}
		return renderTarget;
	}

	public boolean isBindFrameBuffer() {
		return bindFrameBuffer;
	}

	public void setBindFrameBuffer(boolean bindFrameBuffer) {
		this.bindFrameBuffer = bindFrameBuffer;
	}

	@SuppressWarnings("unchecked")
	public void setSeperateLights(boolean b) {
		if (seperateLights && !b) {
			if (renderers.size() > 0)
				lights = renderers.get(0).lights;
			else
				lights = new ArrayList<>();
			for (int i = 0; i < renderers.size(); i++) {
				List<Light> ls = renderers.get(i).lights;
				for (int i2 = 0; i2 < ls.size(); i2++) {
					if (!lights.contains(ls.get(i2))) {
						lights.add(ls.get(i2));
					}
				}
				renderers.get(i).lights = lights;
			}
		} else if (!seperateLights && b) {
			if (renderers.size() > 0) {
				List<Light> l = renderers.get(0).lights;
				for (int i = 1; i < renderers.size(); i++) {
					renderers.get(i).lights = l;
				}
			}
		}
		seperateLights = b;
	}

	public ArrayList<EmptyRenderer> renderers() {
		return renderers;
	}

	public void addRenderer(EmptyRenderer<?> r) {
		addRenderer(r, true);
	}

	protected void addRenderer(EmptyRenderer<?> r, boolean setLightList) {
		if(r == null)
			return;
		boolean done = false;
		for (int i = 0; i < renderers.size(); i++) {
			int prio = renderers.get(i).renderTiming();
			if (r.renderTiming() < prio) {
				renderers.add(i, r);
				done = true;
				break;
			}
		}
		if (!done)
			renderers.add(r);
		if (!seperateLights && setLightList) {
			seperateLights = !seperateLights;
			setSeperateLights(!seperateLights);
		}
	}

	public void removeRenderer(EmptyRenderer r) {
		renderers.remove(r);
	}

	public void setPostProcessor(PostProcessor post) {
		this.post = post;
	}

	public PostProcessor getPostProcessor() {
		return post;
	}

	public void setRenderTarget(Fbo f) {
		this.renderTarget = f;
	}

	public Fbo getRenderTarget() {
		return renderTarget;
	}

	public void cleanUp() {
		renderTarget.delete();
		post.delete();
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).cleanUp();
	}

	@SuppressWarnings("unchecked")
	public boolean add(Asset a) {
		if (a == null)
			return false;
		for (int i = 0; i < otherStages.size(); i++) {
			otherStages.get(i).add(a);
		}
		if (a.assets() != null) {
			for (int i = 0; i < a.assets().size(); i++)
				add(a.assets().get(i));
			a.shown(true);
		}
		if (a instanceof Light) {
			addLight((Light) a);
			a.shown(true);
//			AppFolder.log.println("added a light!");
		} else if (a instanceof Thing) {
			Thing m = (Thing) a;
			EmptyRenderer<Thing> r = modelMap.get(m.getClass());
			if (r != null) {
				r.add(m);
				m.shown(true);
				return true;
			}
			int maxPrioRenderer = -1;
			int maxPrio = 0, prio;
			for (int i = 0; i < renderers.size(); i++) {
				if ((prio = (r = renderers.get(i)).priority(m)) > maxPrio) {
					maxPrio = prio;
					maxPrioRenderer = i;
				}
			}
			if (maxPrio > 0) {
				modelMap.put(m.getClass(), renderers.get(maxPrioRenderer));
				r = renderers.get(maxPrioRenderer);
				r.add(m);
//				AppFolder.log.println("added " + m.getClass() + " to " + r);
				m.shown(true);
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void remove(Asset a) {
		if (a == null)
			return;
		for (int i = 0; i < otherStages.size(); i++) {
			otherStages.get(i).remove(a);
		}
		if (a instanceof Light) {
			lights.remove(a);
			a.shown(false);
		} else if (a instanceof Thing) {
			Thing m = (Thing) a;
			EmptyRenderer<Thing> r = modelMap.get(m.getClass());
			if (r != null) {
				r.remove(m);
			} // if the model has been added there's a map entry for it!+
		}
		if (a.assets() != null)
			for (int i = 0; i < a.assets().size(); i++)
				remove(a.assets().get(i));
		a.shown(false);
	}

	public void setParticleMaster(ParticleMaster m) {
		this.pm = m;
	}

	public ParticleMaster pm() {
		return pm;
	}

	/**
	 * will only work if seperateLights is false (it is by default)
	 */
	public void addLight(Light l) {
		lights.add(l);
	}

	public void removeLight(Light l) {
		lights.remove(l);
	}

	public void clearAll() {
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).clear();
	}

	public void forAll(ModelProcessor mp) {
		for (int i = 0; i < renderers.size(); i++)
			renderers.get(i).forAll(mp);
	}

	public Map<String, PipelineResult> pipelineResults() {
		return pipelineResults;
	}

//	public String exportCode() {
//		StringBuilder b = new StringBuilder();
//		for(int i = 0; i < renderers.size(); i++)
//			renderers.get(i).exportCode(b);
//		return b.toString();
//	}

}
