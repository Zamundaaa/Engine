package openGlResources.shaders.uniforms.lights;

import genericRendering.lights.Light;
import tools.misc.Vects;

public class PointLightUniform extends PositionalLightUniform {

	public PointLightUniform(String name) {
		super(name);
	}

//	public void set(PointLight p) {
//		if(p == null) {
//			
//		}
//		if (p.parent() != null)
//			p.createTranslationMatrix(Vects.mat4);
//		super.set(p.absolutePosition(), p.color());
//	}

	/**
	 * @param p assumes a PointLight. Is used to avoid unnecessary casting
	 */
	public void set(Light p) {
		if (p == null) {
			super.set(Vects.NULL, Vects.NULL);
			return;
		}
		if (p.parent() != null) {
			p.createTransformationMatrix(Vects.mat4);
			super.set(p.absolutePosition(), p.color());
		} else {
			super.set(p.position(), p.color());
		}
	}

}
