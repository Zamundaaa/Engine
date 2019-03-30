package graphics2D;

import java.util.Map;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import genericRendering.Rectangle;
import models.material.Material;

public class Rectangle2D extends Rectangle {

	public Rectangle2D(float x, float y, float width, float height, Material m) {
		super(new Vector3f(x, y, 0), width, height, m);
		drawMode(DRAWMODE_SCALED);
		cullModel = false;
		fakeLight(1);
	}

	public Rectangle2D(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

}
