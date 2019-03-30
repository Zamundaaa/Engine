package benchmark;

import java.util.Random;

import org.joml.Vector3f;

import generic.Thing;
import genericRendering.MasterRenderer;
import genericRendering.lights.Lamp;
import genericRendering.pipeline.postProcessing.PostProcessor;
import graphics2D.statistics.LineGraph;
import graphics3D.Camera;
import graphics3D.meshRenderer.MeshRenderer;
import graphics3D.particles.ParticleMaster;
import graphics3D.proceduralSkybox.SkyRenderer;
import graphics3D.terrain.*;
import graphics3D.terrain.Terrain.HeightGetter;
import graphics3D.text.TextRenderer;
import loaders.*;
import models.animated.AnimatedModelData;
import models.material.Material;
import models.text.Text2D;
import openGlResources.buffers.Fbo;
import openGlResources.buffers.VAO;
import openGlResources.shaders.ShaderProgram;
import tools.AppFolder;
import tools.Meth;
import tools.misc.interfaces.NotifierInterface;
import window.GLHandler;
import window.Window;

public abstract class Benchmark {

	private static Window w;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		AppFolder.init("benchmarker");
		GLHandler.init(false);
//		int width = 1280, height = 720;
		int width = 1920, height = 1080;
//		int width = 3840, height = 2160;
//		int width = 7680, height = 4320;
		w = GLHandler.createPlatformWindow("benchmark", width, height, 3, 3, "benchmarker", () -> {
			if (w.width() == 0 || w.height() == 0) {
				w.setSize(width, height);
			}
		});
		w.resizable(false);
		w.setSize(width, height);
		Camera c;
		MasterRenderer mr = null;
////		ShaderProgram.defaultDefs.put("bloom", "true");
////		defaultDefs.put("usepointlights", "true");
//		mr = new MasterRenderer(new PostProcessor(width, height),
//				Fbo.createMultiTargetsFbo(width, height, 2, false, true), new ParticleMaster(true),
//				new StaticRenderer(), new AnimatedRenderer(), new SkyRenderer(), new TextRenderer(),
//				new TerrainRenderer());
//		c = loadTestScene(mr);
//		BenchmarkResults ret = benchmarkScene(mr, c, w, 20, 0);

//		ShaderProgram.defaultDefs.put("bloom", "true");
//		if(mr != null)
//			mr.cleanUp();
//		mr = new MasterRenderer(new PostProcessor(width, height),
//				Fbo.createMultiTargetsFbo(width, height, 2, false, true), new ParticleMaster(true),
//				new StaticRenderer(), new AnimatedRenderer(), new SkyRenderer(), new TextRenderer(),
//				new TerrainRenderer());
//		c = loadTestScene(mr);
//		BenchmarkResults withbloom = benchmarkScene(mr, c, w, 20, 1);
//
//		ShaderProgram.defaultDefs.remove("bloom");
//		ShaderProgram.defaultDefs.put("usepointlights", "true");
//		if(mr != null)
//			mr.cleanUp();
//		mr = new MasterRenderer(new PostProcessor(width, height),
//				Fbo.createMultiTargetsFbo(width, height, 2, false, true), new ParticleMaster(true),
//				new StaticRenderer(), new AnimatedRenderer(), new SkyRenderer(), new TextRenderer(),
//				new TerrainRenderer());
//		c = loadTestScene(mr);
//		BenchmarkResults lights = benchmarkScene(mr, c, w, 20, 2);
//	
		boolean HDR = true;
		ShaderProgram.defaultDefs.put("bloom", "true");
		ShaderProgram.defaultDefs.put("usepointlights", "true");
		ShaderProgram.defaultDefs.put("HDR", "" + HDR);
//		ShaderProgram.defaultDefs.put("shownormals", "true");
		if (mr != null)
			mr.cleanUp();
		mr = new MasterRenderer(new PostProcessor(width, height),
				Fbo.createMultiTargetsFbo(width, height, 2, false, true, HDR), new ParticleMaster(true),
				new MeshRenderer(), new SkyRenderer(), new TextRenderer(), new TerrainRenderer());
		c = loadTestScene(mr);
		BenchmarkResults ret = benchmarkScene(mr, c, w, 30, 3);

		// TODO display up to 4 benchmark results with graphs next to each other (in
		// background stuff still running)
		// -> method "showbenchmarkresult(BenchmarkResults res, int corner) &
		// benchmarkScene added with (int corner) aswell

