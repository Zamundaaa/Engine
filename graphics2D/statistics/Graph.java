package graphics2D.statistics;

import java.util.ArrayList;

import collectionsStuff.ArrayListFloat;
import graphics2D.statistics.FunctionPlotter.FloatFunction1D;
import models.AssetHolder;
import models.geometry.Line;
import models.material.Material;

public class Graph extends AssetHolder {

	protected String label;
	protected String unit;// optional
	protected ArrayListFloat values;
	protected ArrayList<Line> lines;
	protected float startValue, maxValue, lineThickness;
	protected float actualMinValue, actualMaxValue;

	public Graph(int nrOfValues, float maxValue, float x, float y, float width, float height, Material lineMat,
			float lineThickness) {
		super(x, y, 0);
		this.material = lineMat;
		this.intrinsicSize.set(width, height, 0.001f);
		this.lineThickness = lineThickness;
		this.maxValue = maxValue;
		values = new ArrayListFloat(nrOfValues + 1);
		lines = new ArrayList<>();
		for (int i = 0; i < nrOfValues; i++) {
			Line l = new Line(lineMat, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, lineThickness);
			lines.add(l);
			addChild(l);
		}
	}

	public void plotFunction(FloatFunction1D func, float startx, float endx) {
		for (int i = this.values.size(); i < this.values.capacity() - 1; i++)
			this.values.add(0);
		float step = (endx - startx) / this.values.size();
		for (int i = 0; i < this.values.size(); i++) {
			float f = func.get(startx + step * i);
			this.values.set(i, f);
		}
		setLines();
	}

	public void setMaxValue(float f) {
		this.maxValue = f;
	}

	public void setMinValue(float f) {
		this.startValue = f;
	}

	public void set(int i, float f) {
		values.set(i, f);
		setLines();
	}

	public boolean push(float f) {
		if (values.size() == lines.size())
			values.remove(0);
		values.add(f);
		boolean resized = false;
		if (f > actualMaxValue) {
			actualMaxValue = f;
			resized = true;
		} else if (f < actualMinValue) {
			actualMinValue = f;
			resized = true;
		}
		/*
		 * if (automaticResize) { if (f > maxValue) { maxValue = f; } else if (f <
		 * startValue) { startValue = f; } else { float d = 0.5f * (actualMaxValue -
		 * actualMinValue); maxValue = Meth.converge(maxValue, d, actualMaxValue);
		 * startValue = Meth.converge(startValue, d, actualMinValue); } }
		 */
		setLines();
		return resized;
	}

	public void setLines() {
//		final float startValue = actualMinValue;
//		final float maxValue = actualMaxValue;
		float x, y, nx, ny;
		x = 0;
		y = this.intrinsicHeight() * (values.get(0) - startValue) / (maxValue - startValue);
		for (int i = 1; i <= lines.size() && i < values.size(); i++) {
			nx = (i / (float) lines.size()) * this.intrinsicWidth();
			ny = this.intrinsicHeight() * (values.get(i) - startValue) / (maxValue - startValue);
			lines.get(i - 1).set(nx, ny, 0, x, y, 0);
			x = nx;
			y = ny;
		}
	}

	public int nrOfValues() {
		return values.size();
	}

	public ArrayListFloat valueList() {
		return values;
	}

	public float lineThickness() {
		return lineThickness;
	}

	public float maxValue() {
		return maxValue;
	}

	public float getActualMinValue() {
		return actualMinValue;
	}

	public float getActualMaxValue() {
		return actualMaxValue;
	}

}
