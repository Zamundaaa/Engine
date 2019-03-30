package gui;

import org.joml.Vector3f;

import genericRendering.Rectangle;
import models.AssetHolder;
import models.material.Material;
import models.text.Text;

public class Textbox extends AssetHolder {

	protected boolean maxFittingTextSize = false;
	protected Text text;
	protected Rectangle background;
	protected Vector3f pos;

	public Textbox(String content, float x, float y, float z, float width, float height, Material textMat,
			Material backgroundMat) {
		pos = new Vector3f(x, y, z);
		text = new Text(content, x, y, z, textMat, true);
		text.fakeLight(1);
		background = new Rectangle(x, y, z + 0.001f, width, height, backgroundMat);
		add(text);
		add(background);
		scaleText();
	}

	public Textbox(String content, float x, float y, float width, float height, Material m, Material backgroundMat) {
		this(content, x, y, 0, width, height, m, backgroundMat);
	}

	public Textbox(String content, float width, float height, Material m, Material backgroundMat) {
		this(content, 0, 0, 0, width, height, m, backgroundMat);
	}

	public void setText(String t) {
		this.text.set(t);
		scaleText();
	}

	private void scaleText() {
		if (text.width() * 2.5f > background.scale().x * 2)
			text.scale(background.scale().x * 2 * 0.95f / text.width());
		else
			text.scale(2.5f);
	}

	public void setPosition(float x, float y, float z) {
		pos.set(x, y, z);
		text.setPosition(x, y, z);
		background.setPosition(x, y, z);
		background.position().z += 0.01f;
	}

}
