package generic;

import static tools.Meth.abs;

import java.lang.Math;
import java.util.*;

import org.joml.*;

import collectionsStuff.SmartByteBuffer;
import genericRendering.MasterRenderer;
import loaders.ModelCache;
import models.Asset;
import models.animated.AnimatedModelData;
import models.components.AnimationComponent;
import models.material.Material;
import openGlResources.buffers.VAO;
import tools.misc.Vects;
import vr.VRController;
import vr.controllerstuff.VRGrabbable;

// why exactly was this abstract? If it's necessary for setting parents of models then this should be instanceable
public class Thing extends Transform implements Asset, VRGrabbable, UpdatedThing {

	public static final int DRAWMODE_PERSPECTIVE = 0, DRAWMODE_VIEW = 1, DRAWMODE_RAW = 2, DRAWMODE_SCALED = 3;

	public static float gravity = -9.81f;

	protected AnimationComponent animation;
	protected Thing parent;
	protected Vector3f scale, absolutePosition = new Vector3f();
	protected Vector3f velocity = new Vector3f(), absoluteVelocity = new Vector3f();
	protected Quaternionf absoluteRotation = new Quaternionf(), angularVelocity = new Quaternionf();
	/**
	 * the values should be inferred by the VAO or the class, so they don't have to
	 * be saved by themselves! If you want or need to have them saved in your class
	 * then do it yourself
	 */
	protected transient Vector3f intrinsicSize = new Vector3f();
	protected transient Vector3f intrinsicMiddle = new Vector3f();

	protected List<Asset> assets;
	protected VAO vao;
	protected Material material;
	protected float mass = 1, groundHeight = 0;
	public boolean doPhysics = true, scaleChildren = false;
	protected boolean shown = false, cullFaces = true, cullModel = true, exactPicking = true, held = false;
	protected int drawMode = 0, nearLights = 0;
	protected float fakeLight = 0.125f;

	public Thing() {
		super();
		scale = new Vector3f(1);
	}

	public Thing(VAO vao, Material m) {
		this();
		this.vao = vao;
		if (vao != null) {
			this.intrinsicMiddle.set(vao.middle());
			this.intrinsicSize.set(vao.size());
		}
		this.material = m;
	}

	public Thing(Vector3f pos) {
		super(pos, new Quaternionf());
		scale = new Vector3f(1);
	}

	public Thing(float x, float y, float z) {
		this(new Vector3f(x, y, z));
	}

	public Thing(Vector3f position, Quaternionf rotation) {
		super(position, rotation);
		this.scale = new Vector3f(1);
	}

	public Thing(Vector3f position, Vector3f scale) {
		super(position, new Quaternionf());
		this.scale = scale;
	}

	public Thing(Vector3f position, Quaternionf rotation, Vector3f scale) {
		super(position, rotation);
		this.scale = scale;
	}

	public Thing(VAO vao, Material mat, float scale) {
		this(vao, mat, new Vector3f(), scale);
	}

	public Thing(VAO vao, Material mat, Vector3f position) {
		this(vao, mat, position, 1);
	}

	public Thing(VAO vao, Material mat, Vector3f position, float scale) {
		this(position);
		this.vao = vao;
		this.intrinsicMiddle.set(vao.middle());
		this.intrinsicSize.set(vao.size());
		this.material = mat;
		this.scale(scale);
	}

	public Thing(AnimatedModelData animdat, Material mat, Vector3f position, float scale) {
		this(animdat.getVAO(), mat, position, scale);
		animation = new AnimationComponent(animdat);
		animation.setParent(this);
	}

	public Thing(Thing toClone) {
		this();
		this.position.set(toClone.position);
		this.rotation.set(toClone.rotation);
		this.scale.set(toClone.scale);
		this.vao = toClone.vao;
		this.material = toClone.material;
		this.doPhysics = toClone.doPhysics;
		this.cullFaces = toClone.cullFaces;
		this.cullModel = toClone.cullModel;
		this.exactPicking = toClone.exactPicking;
		this.drawMode = toClone.drawMode;
		this.fakeLight = toClone.fakeLight;
		this.nearLights = toClone.nearLights;
		this.intrinsicSize.set(toClone.intrinsicSize);
		this.intrinsicMiddle.set(toClone.intrinsicMiddle);
		this.velocity.set(toClone.velocity);
		this.angularVelocity.set(toClone.angularVelocity);
		if (toClone.assets != null) {
			for (int i = 0; i < toClone.assets().size(); i++)
				add(toClone.assets.get(i).clone());
		}
		if (toClone.animation != null)
			this.animation = toClone.animation.clone();
	}