		System.out.println("avg: " + (1000f / ret.frameTimes().average()) + " max: "
				+ (1000f / ret.frameTimes().minValue()) + " min: " + (1000f / ret.frameTimes().maxValue()));

		Text2D t = new Text2D(
				" min: " + Meth.floatToString(1000f / ret.frameTimes().maxValue(), 1) + " avg: "
						+ Meth.floatToString(1000f / ret.frameTimes().average(), 1),
				0.25f, 0.475f, 0, Material.red.clone());

		LineGraph lg = new LineGraph(ret.frameTimes().size() + 1000, 1000f / ret.frameTimes().minValue(), -1, 0, 1,
				0.5f, Material.red.clone(), 0.0025f, new Material(0, 0, 0, 0.5f));

		for (int i = 0; i < ret.frameTimes().size(); i++) {
			lg.push(1000f / ret.frameTimes().get(i));
		}
		for (int i = 0; i < 1_000; i++)
			lg.push(0);

		t.scale(2);
		w.clear();
		mr.add(lg);
		mr.add(t);
		Thread worker = null;
		long time = System.currentTimeMillis();
		boolean sorted1 = false;
		float max = lg.valueList().maxValue();
		while (!w.isCloseRequested()) {
			c.rotation().rotateY(w.frameTimeSeconds() * 0.05f);
			mr.renderAll(c, w.projectionMatrix(), null).blitToScreen(0, w);
			w.update();
			lg.setLines();
			if (System.currentTimeMillis() - time > 3000 && worker == null) {
				worker = new Thread() {
					@Override
					public void run() {
						final Thread main = this;
						lg.valueList().quicksortUp(() -> {
							Meth.wartn(1);
						}, false, true);
						lg.setLines();
					}
				};
				worker.start();
			}
			if (worker != null && !worker.isAlive()) {
				time = System.currentTimeMillis();
				worker = new Thread() {
					@Override
					public void run() {
						final Thread t = this;
						NotifierInterface n = () -> {
							Meth.wartn(1);
						};
						for (int i = 0; i < lg.valueList().size();) {
							float v = Meth.randomFloat(0, max);
							for (int i2 = 0; i < lg.valueList().size() && i2 < 200; i2++, i++) {
								lg.valueList().set(i, v);
								n.somethinghappened();
							}
						}
						lg.valueList().quicksortUp(n, true, true);
						lg.setLines();
					}
				};
				worker.start();
			}
		}
		mr.cleanUp();
		w.close();
		GLHandler.cleanUp();
		System.exit(0);
	}

	public static BenchmarkResults benchmarkScene(MasterRenderer m, Camera c, Window w, int secondsToTest, int corner) {
//		mr.doWireframe = true;
		LineGraph lg = new LineGraph(500, 256f, -1, 0, 1, 0.5f, Material.red.clone(), 0.0025f,
				new Material(0, 0, 0, 0.5f));
		lg.automaticResize(true);
		m.add(lg);

		Text2D text = new Text2D("values", 0.25f, 0.475f, 0, Material.red.clone());
		text.scale(2);
		m.add(text);

		BenchmarkResults ret = new BenchmarkResults(w.width(), w.height());
		long t = System.currentTimeMillis(), t3 = 0;
		int index = 0;
		while (System.currentTimeMillis() - t < secondsToTest * 1000 && !w.isCloseRequested()) {
			long t2 = System.currentTimeMillis();
			SkyRenderer.addToTime(0.1f * w.frameTimeSeconds());
			c.rotation().rotateY(w.frameTimeSeconds() * 0.1f);
			c.position().y += w.frameTimeSeconds() * 0.05f;
			// "current: " + ((int) (1000f / w.frameTimeMillis())) +
			text.set("min: " + Meth.floatToString(1000f / ret.frameTimes().maxValue(), 1) + " avg: "
					+ Meth.floatToString(1000f / ret.frameTimes().average(), 1));
			m.renderAll(c, w.projectionMatrix(), null).blitToScreen(0, w);
			w.vSyncOffThisFrame();
			w.update();

			if (System.currentTimeMillis() - t > 1000) {
				ret.nextFrame();
				t3 += System.currentTimeMillis() - t2;
				while (t3 > 10 && ret.frameTimes().size() > index) {
					lg.push(1000f / ret.frameTimes().get(index++));
					t3 -= 10;
				}
			}
		}
		m.remove(text);
		m.remove(lg);
		return ret;
	}

	public static Camera loadTestScene(MasterRenderer m) {
		// TODO add many different models. High poly count models.
		// Different materials.
		// Some more different animated ones, too.
		// Some different animations perhaps. But don't do more animated instances

		SkyRenderer.setTime(15f);
		Camera c = new Camera(0, 1f, 0);
		c.rotation().rotateX(10 * Meth.angToRad);
		final NormalGenerator ng = new NormalGenerator();
		ng.seed = 5436;
		final float xztmul = 0.3f;
		HeightGetter hg = (x, z) -> {
			return ng.generateHeight(x * xztmul, z * xztmul);
		};
		c.position().y += hg.getHeight(0, 0);
		int count = 3;
		for (int x = -count; x <= count; x++) {
			for (int z = -count; z <= count; z++) {
				Terrain t = new Terrain(new Vector3f(x * 100, 0, z * 100), 100, 1f, hg, false, false);
				t.setMaterial(Material.white);
				t.addSecondaryMaterial(new Material(0.01f, 0.3f, 0.01f, 1));
				t.setMaterialGetter((X, Y, Z) -> {
					if (Y > 105)
						return 0;
					else
						return 1;
				});
				t.generateHeights();
				t.updateModelData();
				t.updateModel();
//				m.pm().addNewParticle(m.pm().cosmic, t.position().x, t.position().y, t.position().z, 0, 1, 0, 0, 30, 0,
//						10);
				m.add(t);
			}
		}
		Random r = new Random(ng.seed);
		// make tree positions predictable / always the same. Or it will differ quite a
		// bit in performance each time!
		VAO tree = ModelCache.get().getModel("res/models/obj/veryuglytree.obj");
		Material treemat = new Material(TextureCache.get().getTexture("res/textures/models/ugly tree base color.png"));
		// 15_000
		for (int i = 0; i < 5_000; i++) {
			final float s = 200;// 450;//
			Thing sm;
			m.add(sm = new Thing(tree, treemat, new Vector3f(0 + Meth.randomFloat(r, s), 0, 0 + Meth.randomFloat(r, s)),
					Meth.randomFloat(r, 0.1f, 1f)));
//			if (i < 10)
//				AppFolder.log.println("pos= " + Meth.coordinatesToString(sm.position()));
			sm.position().y = ng.generateHeight(sm.position().x * xztmul, sm.position().z * xztmul);
		}
		AnimatedModelData animdat = Loader.loadAnimatedModel("res/models/collada/Character Running.dae");
		for (int i = 0; i < 100; i++) {
			final float s = 40;
			Thing a;
//			new Material(
//					new Vector4f(Meth.randomFloat(0, 1), Meth.randomFloat(0, 1), Meth.randomFloat(0, 1), 1))
			Material mat = new Material(TextureCache.get().getTexture("res/textures/models/ice_sword_tex.png"));
			m.add(a = new Thing(animdat, Meth.doChance(0.1f) ? mat : treemat,
					new Vector3f(0 + Meth.randomFloat(r, s), 0, 0 + Meth.randomFloat(r, s)),
					Meth.randomFloat(r, 0.09f, 0.11f)));
			a.position().y = ng.generateHeight(a.position().x * xztmul, a.position().z * xztmul);
			a.rotation().rotateY(Meth.randomFloat(r, 0, 2 * Meth.PI));
		}
		float x = 3, z = 3;
//		m.addLight(new PointLight(c.position().x, c.position().y, c.position().z, 10));
		float red = 5, green = 0, blue = 0;
		float h = 1;
		VAO s = ModelCache.get().getModel("res/models/obj/sphere.obj");
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(-x, z) + h, -z, red, green, blue));

		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, -z) + h, -z, red, green, blue));

		x = z = 20;
		red = 0;
		blue = 20;
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(-x, z) + h, -z, red, green, blue));

		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, -z) + h, -z, red, green, blue));

		x = z = 10;
		red = 0;
		green = 10;
		blue = 5;
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, x, hg.getHeight(-x, z) + h, -z, red, green, blue));

		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, z) + h, z, red, green, blue));
		m.add(new Lamp(s, 0.1f, -x, hg.getHeight(x, -z) + h, -z, red, green, blue));

		return c;
	}

}
