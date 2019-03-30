package graphics2D.statistics;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import collectionsStuff.ArrayListFloat;
import genericRendering.MasterRenderer;
import models.AssetHolder;
import models.geometry.Line;
import models.material.Material;

public class LineGraph extends AssetHolder {

	protected float width, height;
	protected genericRendering.Rectangle back;
	protected Line xAchse, yAchse;
	protected Graph g;
	protected List<Graph> graphs = new ArrayList<>();
	protected boolean automaticResize;
	// TODO perhaps make this shit happen in units? Offset and everything.
	// TODO labels. units.

	public LineGraph(int nrOfValues, float maxValue, float x, float y, float width, float height, Material lineMat,
			float lineThickness, Material background) {
		this(nrOfValues, maxValue, x, y, width, height, 0, 0, lineMat, lineThickness, background);
	}

	/**
	 * @param xOffset offset in coordinates, not units of the graph!
	 * @param yOffset offset in coordinates, not units of the graph!
	 */
	public LineGraph(int nrOfValues, float maxValue, float x, float y, float width, float height, float xOffset,
			float yOffset, Material lineMat, float lineThickness, Material background) {
		super(x, y, 0);
		scaleChildren = true;
		this.width = width;
		this.height = height;
		// why does this go fron -width to width?
		xAchse = new Line(Material.blue, -width, yOffset, 0, 0, yOffset, 0, lineThickness);
		yAchse = new Line(Material.red, xOffset - width, 0, 0, xOffset - width, height, 0, lineThickness);
		addChild(xAchse);
		addChild(yAchse);
		g = new Graph(nrOfValues, maxValue, -width, 0, width, height, lineMat, lineThickness);
		addChild(g);
		if (background != null) {
			back = new genericRendering.Rectangle(new Vector3f(width / 2f, height / 2f, 0), width, height, background);
			addChild(back);
		}
	}

	@Override
	public boolean updateClient(float fts, MasterRenderer mr) {
		// passt die Grenzen im Moment instant an. Mach das hier nen Gradienten.
		// und: nur setLines() falls sich was geändert hat!
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (int i = 0; i <= graphs.size(); i++) {
			Graph g = getGraph(i);
			float m = g.getActualMinValue();
			if (m < min)
				min = m;
			m = g.getActualMaxValue();
			if (m > max)
				max = m;
		}
		for (int i = 0; i <= graphs.size(); i++) {
			Graph g = getGraph(i);
			g.setMinValue(min);
			g.setMaxValue(max);
			g.setLines();
		}
		return false;
	}

	public Graph getGraph(int i) {
		if (i == 0)
			return g;
		else if (i <= graphs.size())
			return graphs.get(i - 1);
		else
			return null;
	}

	public Graph addNewGraph(Material line) {
		Graph ret = new Graph(g.nrOfValues(), g.maxValue(), -width, 0, width * 2, height, line, g.lineThickness());
		addChild(ret);
		graphs.add(ret);
		return ret;
	}

	public ArrayListFloat valueList() {
		return g.values;
	}

	public void automaticResize(boolean b) {
		automaticResize = b;
	}

	public void push(float f) {
		g.push(f);
	}

	public void setLines() {
		g.setLines();
	}

}
