package models.geometry;

import static tools.Meth.xOnCircle;
import static tools.Meth.yOnCircle;

import java.util.ArrayList;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Vector3f;

import generic.Thing;
import genericRendering.MasterRenderer;
import models.Asset;
import models.material.Material;

public class Arc extends Thing implements Asset {

	protected List<Asset> assets = new ArrayList<>();
	protected List<Line> lines = new ArrayList<>();
	protected float startAngle, endAngle, radius, width;
	protected int intersections;
	protected Material material;
	protected boolean shown = false, cullModels = true;

	public Arc(float width, Material m, float x, float y, float z, float radius, int intersections, float startAngle,
			float endAngle) {
		this(width, m, new Vector3f(x, y, z), radius, intersections, startAngle, endAngle);
	}

	public Arc(float width, Material m, Vector3f middle, float radius, int intersections, float startAngle,
			float endAngle) {
		super(middle);
		this.material = m;
		set(middle.x, middle.y, middle.z, radius, intersections, width, startAngle, endAngle);
	}

	/**
	 * if you change the amount of intersections remove _this_ from the renderer
	 * first and add it again afterwards or you'll see some weird stuff happening.
	 * Or (better, vastly more efficient!) use
	 * {@link Arc2D#set(float, float, float, int, float, float, float, MasterRenderer)}
	 */
	public void set(float x, float y, float z, float radius, int intersections, float width, float startAngle,
			float endAngle) {
		set(x, y, z, radius, intersections, width, startAngle, endAngle, null);
	}

	public void set(float x, float y, float z, float radius, int intersections, float width, float startAngle,
			float endAngle, MasterRenderer r) {
//		if (startAngle == 0)
//			startAngle = 0.0001f;
		position.set(x, y, z);
		this.radius = radius;
		this.intersections = intersections;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.width = width;
		final float pangle = (endAngle - startAngle) / intersections;
		float nextX = xOnCircle(0, radius, startAngle);// x
		float nextY = yOnCircle(0, radius, startAngle);// y
		for (int i = 0; i < intersections; i++) {
			float a = startAngle + (i + 1) * pangle;
			float nx = xOnCircle(0, radius, a);// x
			float ny = yOnCircle(0, radius, a);// y
			if (lines.size() > i) {
				Line l = lines.get(i);
				l.set(nextX, nextY, 0, nx, ny, 0, width);
				if (r != null && shown && !l.shown())
					r.add(l);
			} else {
				Line l = new Line(material, nextX, nextY, 0, nx, ny, 0, width);
				l.cullFaces(false);
				l.setParent(this);
				lines.add(l);
				add(l);
				l.cullModel(cullModels);
				if (r != null && shown)
					r.add(l);
			}
			nextX = nx;
			nextY = ny;
		}
		for (int i = intersections; i < lines.size();) {
			Line l = lines.remove(i);
			remove(l);
			if (r != null)
				r.remove(l);
		}
	}

	public void set(float startAngle, float endAngle) {
		set(position.x, position.y, position.z, radius, intersections, width, startAngle, endAngle);
	}

	public void set(float radius, float startAngle, float endAngle) {
		set(position.x, position.y, position.z, radius, intersections, width, startAngle, endAngle);
	}

	public void setMaterial(Material m) {
		this.material = m;
		for (int i = 0; i < lines.size(); i++)
			lines.get(i).setMaterial(m);
	}

	public Material material() {
		return material;
	}

	public float width() {
		return width;
	}

	public int intersections() {
		return intersections;
	}

	@Override
	public boolean shown() {
		return shown;
	}

	@Override
	public List<Asset> assets() {
		return assets;
	}

	@Override
	public void drawMode(int d) {
		for (int i = 0; i < assets.size(); i++)
			assets.get(i).drawMode(d);
	}

	@Override
	public void shown(boolean b) {
		this.shown = b;
	}

	@Override
	public void add(Asset a) {
		assets.add(a);
	}

	@Override
	public void remove(Asset a) {
		assets.remove(a);
	}

	public Vector3f intersectionPoint(Vector3f a, Vector3f b, Vector3f dest) {
		return intersectionPoint(a, b.x - a.x, b.y - a.y, b.z - a.z, dest);
	}

	public Vector3f intersectionPoint(Vector3f p, float rx, float ry, float rz, Vector3f dest) {
		return intersectionPoint(p.x, p.y, p.z, rx, ry, rz, dest);
	}

	/**
	 * just 2D (x & y) right now...
	 */
	public Vector3f intersectionPoint(float px, float py, float pz, float rx, float ry, float rz, Vector3f dest) {
		float minx = -1;
		for (int i = 0; i < lines.size(); i++) {
			Line l2 = lines.get(i);
//			float dx2 = l2.second().x - l2.position().x, dy2 = l2.second().y - l2.position().y,
//					dz2 = l2.second().z - l2.position().z;
			float x = Intersectionf.intersectRayLineSegment(px, py, rx, ry, l2.position().x, l2.position().y,
					l2.second().x, l2.second().y);
			if (x != -1 && (minx == -1 || x < minx)) {
				minx = x;
			}
		}
		if (minx != -1) {
			dest.set(rx, ry, rz).mul(minx).add(px, py, pz);
			return dest;
		}
		return null;
	}

	public void cullModels(boolean b) {
		this.cullModels = b;
		for (int i = 0; i < lines.size(); i++) {// TODO assets...
			lines.get(i).cullModel(cullModels);
		}
	}

}
