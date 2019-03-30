package gui;

import java.util.ArrayList;

import collectionsStuff.ArrayListBool;
import generic.Thing;
import genericRendering.Rectangle;
import models.Asset;
import models.AssetHolder;
import models.material.Material;

public class ListingPanel extends AssetHolder {

	protected ArrayList<Thing> allModels = new ArrayList<>();
	protected Rectangle background;
	// TODO priority list!
	protected ArrayListBool toArrange = new ArrayListBool();
	protected float spacing = 0.02f;

	public ListingPanel(float x, float y, float width, float height, Material backgroundMat) {
		this(x, y, 0, width, height, backgroundMat);
	}

	public ListingPanel(float x, float y, float z, float width, float height, Material backgroundMat) {
		background = new Rectangle(x, y, z, width, height, backgroundMat);
		if (backgroundMat != null)
			add(background);
	}

	@Override
	public void add(Asset m) {
		super.add(m);
		recAdd(m);
	}

	@Override
	public void remove(Asset m) {
		super.remove(m);
		recRem(m);
	}

//	@Override
//	public void add(AssetHolder m) {
//		super.add(m);
//		recAdd(m);
//	}
//
//	@Override
//	public void remove(AssetHolder m) {
//		super.remove(m);
//		recRem(m);
//	}

	private void recAdd(Asset a) {
		if (a instanceof Thing) {
			Thing m = (Thing) a;
			m.setParent(background);
			allModels.add(m);
		}
	}

	private void recRem(Asset a) {
		if (a instanceof Thing) {
			Thing m = (Thing) a;
			m.setParent(null);
			allModels.remove(m);
		}
	}

//	private void recAdd(AssetHolder a) {
//		if (a.assets() != null)
//			for (int i = 0; i < a.assets().size(); i++)
//				recAdd(a.assets().get(i));
//		if (a.children() != null)
//			for (int i = 0; i < a.children().size(); i++)
//				recAdd(a.children().get(i));
//	}
//
//	private void recRem(AssetHolder a) {
//		if (a.assets() != null)
//			for (int i = 0; i < a.assets().size(); i++)
//				recRem(a.assets().get(i));
//		if (a.children() != null)
//			for (int i = 0; i < a.children().size(); i++)
//				recRem(a.children().get(i));
//	}

	public void setParent(Thing t) {
		background.setParent(t);
	}

	/**
	 * @param direction -2 for right to left, -1 for up to down, 1 for down to up
	 *                  and 2 for left to right
	 */
	public void arrange(int direction) {
		float cursorX = direction == 2 ? -background.scaledWidth() * 0.5f + spacing
				: (direction == -2 ? background.scaledWidth() * 0.5f - spacing : 0);
		float cursorY = direction == 1 ? -background.scaledHeight() * 0.5f + spacing
				: (direction == -1 ? background.scaledHeight() * 0.5f - spacing : 0);
		float stepMulX = direction == -2 ? -0.5f : (direction == 2 ? 0.5f : 0);
		float stepMulY = direction == -1 ? -0.5f : (direction == 1 ? 0.5f : 0);
		float spacing = this.spacing * 2;
		for (int i = background.material() == null ? 0 : 1; i < allModels.size(); i++) {
			Thing m = allModels.get(i);
			if (!m.shown() || m.material() == null)
				continue;
			cursorX += m.scaledWidth() * stepMulX;
			cursorY += m.scaledHeight() * stepMulY;
			m.position().set(cursorX, cursorY, 0);
			cursorX += m.scaledWidth() * stepMulX;
			cursorY += m.scaledHeight() * stepMulY;
			cursorX += spacing * stepMulX;
			cursorY += spacing * stepMulY;
		}
	}

}
