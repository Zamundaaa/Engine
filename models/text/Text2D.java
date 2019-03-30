package models.text;

import loaders.fontMeshCreator.Font;
import models.material.Material;

public class Text2D extends Text {

	public Text2D(String t, float x, float y, float depth, Font f, Material m) {
		super(t, x, y, -depth, m, f, true);
		drawMode(DRAWMODE_SCALED);
		fakeLight(1);
		scale(4);
	}

	public Text2D(String t, float x, float y, float depth, Material m) {
		this(t, x, y, depth, Font.defaultFont, m);
	}

	public Text2D(String t, float x, float y, float depth, Font f, Material m, boolean centered) {
		super(t, x, y, -depth, m, f, centered);
		drawMode(DRAWMODE_SCALED);
		fakeLight(1);
	}

	public Text2D(String t, float x, float y, float depth, Material m, boolean centered) {
		super(t, x, y, -depth, m, Font.defaultFont, centered);
		drawMode(DRAWMODE_SCALED);
		fakeLight(1);
	}

//	@Override
//	public Matrix4f scale(Matrix4f m) {
//		return m.scale(scale.x, scale.y, scale.z);
//	}

//	@Override
//	public Matrix4f translate(Matrix4f m) {
//		if (center)
//			return m.translate(position.x - scaledWidth() * 0.5f, position.y + scaledHeight() * 0.5f, position.z);
//		return m.translate(position.x, position.y + scaledHeight(), position.z);
//	}
//
//	@Override
//	public Matrix4f transform(Matrix4f m) {
//		if (parent == null)
//			return scale(rotate(translate(m)));
//		else// the best!
//			return scale(rotate(translate(parent.transformWithoutScale(m))));
//	}

}
