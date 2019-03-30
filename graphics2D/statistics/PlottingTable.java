package graphics2D.statistics;

import static tools.Meth.randomFloat;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import generic.Thing;
import genericRendering.MasterRenderer;
import models.material.Material;

public class PlottingTable extends Thing {

	protected List<List<LineGraph>> l = new ArrayList<>();

	public PlottingTable(Vector3f pos, float width, float height, int divisionsX, int divisionsY, float spacing,
			Material lineMat) {
		super(pos);
		if (spacing >= 1 || spacing < 0)
			spacing = 0;
		scaleChildren = true;
		doPhysics = false;// just to be sure.
		this.intrinsicSize.set(width, height, 0.001f);
		final float wt = (width / divisionsX) * (1 - spacing);
		final float ht = (height / divisionsY) * (1 - spacing);
		final float xo = wt;
		for (int x = 0; x < divisionsX; x++) {
			List<LineGraph> sl = new ArrayList<>();
			l.add(sl);
			for (int y = 0; y < divisionsY; y++) {
				float fx = x - divisionsX / 2f;
				float fy = y - divisionsY / 2f;
				LineGraph lg = new LineGraph(300, 100, fx * width / divisionsX + xo, fy * height / divisionsY, wt, ht,
						0, 0,
						lineMat != null ? lineMat
								: new Material(randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1), 1),
						0.003f, null);
				lg.automaticResize(true);
				sl.add(lg);
				addChild(lg);
			}
		}
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
//		for(int i = 0; i < l.size(); i++) {
//			List<LineGraph> sl = l.get(i);
//			for(int j = 0; j < sl.size(); j++) {
//				LineGraph g = sl.get(j);
//				g.update
//			}
//		}
		return super.updateClient(frameTimeSeconds, mr);
	}

	public void push(int x, int y, float f) {
		LineGraph g = get(x, y);
		if (g == null)
			return;
		g.push(f);
	}

	public LineGraph get(int x, int y) {
		if (x >= l.size())
			return null;
		List<LineGraph> sl = l.get(x);
		if (y >= sl.size())
			return null;
		return sl.get(y);
	}

	public int xGridSize() {
		return l.size();
	}

	public int yGridSize() {
		if (l.size() == 0)
			return 0;
		return l.get(0).size();
	}

}
