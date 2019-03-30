package openGlResources.textures;

import java.nio.ByteBuffer;

public class TextureData {

	private int width, height;
	private ByteBuffer buffer;
	private boolean transparent, translucent;

	public TextureData(int width, int height, ByteBuffer buffer) {
		this.width = width;
		this.height = height;
		this.buffer = buffer;
	}

	public TextureData(int width, int height, ByteBuffer buffer, boolean transparent, boolean translucent) {
		this(width, height, buffer);
		this.transparent = transparent;
		this.translucent = translucent;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isTranslucent() {
		return translucent;
	}

	public void setTranslucent(boolean translucent) {
		this.translucent = translucent;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}
}
