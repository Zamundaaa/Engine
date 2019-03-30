package graphics3D.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

import tools.misc.Vects;

public class Particle {

	protected Vector3f position;
	protected Vector3f velocity;
	protected float gravityEffect;
	protected float lifeLength;
	protected float rotation;
	protected float scale;

	protected short tex;

	protected float blend;

	protected float elapsedTime = 0;
	protected float distance;
	
	protected static Vector3f changeVectReusable = new Vector3f();

	protected boolean alive = true;

	public Particle(short tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength,
			float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
	}

	public Particle(short tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength,
			float rotation, float scale, float elapsedTime) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		this.elapsedTime = elapsedTime;
	}

	public void updateElapsedTime(float frameTimeSeconds) {
		elapsedTime -= frameTimeSeconds;
	}

	public float getBlend() {
		return blend;
	}

	public short getTex() {
		return tex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		if (!enlargeEffect) {
			return scale;
		} else {
			if (elapsedTime < lifeLength * 0.5f) {
				return scale * (0.5f + (elapsedTime / lifeLength));
			} else {
				return scale * (0.5f - (elapsedTime / lifeLength));
			}
		}
	}

	public float getDistance() {
		return distance;
	}

	public boolean isAlive() {
		return alive;
	}

	protected boolean update(Vector3f camPos, float frameTimeSeconds) {
		if (velocity != Vects.NULL) {
			velocity.y += gravityEffect * frameTimeSeconds;
			// if(windfactor != 0){
			// velocity.x = WeatherController.getWindX(position.x, position.z);
			// velocity.z = WeatherController.getWindZ(position.x, position.z);
			// }
			changeVectReusable.set(velocity);
			// changeVectReusable.scale(Loop.getFrameTimeSeconds());
			// changeVectReusable.normalize();
			changeVectReusable.mul(frameTimeSeconds);
			position.add(changeVectReusable);
		}
		// distance = Vector3f.sub(cam.getPosition(), position,
		// null).lengthSquared();
		distance = camPos.distanceSquared(position);
		elapsedTime += frameTimeSeconds;
		if (alive)
			alive = elapsedTime < lifeLength;

		return alive;
	}

	/**
	 * add the particle to your ParticleMaster afterwards. Or just let the Master do
	 * this!
	 */
	public void setActive(short tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeTime,
			float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeTime;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		elapsedTime = 0;
		alive = true;
	}

	/**
	 * add the particle to your ParticleMaster afterwards. Or just let the Master do
	 * this!
	 */
	public void setActive(short tex, Vector3f position, Vector3f velocity, float gravityEffect, float lifeTime,
			float elapsedTime, float rotation, float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeTime;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		this.elapsedTime = elapsedTime;
		alive = true;
	}

	public void setActive(short tex, float x, float y, float z, float vx, float vy, float vz, float gravityEffect,
			float lifeTime, float elapsedTime, float rotation, float scale) {
		this.position.set(x, y, z);
		if (this.velocity == Vects.NULL)
			velocity = new Vector3f();
		this.velocity.set(vx, vy, vz);
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeTime;
		this.rotation = rotation;
		this.scale = scale;
		this.tex = tex;
		this.elapsedTime = elapsedTime;
		alive = true;
	}

	public void getTextureCoordInfo(int numberOfRows, int squared, Vector2f offset1, Vector2f offset2) {
		float lifeFactor = elapsedTime / lifeLength;
		float atlasProgression = lifeFactor * squared;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < squared - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		int column = index1 % numberOfRows;
		int row = index1 / numberOfRows;
		offset1.x = (float) column / numberOfRows;
		offset1.y = (float) row / numberOfRows;

		column = index2 % numberOfRows;
		row = index2 / numberOfRows;
		offset2.x = (float) column / numberOfRows;
		offset2.y = (float) row / numberOfRows;
	}

	public void setPosition(Vector3f pos) {
		this.position = pos;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void hide() {
		elapsedTime = lifeLength + 1;
	}

	public float remainingLifeTime() {
		return lifeLength - elapsedTime;
	}

	public void enlargeEffect() {
		enlargeEffect = true;
	}

	private boolean enlargeEffect = false;

	public void kill() {
		elapsedTime = lifeLength + 1;
	}

	public float gravity() {
		return gravityEffect;
	}

	public float distance() {
		return distance;
	}

}
