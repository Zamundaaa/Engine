package models.geometry;

import static tools.Meth.xOnCircle;
import static tools.Meth.yOnCircle;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import generic.Thing;
import genericRendering.MasterRenderer;
import models.AssetHolder;
import models.material.Material;

public class Arc2D extends AssetHolder {

	protected List<Line2D> lines = new ArrayList<>();
	protected Thing middle;
	protected float startAngle, endAngle, radius, width;
	protected int intersections;
	protected Material material;

	public Arc2D(float width, Material m, float x, float y, float radius, int intersections, float startAngle,
			float endAngle) {
		this(width, m, new Vector2f(x, y), radius, intersections, startAngle, endAngle);
	}

	public Arc2D(float width, Material m, Vector2f middle, float radius, int intersections, float startAngle,
			float endAngle) {
		this.middle = new Thing(middle.x, middle.y, -0.06f);
		this.material = m;
		set(middle.x, middle.y, radius, intersections, width, startAngle, endAngle);
	}

	/**
	 * if you change the amount of intersections remove _this_ from the renderer
	 * first and add it again afterwards or you'll see some weird stuff happening.
	 * Or (better, vastly more efficient!) use
	 * {@link Arc2D#set(float, float, float, int, float, float, float, MasterRenderer)}
	 */
	public void set(float x, float y, float radius, int intersections, float width, float startAngle, float endAngle) {
		set(x, y, radius, intersections, width, startAngle, endAngle, null);
	}

	public void set(float x, float y, float radius, int intersections, float width, float startAngle, float endAngle,
			MasterRenderer r) {
//		if (startAngle == 0)
//			startAngle = 0.0001f;
		this.middle.position().x = x;
		this.middle.position().y = y;
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
				Line2D l = lines.get(i);
				l.set(nextX, nextY, nx, ny, width);
			} else {
				Line2D l = new Line2D(material, nextX, nextY, nx, ny, width);
				l.setParent(middle);
				lines.add(l);
				add(l);
				if (r != null && shown)
					r.add(l);
			}
			nextX = nx;
			nextY = ny;
		}
		for (int i = intersections; i < lines.size();) {
			Line2D l = lines.remove(i);
			remove(l);
			if (r != null)
				r.remove(l);
		}
	}

	public void setDepth(float d) {
		middle.position().z = d;
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

}
