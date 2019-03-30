package models.text;

import java.util.Map;

import org.joml.Matrix4f;

import collectionsStuff.SmartByteBuffer;
import generic.Thing;
import loaders.Loader;
import loaders.fontMeshCreator.Font;
import loaders.fontMeshCreator.TextMeshData;
import models.material.Material;
import openGlResources.buffers.VAO;

public class Text extends Thing {

	// TODO Savable

	protected String content;
	protected int lines;
	protected boolean center = false;
	protected float fontSize = 1f, maxLineSize = 0.5f;
	protected Font font;
	protected float modelLength, modelHeight;

	public Text(String t, Material m, Font f) {
		this(t, m, f, false);
	}

	public Text(String t, Material m, Font f, boolean center) {
		this(t, 0, 0, 0, m, f, center);
	}

	public Text(String t, Material m) {
		this(t, m, Font.defaultFont);
	}

	public Text(String t, Material m, boolean center) {
		this(t, m, Font.defaultFont, center);
	}

	public Text(String t, float x, float y, float z, Material m, boolean center) {
		this(t, x, y, z, m, Font.defaultFont, center);
	}

	public Text(String t, float x, float y, float z, Material m, Font f, boolean center) {
		super((VAO) null, m);
		position.set(x, y, z);
		this.material = m;
		this.center = center;
		this.content = t;
		if (f == null)
			f = Font.defaultFont;
		this.font = f;
		cullFaces(false);
		fakeLight(0.5f);
		scale(4);
		reload();
	}

	public Text(SmartByteBuffer data, Map<String, Short> versions) {
		super(data, versions);
	}

	public void set(String text) {
		if (!this.content.equals(text)) {
//			AppFolder.log.println(this.content + "->" + text);
			this.content = text;
			reload();
		}
	}

	/**
	 * really not recommended. Better use font size 1 (default) and scale up (scale
	 * default: 4)
	 */
	public void setFontSize(float fs) {
		if (fs != fontSize) {
			fontSize = fs;
			reload();
		}
	}

	/**
	 * deletes the vao (if non-null) and loads a new one with new generated data
	 */
	public void reload() {
		TextMeshData tmd = font.loadText(this);
		modelLength = tmd.modelLength();
		modelHeight = tmd.modelHeight();
		if (this.vao != null)
			this.vao.delete();
		this.vao = Loader.loadToVAO(new int[] { 3, 2 }, tmd.getVertexPositions(), tmd.getTextureCoords());
	}

	public String getTextString() {
		return content;
	}

	public void setNumberOfLines(int l) {
		this.lines = l;
	}

	public boolean isCentered() {
		return center;
	}

	public void center(boolean b) {
		center = b;
	}

	public float getFontSize() {
		return fontSize;
	}

	public float getMaxLineSize() {
		return maxLineSize;
	}

	public float scaledWidth() {
		return scale.x * modelLength;
	}

	public float width() {
		return modelLength;
	}

	public float scaledHeight() {
		return scale.y * modelHeight;
	}

	public float height() {
		return modelHeight;
	}

	public Font getFont() {
		return font;
	}

	@Override
	public String toString() {
		return "Text '" + content + "'";
	}

	private Matrix4f translateToCenter(Matrix4f m) {
		if (center)
			m.translate(-scaledWidth() * 0.5f, scaledHeight() * 0.5f, 0);
		return m;
	}

	@Override
	public Matrix4f transform(Matrix4f m) {
		if (parent == null)
			return scale(translateToCenter(rotate(translate(m))));
		else// the best!
			return scale(translateToCenter(rotate(translate(parent.parentTransform(m)))));
	}
	
	public void delete() {
		this.vao.delete();
	}

	
}
