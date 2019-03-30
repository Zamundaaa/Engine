package generic;

import org.joml.Vector3f;

import models.StaticModel;
import models.material.Material;
import openGlResources.buffers.VAO;

public class ModeledThing extends Thing {

	protected StaticModel model;

	public ModeledThing() {
		super();
	}

	public ModeledThing(Vector3f position) {
		super(position);
	}

	public void attachModel(VAO v, Material m, float scale) {
		this.model = new StaticModel(v, m, position, scale);
	}

	public void attachModel(StaticModel m) {
		setModel(m);
		if (model != null)
			this.model.setPositionRef(position);
	}

	/**
	 * @return this Things model. May be null!
	 */
	public StaticModel model() {
		return model;
	}
	
	public void setModel(StaticModel m) {
		this.model = m;
	}
	
}
