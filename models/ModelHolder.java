package models;

import java.util.ArrayList;
import java.util.List;

public class ModelHolder {

	protected List<ModelHolder> children;
	protected List<Model> models;
	protected boolean shown = false;

	public void shown(boolean b) {
		shown = b;
		if (children != null)
			for (int i = 0; i < children.size(); i++)
				children.get(i).shown(b);

		if (models != null)
			for (int i = 0; i < models.size(); i++)
				models.get(i).shown(b);
	}

	public boolean shown() {
		return shown;
	}

	public void drawMode(int d) {
		if (children != null)
			for (int i = 0; i < children.size(); i++)
				children.get(i).drawMode(d);
		if (models != null)
			for (int i = 0; i < models.size(); i++)
				models.get(i).drawMode(d);
	}

	public List<ModelHolder> children() {
		return children;
	}

	public List<Model> models() {
		return models;
	}

	public void add(Model m) {
		if (models == null)
			models = new ArrayList<>();
		models.add(m);
	}

	public void add(ModelHolder m) {
		if (children == null)
			children = new ArrayList<>();
		children.add(m);
	}

	public void add(Model... models) {
		if (models == null)
			return;
		if (this.models == null)
			this.models = new ArrayList<>();
		for (int i = 0; i < models.length; i++)
			this.models.add(models[i]);
	}

	public void add(ModelHolder... mhs) {
		if (mhs == null)
			return;
		if (children == null)
			children = new ArrayList<>();
		for (int i = 0; i < mhs.length; i++)
			this.children.add(mhs[i]);
	}

	public void remove(Model m) {
		models.remove(m);
	}

	public void remove(ModelHolder m) {
		children.remove(m);
	}

	public void remove(Model... models) {
		if (models == null)
			return;
		for (int i = 0; i < models.length; i++)
			this.models.remove(models[i]);
	}

	public void remove(ModelHolder... mhs) {
		if (mhs == null)
			return;
		for (int i = 0; i < mhs.length; i++)
			this.children.remove(mhs[i]);
	}

}
