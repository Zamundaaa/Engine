package newTest;

import static tools.Meth.PI;
import static tools.Meth.randomFloat;

import java.util.ArrayList;

import org.joml.Vector3f;

import generic.Thing;
import genericRendering.MasterRenderer;
import genericRendering.lights.Lamp;
import genericRendering.pipeline.postProcessing.PostProcessor;
import graphics3D.Camera;
import graphics3D.meshRenderer.MeshRenderer;
import graphics3D.particles.ParticleMaster;
import graphics3D.proceduralSkybox.SkyRenderer;
import graphics3D.shadows.SunShadowRenderer;
import graphics3D.terrain.*;
import graphics3D.text.TextRenderer;
import loaders.Loader;
import loaders.ModelCache;
import models.animated.AnimatedModelData;
import models.animated.Bone;
import models.material.Material;
import models.text.Text;
import openGlResources.buffers.Fbo;
import openGlResources.buffers.VAO;
import openGlResources.shaders.ShaderProgram;
import tools.AppFolder;
import tools.Meth;
import tools.misc.Vects;
import tools.raytracing.GenericRaytracer;
import window.GLHandler;
import window.Window;
import window.input.Mouse;

public class SimplePhysics {

	private static MasterRenderer mr;
	private static Window w;
	private static Camera c;
	private static ProceduralTerrainGenerator terrain;
	private static Text fps;

	public static void main(String[] args) {
		AppFolder.init("thetestinggame/physicstest");
		GLHandler.init(false);
		w = GLHandler.createPlatformWindow("idk", 500, 500, 3, 3, "idk", () -> {
			mr.getPostProcessor().delete();
			mr.setPostProcessor(new PostProcessor(w));
			mr.getRenderTarget().delete();
			mr.setRenderTarget(Fbo.createMultiTargetsFbo(w.width(), w.height(), 2, false, true, true));
		});
		c = w.camera();
		ShaderProgram.defaultDefs.put("bloom", "true");
		ShaderProgram.defaultDefs.put("HDR", "true");
		TerrainRenderer tr = new TerrainRenderer();
		final NormalGenerator ng = new NormalGenerator();
		terrain = new ProceduralTerrainGenerator(c, tr, (x, z) -> {
			synchronized (ng) {
				return 0.3f * ng.generateHeight(x * 0.4f, z * 0.4f);
			}
		}, true, () -> {
			return w.isCloseRequested();
		});
		c.position().set(0, terrain.heightAt(0, 0) + 2, 0);
		mr = new MasterRenderer(new PostProcessor(w),
				Fbo.createMultiTargetsFbo(w.width(), w.height(), 2, false, true, true), new ParticleMaster(true),
				new SkyRenderer(), new MeshRenderer(), new TextRenderer(), tr);
		mr.addRenderStage(-1, new SunShadowRenderer(w));
		init();
		while (!w.isCloseRequested()) {
			update();
			Fbo o = mr.renderAll(c, w.projectionMatrix(), null);
			o.blitToScreen(0, w);
			w.update();
		}
		mr.cleanUp();
		w.close();
		GLHandler.cleanUp();
		AppFolder.savePreferences();
	}

	private static ArrayList<Thing> movs = new ArrayList<>();

