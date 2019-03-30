package genericRendering;

import java.util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import generic.Thing;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;
import openGlResources.GLUtils;
import openGlResources.buffers.VAO;
import openGlResources.shaders.ShaderProgram;

public abstract class Renderer<ToRender extends Thing, Shader extends ShaderProgram> extends EmptyRenderer<ToRender> {

	// TODO better sorting!

	protected List<ToRender> toRender = new ArrayList<>();
	protected Shader shader;
	protected boolean autoStartShader = true, autoStopShader = true, autoSortList = true, autoSetCulling = true;

	public Renderer(Shader shader) {
		this.shader = shader;
	}

	/**
	 * here you can set general shader variables or enable translucency or whatever
	 * you wanna do. Shader is automatically started before this (if autoStartShader
	 * is set to true. Default: true)
	 */
	protected abstract boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults);

	/**
	 * set just the shader uniforms concerning t. Others you set in
	 * {@link Renderer#startRendering()}. VAO is already bound. You could also do
	 * more than setting uniforms, but normally not needed
	 * 
	 * @return true if you want to discard (not render) this single ToRender
	 */
	protected abstract boolean prepareFor(ToRender t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults);

	/**
	 * for example: disable translucency again or something. No need to stop the
	 * shader manually, it's done automatically after this (if autoStopShader is set
	 * to true. Default: true)
	 */
	protected abstract void stopRendering();

	/**
	 * you'll only have to override if you don't use
	 * ({@link VAO#drawElements_Triangles} or {@link VAO#drawArrays_Triangles()}).
	 * If you override this method, don't forget to call
	 * {@link Renderer#prepareFor(Model)} in the new method and also optionally set
	 * OpenGL to cull faces or not. Shader variables concerning the "ToRender" are
	 * automatically set here via {@link Renderer#prepareFor(Model)}. VAO is already
	 * bound before this method is called. Has to be.
	 */
	protected void _render(ToRender t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		if (!prepareFor(t, c, projMat, inverseViewportScaleMat, pipelineResults)) {
			if (autoSetCulling) {
				if (t.cullFaces())
					GLUtils.enableCulling();
				else
					GLUtils.disableCulling();
			}
			if (t.vao().getIndicesArray() != null)
				t.vao().drawElements_Triangles();
			else
				t.vao().drawArrays_Triangles();
		}
	}

	@Override
	public void renderAll(List<ToRender> l, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		if (l.size() == 0)
			return;
		if (autoSortList)
			sortList(l, c);
		if (autoStartShader)
			shader.start();
//		long millis = System.currentTimeMillis();
//		int count = 0;
		startRendering(c, projMat, inverseViewportScaleMat, pipelineResults);
		try {
			VAO v = null;
			for (int i = 0; i < l.size(); i++) {
				ToRender t = l.get(i);
				if (t != null && t.vao() != null) {
					if (v == null || v.ID() != t.vao().ID()) {
						v = t.vao();
						v.bind();
					}
//					count++;
					_render(t, c, projMat, inverseViewportScaleMat, pipelineResults);
				}
			}
			if (v != null)
				v.unbind();
		} catch (IndexOutOfBoundsException e) {

		}
		stopRendering();
//		AppFolder.log
//				.println(this + " took " + (System.currentTimeMillis() - millis) + "ms to render " + count + " models");
		if (autoStopShader)
			shader.stop();
	}

	@Override
	public void renderAll(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		renderAll(toRender, c, projMat, inverseViewportScaleMat, pipelineResults);
	}

	@Override
	public void render(ToRender t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		if (t == null)
			return;
		if (autoStartShader)
			shader.start();
		startRendering(c, projMat, inverseViewportScaleMat, pipelineResults);
		t.vao().bind();
		_render(t, c, projMat, inverseViewportScaleMat, pipelineResults);
		t.vao().unbind();
		stopRendering();
		if (autoStopShader)
			shader.stop();
	}

	public Shader getShader() {
		return shader;
	}

	/**
	 * does NOT delete the old one
	 */
	public void setShader(Shader s) {
		this.shader = s;
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	@Override
	public void add(ToRender t) {
//		AppFolder.log.println("added " + t + " to " + this);
		synchronized (toRender) {
			toRender.add(t);
		}
	}

	@Override
	public boolean remove(ToRender t) {
		synchronized (toRender) {
			return toRender.remove(t);
		}
	}

	/**
	 * you should override this if you want to deploy a own sorting method or sort
	 * from front to back or something like that
	 */
	public void sortList(List<ToRender> toRender, Camera cam) {
		sortVAOs(toRender);
	}

	// FIXME compare bubblesort and quicksort performance here (should be
	// obvious...)

	/**
	 * uses -bubblesort- quicksort at the moment. Makes rendering with this class A
	 * LOT more efficient, as it only switches VAOs whilst renderings when they
	 * actually change. Also sorts by things like drawPerspective and fakeLight. Is
	 * automatically called if {@link Renderer#autoSortList} is true (enabled by
	 * default)
	 */
	public static <T extends Thing> void sortVAOs(List<T> toRender) {
		if (toRender.size() < 2)
			return;
		synchronized (toRender) {
//			T t;
//			T t1 = toRender.get(0);
//			for (int i = 1; i < toRender.size(); i++) {
//				t = toRender.get(i);
//				if (shouldSwap(t, t1)) {
//					toRender.set(i - 1, t);
//					toRender.set(i, t1);
//				} else {
//					t1 = t;
//				}
//			}
			quicksort(toRender, (t1, t2) -> {
				return shouldSwap(t1, t2);
			}, 0, toRender.size() - 1);
		}
	}

	protected static interface ShouldSwap<T> {
		public boolean should(T t, T t2);
	}

	protected static <T extends Thing> void quicksort(List<T> toRender, ShouldSwap<T> s, int low, int high) {
		if (low >= high)
			return;
		T pivotValue = toRender.get((low + high) / 2);
		int i = low, j = high;

		while (i <= j) {
			while (i < high && !s.should(toRender.get(i), pivotValue))// values[i] < pivotValue
				i++;
			while (j > low && !s.should(pivotValue, toRender.get(j)))// values[j] > pivotValue
				j--;
			if (i <= j) {
				if (i < j) {
					T t = toRender.get(j);
					toRender.set(j, toRender.get(i));
					toRender.set(i, t);
				}
				i++;
				j--;
			}
		}

		quicksort(toRender, s, low, j);
		quicksort(toRender, s, i, high);
	}

	/**
	 * if you want to use this method you should override {@link Renderer#sortList}
	 * and call it there
	 */
	public static <T extends Thing> void sortFrontToBackAndVAOs(List<T> toRender, Vector3f cameraPos) {
		sortZAndVAOs(toRender, cameraPos, true);
	}

	/**
	 * if you want to use this method you should override {@link Renderer#sortList}
	 * and call it there
	 */
	public static <T extends Thing> void sortBackToFrontAndVAOs(List<T> toRender, Vector3f cameraPos) {
		sortZAndVAOs(toRender, cameraPos, false);
	}

	/**
	 * if you want to use this method you should override {@link Renderer#sortList}
	 * and call it there
	 */
	public static <T extends Thing> void sortZAndVAOs(List<T> toRender, Vector3f cameraPos, final boolean frontToBack) {
		if (toRender.size() < 2)
			return;
		synchronized (toRender) {
			T t;
			T t1 = toRender.get(0);
			float d, d1 = cameraPos.distanceSquared(t1.position());
			for (int i = 1; i < toRender.size(); i++) {
				t = toRender.get(i);
				d = cameraPos.distanceSquared(t.position());
				if (frontToBack ? d1 < d : d1 > d) {
					toRender.set(i - 1, t);
					toRender.set(i, t1);
				} else if (d1 == d && shouldSwap(t, t1)) {
					toRender.set(i - 1, t);
					toRender.set(i, t1);
				} else {
					t1 = t;
				}

			}
		}
	}

	private static boolean shouldSwap(Thing t, Thing t1) {
//		if (t1 == null || t.vao() == null)
//			return false;
//		return t == null || t.vao() == null || t1.vao() == null || t.vao().ID() > t1.vao().ID()
//				|| (t.drawPerspective() && !t1.drawPerspective()) || (t.drawView() && !t1.drawView())
//				|| t.fakeLight() > t1.fakeLight()
//				|| (t.material() != null && t1.material() != null && t.material().diffuse().hasTexture()
//						&& (!t1.material().diffuse().hasTexture()
//								|| t.material().diffuse().tex().ID() < t1.material().diffuse().tex().ID()));
		if (t == null) {// sollte nicht möglich sein...
			return t1 != null;
		}
		if (t1 == null) // sollte nicht möglich sein...
			return false;
		if (t.vao() == null)
			return t1.vao() != null;
		if (t1.vao() == null)
			return false;
		if (t.material() == null)
			return t1.material() != null;
		if (t1.material() != null)
			return false;
//		if (t == null || t1 == null || t.vao() == null || t1.vao() == null || t.material() == null
//				|| t1.material() == null)
//			return false;
		if (t1.vao().ID() < t.vao().ID())
			return true;
		if (t.drawPerspective() && !t1.drawPerspective())
			return true;
		if (t.drawView() && !t1.drawView())
			return true;
		if (t.fakeLight() > t1.fakeLight())
			return true;
		if (t.material().compare(t1.material()) > 0)
			return true;
		return false;
	}

	public void autoStartShader(boolean b) {
		autoStartShader = b;
	}

	public void autoStopShader(boolean b) {
		autoStopShader = b;
	}

	public void autoSortList(boolean b) {
		autoSortList = b;
	}

	public void autoSetCulling(boolean b) {
		autoSetCulling = b;
	}

	@Override
	public int modelCount() {
		return toRender.size();
	}

	@Override
	public void clear() {
		toRender.clear();
	}

	@Override
	public void forAll(ModelProcessor mp) {
		for (int i = 0; i < toRender.size(); i++)
			mp.process(toRender.get(i));
	}

//	public void exportCode(StringBuilder b) {
//		for(int i = 0; i < toRender.size(); i++) {
//			toRender.get(i).exportCreationCode(b);
//		}
//	}

}
