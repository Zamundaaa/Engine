package graphics3D.particles;

import java.util.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import graphics3D.Camera;
import openGlResources.textures.Texture;
import tools.AppFolder;
import tools.Meth;

public class ParticleMaster {

	public short fire, cosmic, smoke, projectile, spark, fireworks;
	public float speedMul = 1;

	public static interface ParticleUpdater {
		public void updateParticle(Particle p);
	}

	private List<List<Particle>> particleList = new ArrayList<>();
	private ArrayDeque<Particle> deadParticles = new ArrayDeque<>();
	private ArrayDeque<Particle> queue = new ArrayDeque<>();
	private ParticleRenderer renderer;
	private ParticleUpdater extraUpdate = (p) -> {
	};

	public ParticleMaster() {
		this(true);
	}

	public ParticleMaster(boolean loadDefaultParticles) {
		this(loadDefaultParticles, 20_000);
	}

	public ParticleMaster(int max_particles) {
		this(true, max_particles);
	}

	public ParticleMaster(boolean loadDefaultParticles, int max_particles) {
		renderer = new ParticleRenderer(this, max_particles);
		if (loadDefaultParticles) {
			int i = 1;
			fire = loadParticleTexture("res/textures/particles/fire.png", 8, true, i++);
			if (fire > 0)
				get(fire).setBright(1);
			cosmic = loadParticleTexture("res/textures/particles/cosmic.png", 4, true, i++);
			if (cosmic > 0)
				get(cosmic).setBright(1);
			smoke = loadParticleTexture("res/textures/particles/smoke.png", 8, true, i++);
			projectile = loadParticleTexture("res/textures/particles/1stprojectile.png", 4, true, i++);
			if (projectile > 0)
				get(projectile).setBright(1);
			spark = loadParticleTexture("res/textures/particles/spark.png", 4, false, i++);
			if (spark > -1)
				get(spark).setBright(5);
			fireworks = loadParticleTexture("res/textures/particles/fireworks.png", 4, false, i++);
			if (fireworks > 0)
				get(fireworks).setBright(3);
		}
	}

	public void update(Camera c, float frameTimeSeconds) {
		frameTimeSeconds *= speedMul;
		try {
			synchronized (queue) {
				while (!queue.isEmpty()) {
					Particle p = queue.pop();
					List<Particle> ps = null;
					for (int i = 0; i < particleList.size(); i++) {
						List<Particle> pl = particleList.get(i);
						if (pl.size() > 0 && pl.get(0).tex == p.tex) {
							ps = pl;
							break;
						}
					}
					if (ps == null) {
						particleList.add(ps = new ArrayList<>());
					}
					ps.add(p);
				}
			}
			for (int i = 0; i < particleList.size(); i++) {
				List<Particle> list = particleList.get(i);
				short tex = -1;
				for (int i2 = 0; i2 < list.size(); i2++) {
					Particle p = list.get(i2);
					tex = p.tex;
					boolean stillAlive = p.update(c.position(), frameTimeSeconds);
					if (!stillAlive) {
						list.remove(i2--);
						synchronized (deadParticles) {
							deadParticles.add(p);
						}
						if (list.isEmpty()) {
//							synchronized (particles) {
							particleList.remove(i--);
//							}
							tex = -1;
							break;
						}
					} else {
						extraUpdate.updateParticle(p);
					}
				}
				// FIXME wtf this is so damn inefficient. BUBBLE SORT ?!? REALLY???
				if (tex > -1)
					if (!get(tex).isTransparent())
						sortHighToLow(list);
			}
		} catch (Exception e) {
			AppFolder.log.println("Particle updater failed again...");
			e.printStackTrace(AppFolder.log);
			System.exit(-1);
		}

	}

	public List<Particle> getParticleList(short tex) {
		List<Particle> ps = null;
		for (int i = 0; i < particleList.size(); i++) {
			List<Particle> pl = particleList.get(i);
			if (pl.size() > 0 && pl.get(0).tex == tex) {
				ps = pl;
				break;
			}
		}
		return ps;
	}

