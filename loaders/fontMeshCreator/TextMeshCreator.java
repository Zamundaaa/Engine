package loaders.fontMeshCreator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import collectionsStuff.ArrayListFloat;
import models.text.Text;

public class TextMeshCreator {

	public static final double LINE_HEIGHT = 0.03f, tablength = 5;
	protected static final int SPACE_ASCII = 32;
	protected static final char linebreak = '\n', tab = '\t';

	private MetaFile metaData;
	private float currentWidth, currentMaxHeight;

	protected TextMeshCreator(InputStream metaFileStream) {
		metaData = new MetaFile(metaFileStream);
	}

	protected TextMeshData createTextMesh(Text text) {
		List<Line> lines = createStructure(text);
		return createQuadVertices(text, lines);
	}

	private List<Line> createStructure(Text text) {
		ArrayList<java.lang.Character> chars = new ArrayList<>();
		for (int i = 0; i < text.getTextString().length(); i++) {
			chars.add(text.getTextString().charAt(i));
		}
		List<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());
		outer: for (int i = 0; i < chars.size(); i++) {
			char c = chars.get(i);
			int ascii = c;
			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(text.getFontSize());
				continue;
			} else if (ascii == linebreak) {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}
				lines.add(currentLine);
				currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
				currentWord = new Word(text.getFontSize());
				continue;
			} else if (ascii == tab) {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				} else {
					currentWord = new Word(text.getFontSize());
					Character space = metaData.getCharacter(' ');
					double goal = 0;
					while (goal <= currentLine.getLineLength() + currentWord.getWordWidth())
						goal += tablength * space.getSizeX();
					for (int i2 = 0; i2 < tablength
							&& currentLine.getLineLength() + currentWord.getWordWidth() < goal; i2++) {
						currentWord.addCharacter(space);
						boolean fits = currentLine.wordFits(currentWord);
						if (!fits) {
							lines.add(currentLine);
							currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
							// currentLine.attemptToAddWord(currentWord);
							currentWord = new Word(text.getFontSize());
							continue outer;
						} else if (i2 == tablength - 1) {
							lines.add(currentLine);
							currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
							// currentLine.attemptToAddWord(currentWord);
							currentWord = new Word(text.getFontSize());
							continue outer;
						}
					}
				}
			}
			Character character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, Text text) {
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(Text text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double cursorX = 0f;
		double cursorY = 0f;
		ArrayListFloat vertices = new ArrayListFloat();
		ArrayListFloat textureCoords = new ArrayListFloat();
		for (Line line : lines) {
//			if (text.isCentered()) {
//				cursorX = startX = (line.getMaxLength() - line.getLineLength()) / 2;
//			}

			if (line.getLineLength() > currentWidth) {
				currentWidth = (float) (line.getLineLength());
			}

			for (int w = 0; w < line.getWords().size(); w++) {
				Word word = line.getWords().get(w);
				for (int i = 0; i < word.getCharacters().size(); i++) {
					Character letter = word.getCharacters().get(i);
					addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize(), vertices);
					addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					if (i < word.getCharacters().size() - 1)
						cursorX += letter.getxAdvance() * text.getFontSize();
				}
				if (w < line.getWords().size() - 1)
					cursorX += metaData.getSpaceWidth() * text.getFontSize() * 3.25f;
			}
			cursorX = 0;
			cursorY += LINE_HEIGHT * text.getFontSize();
		}
		TextMeshData ret = new TextMeshData(vertices.capToArray(), textureCoords.capToArray(), currentWidth,
				currentMaxHeight);
		currentWidth = 0;
		currentMaxHeight = 0;
		return ret;
	}

	private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize,
			ArrayListFloat vertices) {
		double x = cursorX + (character.getxOffset() * fontSize);
		double y = cursorY + (character.getyOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y + (character.getSizeY() * fontSize);
//		double properX = (2 * x) - 1;
//		double properY = (-2 * y) + 1;
//		double properMaxX = (2 * maxX) - 1;
//		double properMaxY = (-2 * maxY) + 1;
//		addVertices(vertices, properX, properY, properMaxX, properMaxY);
		addVertices(vertices, x, y, maxX, maxY);
		if ((float) maxY > currentMaxHeight) {
			currentMaxHeight = (float) maxY;
		}
	}

	private static void addVertices(ArrayListFloat vertices, double x, double y, double maxX, double maxY) {
		vertices.add(x);
		vertices.add(-y);
		vertices.add(0f);
		vertices.add(x);
		vertices.add(-maxY);
		vertices.add(0f);
		vertices.add(maxX);
		vertices.add(-maxY);
		vertices.add(0f);
		vertices.add(maxX);
		vertices.add(-maxY);
		vertices.add(0f);
		vertices.add(maxX);
		vertices.add(-y);
		vertices.add(0f);
		vertices.add(x);
		vertices.add(-y);
		vertices.add(0f);
	}

	private static void addTexCoords(ArrayListFloat texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add(x);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(y);
		// flipped:
//		texCoords.add(x);
//		texCoords.add(maxY);
//		texCoords.add(x);
//		texCoords.add(y);
//		texCoords.add(maxX);
//		texCoords.add(y);
//		texCoords.add(maxX);
//		texCoords.add(y);
//		texCoords.add(maxX);
//		texCoords.add(maxY);
//		texCoords.add(x);
//		texCoords.add(maxY);
	}

}
