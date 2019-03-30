package models;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import generic.Thing;

/**
 * 
 * @author xaver
 *
 */
public class AssetHolder extends Thing implements Asset {

	protected List<Asset> assets;
	protected boolean shown = false;

	public AssetHolder(float x, float y, float z) {
		this(new Vector3f(x, y, z));
	}

	public AssetHolder(Vector3f pos) {
		super(pos);
	}

	public AssetHolder() {

	}

	public void shown(boolean b) {
		shown = b;
//		if (children != null)
//			for (int i = 0; i < children.size(); i++)
//				children.get(i).shown(b);

		if (assets != null)
			for (int i = 0; i < assets.size(); i++)
				assets.get(i).shown(b);
	}

	public boolean shown() {
		return shown;
	}

	public void drawMode(int d) {
//		if (children != null)
//			for (int i = 0; i < children.size(); i++)
//				children.get(i).drawMode(d);
		if (assets != null)
			for (int i = 0; i < assets.size(); i++)
				assets.get(i).drawMode(d);
	}

	public List<Asset> assets() {
		return assets;
	}

	public void add(Asset m) {
		if (assets == null)
			assets = new ArrayList<>();
		assets.add(m);
	}

	public void addChild(Asset a) {
		add(a);
		a.setParent(this);
	}

	public void add(Asset... models) {
		if (models == null)
			return;
		if (this.assets == null)
			this.assets = new ArrayList<>();
		for (int i = 0; i < models.length; i++)
			this.assets.add(models[i]);
	}

	public void remove(Asset m) {
		assets.remove(m);
	}

	public void remove(Asset... models) {
		if (models == null)
			return;
		for (int i = 0; i < models.length; i++)
			this.assets.remove(models[i]);
	}

}
