package loaders.fontMeshCreator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import loaders.TextureCache;
import models.text.Text;
import openGlResources.textures.Texture;
import tools.AppFolder;

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font. Also stores
 * Fonts by name and a default Font (static initializer loads ubuntuCondensed as
 * the default font) that you can of course set yourself, too
 * 
 * @author Xaver, parts taken from ThinMatrix
 *
 */
public class Font {

	public final String name;
	private Texture textureAtlas;
	private TextMeshCreator loader;

	/**
	 * Creates a new font and loads up the data about each character from the font
	 * file.
	 * 
	 * @param textureAtlas - the ID of the font atlas texture.
	 * @param fontFile     - the font file containing information about each
	 *                     character in the texture atlas.
	 */
	public Font(String name, InputStream fontFileStream, Texture textureAtlas) {
		this.name = name;
		this.textureAtlas = textureAtlas;
		this.loader = new TextMeshCreator(fontFileStream);
		fonts.put(name, this);
	}

	/**
	 * @return The font texture atlas.
	 */
	public Texture getTextureAtlas() {
		return textureAtlas;
	}

	/**
	 * Takes in an unloaded text and calculate all of the vertices for the quads on
	 * which this text will be rendered. The vertex positions and texture coords and
	 * calculated based on the information from the font file.
	 * 
	 * @param text - the unloaded text.
	 * @return Information about the vertices of all the quads.
	 */
	public TextMeshData loadText(Text text) {
		return loader.createTextMesh(text);
	}

	public void cleanUp() {
		textureAtlas.delete();
		fonts.remove(name);
	}

	public static Font defaultFont;

	private static Map<String, Font> fonts = new HashMap<>();

	public static Font loadFont(String name, String fontPath, String textureAtlasPath) {
		Font ret = get(name);
		if (ret != null)
			return ret;
		try {
			InputStream inputstream = AppFolder.getInputStream(fontPath);
			if (inputstream == null)
				AppFolder.log.println("no font file at " + fontPath);
			Texture atlas = TextureCache.get().getTexture(textureAtlasPath);
			if (atlas == null)
				AppFolder.log.println("no atlas texture at " + textureAtlasPath);
			ret = new Font(name, inputstream, atlas);
			if (defaultFont == null)
				defaultFont = ret;
			return ret;
		} catch (FileNotFoundException f) {
			f.printStackTrace(AppFolder.log);
			return null;
		}
	}

	public static Font loadFont(String name, String fontPath, Texture textureAtlas) {
		Font ret = get(name);
		if (ret != null)
			return ret;
		try {
			ret = new Font(name, AppFolder.getInputStream(fontPath), textureAtlas);
			if (defaultFont == null)
				defaultFont = ret;
			return ret;
		} catch (FileNotFoundException f) {
			f.printStackTrace(AppFolder.log);
			return null;
		}
	}

	public static Font get(String name) {
		return fonts.get(name);
	}

	/**
	 * empty. Triggers the static initializer, if you want to do that explicitly
	 */
	public static void initialize() {

	}

	static {
		AppFolder.log.print("initializing Font!");
		loadFont("ubuntuCondensed", "res/fonts/ubuntuCondensed.fnt", "res/fonts/ubuntuCondensed.png");
//		loadFont("candara", "res/fonts/candara.fnt", "res/fonts/candara.png");
	}

}
