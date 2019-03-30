package tools.misc;

import tools.Meth;

public class Rectangle {

	public float x, y, w, h;

	public Rectangle() {

	}

	public Rectangle(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rectangle(Rectangle r) {
		this(r.x, r.y, r.w, r.h);
	}

	public boolean contains(float x, float y) {
		return x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public void setBounds(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean isNull() {
		return x == 0 && y == 0 && w == 0 && h == 0;
	}

	public boolean noSize() {
		return w == 0 && h == 0;
	}

	@Override
	public String toString() {
		return "X: " + Meth.floatToString(x, 1) + " Y; " + Meth.floatToString(y, 1) + " W: " + Meth.floatToString(w, 1)
				+ " H: " + Meth.floatToString(h, 1);
	}

	public void mul(float scale) {
		x *= scale;
		y *= scale;
		w *= scale;
		h *= scale;
	}

	public void setSize(float w, float h) {
		this.w = w;
		this.h = h;
	}

}
