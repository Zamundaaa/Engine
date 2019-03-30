package graphics2D.statistics;

import java.util.ArrayList;
import java.util.List;

import models.material.Material;

public class FunctionPlotter extends LineGraph {

	public static interface FloatFunction1D {
		public float get(float x);
	}

	protected FloatFunction1D func;
	protected List<FloatFunction1D> funcs = new ArrayList<>();
	protected float xOffset, width;
	protected float contentRange = 1;
	protected float scale = 1 / contentRange;

	public FunctionPlotter(FloatFunction1D func, float contentRange, float xOffset, float yOffset, float x, float y,
			float width, float height, Material lineMat, Material background) {
		this(func, 100, contentRange, xOffset, yOffset, x, y, width, height, lineMat, background);
	}

	public FunctionPlotter(FloatFunction1D func, int numberOfElements, float contentRange, float xOffset, float yOffset,
			float x, float y, float width, float height, Material lineMat, Material background) {
		super(100, yOffset + contentRange, x, y, width, height, -2 * xOffset / contentRange,
				-2 * yOffset / contentRange, lineMat, height / 1000.0f, background);
		this.width = width;
		this.contentRange = contentRange;
		scale = 1 / contentRange;
		super.g.startValue = yOffset;
		this.xOffset = xOffset;
		this.func = func;
		plot();
	}

	@Override
	public Graph addNewGraph(Material line) {
		Graph g = super.addNewGraph(line);
		funcs.add((x) -> {
			return 0;
		});
		return g;
	}

	public float convertXToScreenSpace(float x) {
		return this.position.x + ((x - xOffset) / contentRange) * this.width;
	}

	public float convertYToScreenSpace(float y) {
		return this.position.y + ((y - super.g.startValue) / contentRange) * this.height;
	}

	public FloatFunction1D getFunction(int graph) {
		if (graph == 0)
			return func;
		else if (graph <= graphs.size())
			return funcs.get(graph - 1);
		else
			return null;
	}

	public void plotAll() {
		for (int i = 0; i <= graphs.size(); i++)
			plot(i);
	}

	public void plot() {
		plot(0);
	}

	public void plot(int graph) {
		Graph g = getGraph(graph);
		if (g == null)
			return;
		FloatFunction1D func = getFunction(graph);
		float x;
		for (int i = 0; i < super.g.values.capacity(); i++) {
			// 0.5f * und 2 * wegen blödem Koordinatensystem!
			x = xOffset - width + width * i * contentRange / g.values.capacity();
			if (g.values.size() <= i)
				g.values.add(func.get(x) * 2 * scale);
			else
				g.values.set(i, func.get(x) * 2 * scale);
		}
		g.setLines();
	}

	public float contentRange() {
		return contentRange;
	}

	public float xOffset() {
		return xOffset;
	}

	public float yOffset() {
		return super.g.startValue;
	}

	public void setFunction(FloatFunction1D func) {
		setFunction(func, 0);
	}

	public void setFunction(FloatFunction1D func, int graph) {
		if (graph == 0) {
			this.func = func;
			plot();
		} else if (graph <= graphs.size()) {
			funcs.set(graph - 1, func);
			plot(graph);
		}
	}

}