	private static void init() {
		createMovs();
		VAO tree = ModelCache.get().getModel("veryuglytree.obj");
		for (int i = 0; i < 100; i++) {
			Thing t = new Thing(tree, Material.blue, new Vector3f(Meth.randomFloat(50), 0, Meth.randomFloat(50)), 1);
			mr.add(t);
			t.position().y = terrain.heightAt(t.position().x, t.position().z);
		}
		AnimatedModelData anim = Loader.loadAnimatedModel("res/models/collada/Character Running.dae");
		Thing a = new Thing(anim, Material.white, new Vector3f(5, 0, 0), 0.3f);
		a.position().set(5, terrain.heightAt(5, 0), 0);
		a.rotation().rotateY(Meth.randomFloat(0, 2 * PI));
		Bone hand = a.animation().getBone("Armature_Hand_R");
		if (hand != null) {
			Lamp l = new Lamp(ModelCache.get().getModel("sphere.obj"), 0.2f, 0.2f, 0, 0);
			l.position().set(0, 0.15f, 0.022f);
			l.setParent(hand);
			a.add(l);
		} else {
			AppFolder.log.println("hand is null...");
		}
		mr.add(a);
		for (int i = 0; i < 5; i++) {
			Lamp l = new Lamp(ModelCache.get().getModel("sphere.obj"), 0.1f, randomFloat(0, 3), randomFloat(0, 3),
					randomFloat(0, 3));
			l.position().set(randomFloat(4), randomFloat(0, 2), randomFloat(4));
			l.doPhysics = true;
			mr.add(l);
			movs.add(l);
		}
//		fps = new Text("fps", Material.black, true);
//		fps.position().set(-0.85f, 0.4f, -0.5f);
//		fps.scale(2);
////		fps.drawModeScaled();
//		fps.setParent(c);
//		mr.add(fps);
	}

	private static void createMovs() {
		for (int i = 0; i < 500; i++) {
			Thing m = new Thing(ModelCache.get().getModel("res/models/obj/cube.obj"), Material.green,
					Vects.randomVector3f(20f, new Vector3f()), Meth.randomFloat(0.075f * 0.25f, 0.1f * 0.25f));
//			m.scale().y *= 2;
			m.position().y += 20;
			m.rotation().rotateTo(Vects.DOWN, Vects.randomVector3f(1, Vects.calcVect()).normalize());
//			m.createRigidBody(cw);
			movs.add(m);
			mr.add(m);
		}

//		cb = new CollisionBody(
//				new net.smert.jreactphysics3d.mathematics.Transform(new Vector3(position.x, position.y, position.z),
//						new Quaternion(rotation.x, rotation.y, rotation.z, rotation.z)),
//				new BoxShape(
//						new Vector3(scale.x * intrinsicSize.x, scale.y * intrinsicSize.y, scale.z * intrinsicSize.z),
//						0.01f),
//				1);

	}

	private static void update() {
		if (fps != null)
			fps.set("" + (int) w.avgFps());
		float f = w.frameTimeSeconds();
		c.doEasyControls(w);
		terrain.update(f);
		if (w.mouse().buttonClickedThisFrame(Mouse.RIGHT)) {
			while (movs.size() > 0) {
				Thing m = movs.remove(movs.size() - 1);
				mr.remove(m);
			}
			createMovs();
		}
		for (int i = 0; i < movs.size(); i++) {
			Thing m = movs.get(i);
			m.setGroundHeight(terrain.heightAt(m.position().x, m.position().z));
			if (m.sitsOnGround() && Meth.doChance(0.1f * w.frameTimeSeconds())) {
				m.velocity().add(Meth.randomFloat(2), Meth.randomFloat(2, 5), Meth.randomFloat(1));
				m.angularVelocity().rotateAxis(Meth.randomFloat(5 * PI), Meth.randomFloat(1), Meth.randomFloat(1),
						Meth.randomFloat(1));
			}
			if (m.updateServer(f))
				movs.remove(m);
			else if (m.updateClient(f, null))
				movs.remove(m);
		}
		if (w.mouse().buttonClickedThisFrame(Mouse.RIGHT))
			GenericRaytracer.find(c.position(), w.mouseRay(), 0.5f, 50, (x, y, z) -> {
				mr.pm().addNewParticle(mr.pm().cosmic, x, y, z, 0, 0, 0, 0, 5, 0, 0.2f);
				return false;
			}, new Vector3f());
		SkyRenderer.addToTime(w.mouse().getDWheelFrame() * 0.3f);
	}

}
