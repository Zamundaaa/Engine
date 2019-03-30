package models;

import java.util.*;

import org.joml.*;

import collectionsStuff.SmartByteBuffer;
import generic.Thing;
import loaders.ModelCache;
import models.material.Material;
import openGlResources.buffers.VAO;
import physics.collisions.Collider;
import tools.misc.Vects;

public class Model extends Thing implements Asset {

	public static final int DRAWMODE_PERSPECTIVE = 0, DRAWMODE_VIEW = 1, DRAWMODE_RAW = 2, DRAWMODE_SCALED = 3;

	protected List<Asset> assets;
	protected VAO vao;
	protected Material material;
	protected Collider coll;
	protected boolean shown = false, cullFaces = true, cullModel = true;
	protected int drawMode = 0;
	protected float fakeLight = 0.3f;
	/**
	 * makes efficient rendering easier!
	 */
	protected transient int nearLights = 0;

	public Model(VAO v, Material m) {
		this.vao = v;
		if (vao != null) {
			intrinsicSize.set(vao.size());
			intrinsicMiddle.set(vao.middle());
		}
		this.material = m;
	}

	public Model(Vector3f position) {
		super(position);
	}

	public Model() {
		super(new Vector3f());
	}

	public Model(VAO vao, Material material, Vector3f position) {
		super(position);
		this.vao = vao;
		intrinsicSize.set(vao.size());
		intrinsicMiddle.set(vao.middle());
		this.material = material;
	}

	public Model(VAO vao, Material material, Vector3f position, float scale, Quaternionf rotation) {
		super(position, rotation);
		this.vao = vao;
		intrinsicSize.set(vao.size());
		intrinsicMiddle.set(vao.middle());
		this.material = material;
		this.scale.set(scale);
	}

	public Model(VAO vao, Material material, Vector3f position, float scale) {
		super(position);
		this.vao = vao;
		if (vao != null) {
			intrinsicSize.set(vao.size());
			intrinsicMiddle.set(vao.middle());
		}
		this.material = material;
		this.scale.set(scale);
	}

	public Model(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public void setCollider(Collider coll) {
		this.coll = coll;
	}

	public Collider getCollider() {
		return coll;
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

	public void drawMode(int i) {
		drawMode = i;
	}

	public boolean shown() {
		return shown;
	}

	public void shown(boolean b) {
		shown = b;
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

	/**
	 * convenience method that deletes the bound VAO and possibly material (texture)
	 */
	public void delete() {
		if (vao != null)
			vao.delete();
		if (material != null)
			material.delete();
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

	public float weight() {
		return 1;
	}

	public boolean visible(FrustumIntersection f) {
		Vector3f m = transformedMiddle(Vects.calcVect());
		Vector3f s = transformedSize(Vects.calcVect2()).mul(0.5f);
		int i = f.intersectAab(m.x + absolutePosition().x - s.x, m.y + absolutePosition().y - s.y,
				m.z + absolutePosition().z - s.z, m.x + absolutePosition().x + s.x, m.y + absolutePosition().y + s.y,
				m.z + absolutePosition().z + s.z);
		return i <= 0;
//		return f.testPoint(absolutePosition);
	}

	public void nearLights(int nearLights) {
		this.nearLights = nearLights;
	}

	public int nearLights() {
		return nearLights;
	}

	@Override
	public void addData(SmartByteBuffer dest) {
		super.addData(dest);
		dest.addString((vao == null || vao.name() == null) ? "" : vao.name());
		material.addData(dest);
		byte c = 0x00;
		if (cullFaces)
			c |= 0b1;
		if (cullModel)
			c |= 0b10;
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
		drawMode = src.readInt();
		fakeLight = src.readFloat();
		// never reached at all!
	}

	public void add(Asset a) {
		if (assets == null)
			assets = new ArrayList<>();
		assets.add(a);
	}

	public void addChild(Asset a) {
		add(a);
		a.setParent(this);
	}

	@Override
	public List<Asset> assets() {
		return assets;
	}

	@Override
	public void remove(Asset a) {
		if (assets != null)
			assets.remove(a);
	}

}
