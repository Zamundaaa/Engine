package graphics2D;

import java.util.Map;

import org.joml.Vector3f;

import collectionsStuff.SmartByteBuffer;
import genericRendering.Square;
import models.material.Material;

public class Square2D extends Square {

	public Square2D(float x, float y, float width, float height, Material m) {
		super(new Vector3f(x, y, 0), width, height, m);
		drawMode(DRAWMODE_SCALED);
		fakeLight(1);
	}

	public Square2D(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

}