	private void sortHighToLow(List<Particle> list) {
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).distance() < list.get(i).distance()) {
				Particle p = list.get(i);
				list.set(i, list.get(i - 1));
				list.set(i - 1, p);
			}
		}
	}

	/**
	 * @return NumberOfParticles
	 */
	public int NOP(short tex) {
		List<Particle> ps = getParticleList(tex);
		if (ps != null) {
			return ps.size();
		} else {
			return 0;
		}
	}

	public Particle addNewParticle(short tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale) {
		Particle ret;
		if (NOP(tex) <= renderer.MAX_INSTANCES) {
			synchronized (deadParticles) {
				if (!deadParticles.isEmpty()) {
					ret = deadParticles.pop();
					ret.setActive(tex, position, velocity, gravityEffect, lifeLength, 0, rotation, scale);
					this.addParticle(ret);
				} else {
					ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, 0);
					this.addParticle(ret);
				}
			}
		} else {
			ret = null;
		}
		return ret;
	}

	public Particle addNewParticle(short tex, float x, float y, float z, float vx, float vy, float vz,
			float gravityEffect, float lifeLength, float rotation, float scale) {
		return addNewParticle(tex, x, y, z, vx, vy, vz, gravityEffect, lifeLength, rotation, scale, 0);
	}

	public Particle addStaticParticle(short tex, float x, float y, float z, float lifeLength, float scale) {
		return addNewParticle(tex, x, y, z, 0, 0, 0, 0, lifeLength, 0, scale, 0);
	}

	public Particle addNewParticle(short tex, float x, float y, float z, float vx, float vy, float vz,
			float gravityEffect, float lifeLength, float rotation, float scale, float elapsedTime) {
		Particle ret;
		if (NOP(tex) <= renderer.MAX_INSTANCES) {
			if (deadParticles.isEmpty()) {
				ret = new Particle(tex, new Vector3f(x, y, z), new Vector3f(vx, vy, vz), gravityEffect, lifeLength,
						rotation, scale, elapsedTime);
				this.addParticle(ret);
			} else {
				synchronized (deadParticles) {
					if (!deadParticles.isEmpty()) {
						ret = deadParticles.pop();
						ret.setActive(tex, x, y, z, vx, vy, vz, gravityEffect, lifeLength, elapsedTime, rotation,
								scale);
						this.addParticle(ret);
					} else {
						ret = new Particle(tex, new Vector3f(x, y, z), new Vector3f(vx, vy, vz), gravityEffect,
								lifeLength, rotation, scale, elapsedTime);
						this.addParticle(ret);
					}
				}
			}
		} else {
			ret = null;
		}
		return ret;
	}

	public Particle addNewParticle(short tex, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale, float elapsedTime) {
		Particle ret;
		if (NOP(tex) <= renderer.MAX_INSTANCES) {
			if (deadParticles.isEmpty()) {
				ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale, elapsedTime);
				this.addParticle(ret);
			} else {
				synchronized (deadParticles) {
					if (!deadParticles.isEmpty()) {
						ret = deadParticles.pop();
						ret.setActive(tex, position, velocity, gravityEffect, lifeLength, elapsedTime, rotation, scale);
						this.addParticle(ret);
					} else {
						ret = new Particle(tex, position, velocity, gravityEffect, lifeLength, rotation, scale,
								elapsedTime);
						this.addParticle(ret);
					}
				}
			}
		} else {
			ret = null;
		}
		return ret;
	}

	public void addSplashParticles(int count, short tex, float x, float y, float z, float dv, float gravity,
			float lifeLength, float min_scale, float max_scale) {
		boolean rscale = min_scale < max_scale;
		for (int i = 0; i < count; i++) {
			addNewParticle(tex, x, y, z, Meth.randomFloat(dv), Meth.randomFloat(dv), Meth.randomFloat(dv), gravity,
					lifeLength, 0, rscale ? Meth.randomFloat(min_scale, max_scale) : min_scale);
		}
	}

	public void renderParticles(Matrix4f viewMat, Matrix4f projMat, float planey, boolean upordownside) {
		renderer.render(viewMat, projMat, particleList, planey, upordownside);
	}

	public void renderParticles(Matrix4f viewMat, Matrix4f projMat) {
		renderer.render(particleList, viewMat, projMat);
	}

	public void cleanUp() {
		for (ParticleTexture t : texes.values()) {
			t.getTex().delete();
		}
		texes.clear();
		renderer.cleanUp();
	}

	public void addParticle(Particle p) {
		if (p != null) {
			synchronized (queue) {
				queue.add(p);
			}
		}
	}

	public float particleMul(short tex) {
		int a = NOP(tex);
		float ret = (renderer.MAX_INSTANCES - a) / (float) renderer.MAX_INSTANCES;
		return ret * ret * ret * ret;
	}

	public void setProjectionMatrix(Matrix4f mat) {
		ParticleRenderer.setProjectionMatrix(mat);
	}

	protected Map<Short, ParticleTexture> texes = new HashMap<>();
	protected Map<String, Short> pathMap = new HashMap<>();
	protected short max_ID = 0;
	protected int IDsUnderMaxFree = 0;

	/**
	 * @return the ID of the new particle texture. -1 for the eventuality that
	 *         there's Short.MAX_VALUE-1 particle textures already loaded or that
	 *         there is no texture at the specified path
	 */
	public short loadParticleTexture(String path, int numberOfRows, boolean translucent, int preferredID) {
		synchronized (texes) {
			if (preferredID > 0 && preferredID <= Short.MAX_VALUE && texes.get((short) preferredID) == null) {
				short pID = (short) preferredID;
				Texture tex = Texture.loadJarTexture(path);
				if (tex == null)
					return -1;
				ParticleTexture t = new ParticleTexture(tex, numberOfRows, translucent);
				texes.put(pID, t);
				pathMap.put(path, pID);
				if (pID > max_ID) {
					if (pID > max_ID + 1)
						IDsUnderMaxFree += pID - max_ID - 1;
					max_ID = pID;
				}
				return pID;
			}
			if (IDsUnderMaxFree > 0) {
				for (short s = 0; s < Short.MAX_VALUE; s++) {
					if (texes.get(s) == null) {
						IDsUnderMaxFree--;
						Texture tex = Texture.loadJarTexture(path);
						if (tex == null) {
							IDsUnderMaxFree++;
							return -1;
						}
						ParticleTexture t = new ParticleTexture(tex, numberOfRows, translucent);
						texes.put(s, t);
						pathMap.put(path, s);
						return s;
					}
				}
			} else {
				if (max_ID < Short.MAX_VALUE) {
					Texture tex = Texture.loadJarTexture(path);
					if (tex == null) {
						IDsUnderMaxFree++;
						return -1;
					}
					ParticleTexture t = new ParticleTexture(tex, numberOfRows, translucent);
					texes.put(max_ID += 1, t);
					pathMap.put(path, max_ID);
					return max_ID;
				}
			}
		}
		return -1;
	}

	public ParticleTexture get(short t) {
		return texes.get(t);
	}

	/**
	 * @param pu will be called after {@link Particle#update(Vector3f, float)} if
	 *           the particle hasn't died yet. Pass null to skip such an update
	 *           (empty lambda is inserted in that case). Be sure to not put
	 *           anything too performance
	 */
	public void setParticleUpdater(ParticleUpdater pu) {
		if (pu == null) {
			extraUpdate = (p) -> {
			};
		} else {
			extraUpdate = pu;
		}
	}

}
