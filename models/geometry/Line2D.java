package models.geometry;

import models.material.Material;

public class Line2D extends Line {

	public Line2D(Material m, float x, float y, float x2, float y2, float thickness) {
		super(m, x, y, -0.06f, x2, y2, -0.06f, thickness);
		drawMode = DRAWMODE_SCALED;
	}

	public void set(float x, float y, float x2, float y2, float thickness) {
		super.set(x, y, -0.06f, x2, y2, -0.06f, thickness);
//		position.set(x, y, -0.12f);
//		scale.set(thickness * 0.5f, Meth.getDistance(x, y, x2, y2) * 0.5f, 1);
//		float dx = x2 - x, dy = y2 - y;
//		if (dx == 0)
//			dx = 0.000001f;
//		rotation.identity().rotateTo(0, 1, 0, dx, dy, 0);
	}

	public void set(float x, float y, float x2, float y2) {
		set(x, y, x2, y2, thickness());
	}

	public float thickness() {
		return scale.x * 2;
	}

}
