package genericRendering.lights;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import generic.Thing;
import models.Asset;

public class Light extends Thing implements Asset {

	protected Vector3f rgb;
	/**
	 * just a value to make calculations more efficient!
	 */
	protected float contribution;
	protected List<Asset> assets;

	public Light(float brightness) {
		this(brightness, brightness, brightness);
	}

	public Light(float r, float g, float b) {
		super();
		this.rgb = new Vector3f(r, g, b);
	}

	public Light(Vector3f pos, float r, float g, float b) {
		super(pos);
		this.rgb = new Vector3f(r, g, b);
	}

	public Light(float x, float y, float z, float r, float g, float b) {
		this(new Vector3f(x, y, z), r, g, b);
	}

	public Light(float x, float y, float z, float brightness) {
		super(x, y, z);
		this.rgb = new Vector3f(brightness);
	}

	public Light(Vector3f pos, Vector3f rgb) {
		super(pos);
		this.rgb = rgb;
	}
	
	public Light(Light toClone) {
		super(toClone);
		this.rgb = new Vector3f(toClone.rgb);
	}

//	/**
//	 * just a value to make calculations more efficient!
//	 */
//	public void distance(float d) {
//		this.distance = d;
//	}
//
//	/**
//	 * just a value to make calculations more efficient!
//	 */
//	public float distance() {
//		return distance;
//	}
	
	public void setColor(float r, float g, float b) {
		this.rgb.set(r, g, b);
	}
	
	public Vector3f color() {
		return this.rgb;
	}

	public float r() {
		return this.rgb.x;
	}

	public void r(float r) {
		this.rgb.x = r;
	}

	public float g() {
		return this.rgb.y;
	}

	public void g(float g) {
		this.rgb.y = g;
	}

	public float b() {
		return this.rgb.z;
	}

	public void b(float b) {
		this.rgb.z = b;
	}

	public void setWhite(float brightness) {
		setRGB(brightness, brightness, brightness);
	}

	public void setRGB(float r, float g, float b) {
		this.rgb.set(r, g, b);
	}

	@Override
	public void drawMode(int d) {

	}

	@Override
	public void shown(boolean b) {

	}

	@Override
	public boolean shown() {
		return false;
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

	@Override
	public Light clone() {
		return new Light(this);
	}

}