	public Thing(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public void detachFromParent() {
		if (parent == null)
			return;
		velocity.set(position);// for the angular velocity calculation
		position.set(absolutePosition);
		parent.rotation.mul(rotation, rotation);
		parent.angularVelocity.mul(this.angularVelocity, this.angularVelocity);
////		extract AxisAngle from the parents angular velocity.
//		float ax, ay, az, a;
//		float x = parent.angularVelocity.x;
//		float y = parent.angularVelocity.y;
//		float z = parent.angularVelocity.z;
//		float w = parent.angularVelocity.w;
//		if (w > 1.0f) {
//			float invNorm = (float) (1.0 / Math.sqrt(x * x + y * y + z * z + w * w));
//			x *= invNorm;
//			y *= invNorm;
//			z *= invNorm;
//			w *= invNorm;
//		}
//		a = (float) (2.0f * Math.acos(w));
//		float s = (float) Math.sqrt(1.0 - w * w);
//		if (s < 0.001f) {
//			ax = x;
//			ay = y;
//			az = z;
//		} else {
//			s = 1.0f / s;
//			ax = x * s;
//			ay = y * s;
//			az = z * s;
//		}
		// apply the actual angular velocity of the parent
		velocity.cross(velocity).add(parent.velocity());
//		float r = velocity.length();
//		velocity.cross(ax, ay, az);
//		velocity.normalize().mul(a * r).add(parent.velocity());

		doPhysics = true;
		parent = null;
	}

	public void attachToParent(Thing t) {
		if (parent != null)
			detachFromParent();
		if (t != null) {
			doPhysics = false;
			velocity.set(0);
			angularVelocity.identity();
			Vects.quat.set(t.rotation).invert().mul(rotation, rotation);
			position.sub(t.position);
//			AppFolder.log.print(Meth.coordinatesToString(position) + " -> ");
			Vects.quat.transform(position);
//			AppFolder.log.println(Meth.coordinatesToString(position), false);
			this.parent = t;
		}
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
		if (assets != null)
			for (int i = 0; i < assets.size(); i++)
				assets.get(i).updateClient(frameTimeSeconds, null);
		return false;
	}

	@Override
	public boolean updateServer(float frameTimeSeconds) {
		if (doPhysics) {
			Vector3f s = transformedSize(Vects.calcVect());
			Vector3f m = transformedMiddle(Vects.calcVect2());
			if (position.y + m.y - s.y * 0.5f <= groundHeight && velocity.y <= 0) {
				position.y = groundHeight - m.y + s.y * 0.5f;
			} else {
				velocity.y += gravity * frameTimeSeconds;
				// bounce
//				if (position.y < groundHeight && velocity.y < 0) {position.y = groundHeight;if (velocity.y > -0.1f) {	velocity.y = 0;	} else {velocity.y *= -0.2f;}}
			}
			if (position.y + m.y - s.y * 0.5f <= groundHeight && velocity.y <= 0) {
				float f = Math.max(0, 1 - frameTimeSeconds * 7);
				velocity.x *= f;
				if (abs(velocity.x) < 0.01f)
					velocity.x = 0;
				velocity.z *= f;
				if (abs(velocity.z) < 0.01f)
					velocity.z = 0;
				velocity.y = 0;

				if (f > 0)
					angularVelocity.nlerp(Vects.identityQuat, f);
				else
					angularVelocity.identity();

				// box collision to ground. Not actually this simple but looks cool as it is.
				// FIXME actually consider the center of mass, not just directions. This works
				// for cubes & nothing more!
				// -> plan:
				// center of mass for now: m
				// if this works, perhaps make a guess by averaging vertices.
				// the side this actually has to fall on is the one where the center of mass is
				// over it.
				// next up: this on rotated planes. Should just be DOWN -> normal.
				// next next up: Collision one box -> another box. Broad phase filters.

				Vector3f nearest = rotateAbsolute(Vects.calcVect().set(-1, 0, 0));
				float n = nearest.y;
				Vector3f c = rotateAbsolute(Vects.calcVect2().set(1, 0, 0));
				float d = c.y;
				if (d < n) {
					n = d;
					nearest.set(c);
				}
				for (int y = -1; y <= 1; y += 2) {
					rotateAbsolute(c.set(0, y, 0));
					d = c.y;
					if (d < n) {
						n = d;
						nearest.set(c);
					}
				}
				for (int z = -1; z <= 1; z += 2) {
					rotateAbsolute(c.set(0, 0, z));
					d = c.y;
					if (d < n) {
						n = d;
						nearest.set(c);
					}
				}
				if (nearest.y > -0.9999f) {
					Vects.quat.identity().rotateTo(nearest, Vects.DOWN);
					if (Vects.quat.lengthSquared() < 1)
						Vects.quat.nlerp(Vects.identityQuat, Math.max(0, 1 - 50 * frameTimeSeconds));
					Vects.quat.mul(rotation, rotation);
				}
			}
		}
		if (!held)
			position.add(velocity.x * frameTimeSeconds, velocity.y * frameTimeSeconds, velocity.z * frameTimeSeconds);
		rotation.mul(Vects.quat.set(angularVelocity).nlerp(Vects.identityQuat, 1 - frameTimeSeconds));
		return false;
	}

	public boolean visible(FrustumIntersection f) {
		Vector3f m = transformedMiddle(Vects.calcVect());
		Vector3f s = transformedSize(Vects.calcVect2()).mul(0.5f);
		int i = f.intersectAab(m.x + absolutePosition().x - s.x, m.y + absolutePosition().y - s.y,
				m.z + absolutePosition().z - s.z, m.x + absolutePosition().x + s.x, m.y + absolutePosition().y + s.y,
				m.z + absolutePosition().z + s.z);
		return i <= 0;
	}

	@Override
	public void pickedUp(VRController c) {
		attachToParent(c);
	}

	@Override
	public boolean update(VRController c) {
		return false;
	}

	@Override
	public void released(VRController c) {
		detachFromParent();
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		add(dest, scale);
		vector3f(dest, velocity == null ? new Vector3f() : velocity);
		quaternionf(dest, angularVelocity);
		mass = dest.readFloat();
		groundHeight = dest.readFloat();
		dest.addString((vao == null || vao.name() == null) ? "" : vao.name());
		material.addData(dest);
		byte c = 0x00;
		if (cullFaces)
			c |= 0b1;
		if (cullModel)
			c |= 0b10;
		if (doPhysics)
			c |= 0b100;
		dest.add(c);
		dest.addInt(drawMode);
		dest.addFloat(fakeLight);
		// TODO parent ID?
		// because that's not only needed in Server-Client situations...
		// so save all IDs?!? Because that could give problems in *massive* games
		// so not make massive games then?
	}

	@Override
	public void applyData(SmartByteBuffer src, Map<String, Short> saveVersions) {
		super.applyData(src, saveVersions);
		scale = vector3f(src);
		String name = src.readString();
		if (name.length() > 0) {
			if ((this.vao == null || !this.vao.name().equals(name)))
				this.vao = ModelCache.get().getLoadedModel(name);
		} else {
			this.vao = null;
		}
		if (material == null)
			material = new Material();
		material.applyData(src, saveVersions);
		byte c = src.read();
		cullFaces = (c & 0b1) != 0;
		cullFaces = (c & 0b10) != 0;
		doPhysics = (c & 0b100) != 0;
		drawMode = src.readInt();
		fakeLight = src.readFloat();

	}

	// methods set in stone (basically never gonna change, mostly just getters &
	// setters.)

	public void scale(float f) {
		scale.set(f);
	}

	public void setScale(float x, float y, float z) {
		scale.set(x, y, z);
	}

	public void setScale(Vector3f s) {
		this.scale.set(s);
	}

	public void setScaleRef(Vector3f ref) {
		this.scale = ref;
	}

	public Vector3f scale() {
		return scale;
	}

	public Matrix4f scale(Matrix4f m) {
		return m.scale(scale.x, scale.y, scale.z);
	}

	public Matrix4f createTransformationMatrix(Matrix4f m) {
		Matrix4f ret = transform(m.identity());
		ret.transformPosition(absolutePosition.set(0));
		ret.getUnnormalizedRotation(absoluteRotation);

		return ret;
	}

	@Override
	public Matrix4f transform(Matrix4f m) {
		if (parent == null)
			return scale(rotate(translate(m)));
		else// so good!
			return scale(rotate(translate(parent.parentTransform(m))));
	}

	public Matrix4f parentTransform(Matrix4f m) {
		if (parent == null) {
			if (scaleChildren)
				return scale(rotate(translate(m)));
			else
				return rotate(translate(m));
		} else if (scaleChildren)
			return scale(rotate(translate(parent.parentTransform(m))));
		else// so good!
			return rotate(translate(parent.parentTransform(m)));
	}

	public void set(VAO vao, Material mat) {
		this.vao = vao;
		this.material = mat;
	}

	public void set(VAO vao, Material mat, float scale) {
		this.vao = vao;
		this.material = mat;
		this.scale(scale);
	}

	public boolean shown() {
		return shown;
	}

	public void shown(boolean b) {
		shown = b;
	}

	@Override
	public boolean exactPicking() {
		return exactPicking;
	}

	public boolean cullFaces() {
		return cullFaces;
	}

	public void cullFaces(boolean b) {
		cullFaces = b;
	}

	/**
	 * @return if this model is *allowed* to be culled, not if it should be.
	 *         default: true
	 */
	public boolean cullModel() {
		return cullModel;
	}

	public void cullModel(boolean b) {
		cullModel = b;
	}

	public void drawModeScaled() {
		drawMode(DRAWMODE_SCALED);
	}

	@Override
	public void drawMode(int d) {
		drawMode = d;
		if (assets != null)
			for (int i = 0; i < assets.size(); i++)
				assets.get(i).drawMode(d);
	}

	@Override
	public void add(Asset a) {
		if (assets == null)
			assets = new ArrayList<>();
		assets.add(a);
	}

	@Override
	public List<Asset> assets() {
		return assets;
	}

	public void addChild(Asset a) {
		add(a);
		a.setParent(this);
	}

	@Override
	public void remove(Asset a) {
		if (assets != null)
			assets.remove(a);
	}

	public void setParent(Thing t) {
		this.parent = t;
	}

	public Thing parent() {
		return parent;
	}

	/**
	 * @return basically intrinsic width (x) * scale.x (default: scale.x)
	 */
	public float scaledWidth() {
		return scale.x * intrinsicSize.x;
	}

	/**
	 * @return basically intrinsic height (y) * scale.y (default: scale.y)
	 */
	public float scaledHeight() {
		return scale.y * intrinsicSize.y;
	}

	/**
	 * @return basically intrinsic depth (z) * scale.z (default: scale.z)
	 */
	public float scaledDepth() {
		return scale.z * intrinsicSize.z;
	}

	public void setIntrinsicWidth(float f) {
		this.intrinsicSize.x = f;
	}

	public void setIntrinsicHeight(float f) {
		this.intrinsicSize.y = f;
	}

	public void setIntrinsicDepth(float f) {
		this.intrinsicSize.z = f;
	}

	public float intrinsicWidth() {
		return intrinsicSize.x;
	}

	public float intrinsicHeight() {
		return intrinsicSize.y;
	}

	public float intrinsicDepth() {
		return intrinsicSize.z;
	}

	public Vector3f intrinsicSize() {
		return intrinsicSize;
	}

	public void setIntrinsicSize(float x, float y, float z) {
		intrinsicSize.set(x, y, z);
	}

	public void setIntrinsicSize(float size) {
		intrinsicSize.set(size);
	}

	public Vector3f intrinsicMiddle() {
		return intrinsicMiddle;
	}

	public boolean held() {
		return held;
	}

	public void held(boolean h) {
		held = h;
	}

	public void nearLights(int nearLights) {
		this.nearLights = nearLights;
	}

	public int nearLights() {
		return nearLights;
	}

	public VAO vao() {
		return vao;
	}

	public void setVao(VAO vao) {
		this.vao = vao;
	}

	public Material material() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public float fakeLight() {
		return fakeLight;
	}

	public void fakeLight(float f) {
		fakeLight = f;
	}

	public boolean drawPerspective() {
		return drawMode == 0;
	}

	public boolean drawView() {
		return drawMode < 2;
	}

	/**
	 * if the renderer should use the renderTarget scale matrix to make everything
	 * sqare go sqare again
	 */
	public boolean drawScaled() {
		return drawMode == DRAWMODE_SCALED;
	}

	public void setAnimationComponent(AnimationComponent anim) {
		animation = anim;
		animation.setParent(this);
	}

	public AnimationComponent animation() {
		return animation;
	}

	public void setGroundHeight(float height) {
		this.groundHeight = height;
	}

	/**
	 * actually calculates this from scratch, so don't use too heavily
	 */
	public boolean sitsOnGround() {
		Vector3f s = transformedSize(Vects.calcVect());
		Vector3f m = transformedMiddle(Vects.calcVect2());
		return position.y + m.y - s.y * 0.5f <= groundHeight && velocity.y <= 0;
	}

	public float groundHeight() {
		return groundHeight;
	}

	public Vector3f setVelocity(float x, float y, float z) {
		return this.velocity.set(x, y, z);
	}

	public Vector3f setVelocity(Vector3f vel) {
		return this.velocity.set(vel);
	}

	public Vector3f setVelocityRef(Vector3f ref) {
		return this.velocity = ref;
	}

	public void push(Vector3f vel) {
		velocity.add(vel);
	}

	public void push(float x, float y, float z) {
		velocity.add(x, y, z);
	}

	public Quaternionf angularVelocity() {
		return angularVelocity;
	}

	public Quaternionf setAngularVelocity(float x, float y, float z) {
		return angularVelocity.identity().rotateXYZ(x, y, z);
	}

	public Quaternionf setAngularVelocity(Vector3f angVel) {
		return setAngularVelocity(angVel.x, angVel.y, angVel.z);
	}

	public Quaternionf setAngularVelocityRef(Quaternionf angVel) {
		return angularVelocity = angVel;
	}

	public Vector3f velocity() {
		return velocity;
	}

	public void setMass(float m) {
		this.mass = m;
	}

	public float mass() {
		return mass;
	}

	public boolean containsPoint(Vector3f p) {
		return containsPoint(p.x, p.y, p.z);
	}

	/**
	 * Axis aligned bounding box checks again
	 */
	public boolean intersects(Thing other) {
		Vector3f p = transformedMiddle(Vects.calcVect());
		float dx = p.x + absolutePosition.x, dy = p.y + absolutePosition.y, dz = p.z + absolutePosition.z;
		p = other.transformedMiddle(Vects.calcVect()).add(other.absolutePosition);
		dx = abs(dx - p.x);
		dy = abs(dy - p.y);
		dz = abs(dz - p.z);
		Vector3f s = transformedSize(Vects.calcVect2());
		float sx = s.x, sy = s.y, sz = s.z;
		s = other.transformedSize(Vects.calcVect2());
		sx = (sx + s.x) * 0.5f;
		sy = (sy + s.y) * 0.5f;
		sz = (sz + s.z) * 0.5f;
		return dx < sx && dy < sy && dz < sz;
	}

	public Vector3f rotateAbsolute(Vector3f s) {
		if (parent == null)
			return rotation.transform(s);
		else
			return parent.rotateAbsolute(rotation.transform(s));
	}

	public Vector3f transformedSize(Vector3f s) {
		Vector3f r = rotateAbsolute(s.set(intrinsicSize).mul(scale));
		r.absolute();
		return r;
	}

	/**
	 * @return the *local* middle, so in (scaled) model space!
	 */
	public Vector3f transformedMiddle(Vector3f dest) {
		return rotateAbsolute(dest.set(intrinsicMiddle).mul(scale));
	}

	/**
	 * uses a barebone approximation with an axis aligned bounding box
	 */
	public boolean containsPoint(float x, float y, float z) {
		// the "if" is just there for the case one doesn't update their
		// absolutePosition. some mishaps can be solved by this
		if (parent == null) {
			Vector3f p = transformedMiddle(Vects.calcVect());
			Vector3f s = transformedSize(Vects.calcVect2());
			return abs(p.x + position.x - x) < s.x * 0.5f && abs(p.y + position.y - y) < s.y * 0.5f
					&& abs(p.z + position.z - z) < s.z * 0.5f;
		} else {
			Vector3f p = transformedMiddle(Vects.calcVect());
			Vector3f s = transformedSize(Vects.calcVect2());
			return abs(p.x + absolutePosition.x - x) < s.x * 0.5f && abs(p.y + absolutePosition.y - y) < s.y * 0.5f
					&& abs(p.z + absolutePosition.z - z) < s.z * 0.5f;
		}
	}

	/**
	 * is only updated when {@link Thing#createTransformationMatrix(Matrix4f)} is
	 * called. Or when {@link Thing#updateAbsolutePosition(Matrix4f)} is called
	 */
	public Vector3f absolutePosition() {
		return absolutePosition;
	}

	/**
	 * use only on main thread. Because this uses the Vects.mat4 it's not thread
	 * safe at all
	 */
	public void updateAbsolutePosition() {
		updateAbsolutePosition(Vects.mat4);
	}

	public void updateAbsolutePosition(Matrix4f m) {
		if (parent == null)
			absolutePosition.set(position);
		else
			createTransformationMatrix(m);
	}

	public Thing clone() {
		return new Thing(this);
	}

}
