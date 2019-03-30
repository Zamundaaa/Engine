package genericRendering.lights;

import java.util.List;

import org.joml.Vector3f;

import generic.Thing;
import graphics3D.Camera;
import tools.Meth;
import tools.misc.Vects;

public class PointLight extends Light {

	public PointLight(float brightness) {
		super(brightness);
	}

	public PointLight(float r, float g, float b) {
		super(r, g, b);
	}

	public PointLight(float x, float y, float z, float r, float g, float b) {
		super(x, y, z, r, g, b);
	}

	public PointLight(float x, float y, float z, float brightness) {
		super(x, y, z, brightness);
	}
	
	public PointLight(PointLight toClone) {
		super(toClone);
	}
	
	private static final float minLightContribution = 0.02f;

	public static int getMostRelevantPointLights(Camera c, Thing t, List<Light> lights, List<Light> dest,
			int MAX_LIGHTS) {
		dest.clear();
		if (t == null)
			return 0;
		int count = 0;
		Vector3f v = Vects.calcVect();
		for (int i = 0; i < lights.size(); i++) {
			Light p = lights.get(i);
			p.updateAbsolutePosition();
			float r = Meth.sqrt(0.25f * (t.scaledDepth() * t.scaledDepth() + t.scaledHeight() * t.scaledHeight()
					+ t.scaledWidth() * t.scaledWidth()));
			// it doesn't work properly for Terrains somehow?!?
			t.transformedMiddle(v).add(t.absolutePosition());
			float dsq = p.absolutePosition().distance(v) - r;
			if (dsq < 0)
				dsq = 0;
			dsq *= dsq;
			p.contribution = (p.r() + p.g() + p.b()) / (1 + dsq);
			// minimum brightness for the light to actually change anything.
			// filters out most of potential light sources and thus increases performance by
			// a **lot**
			// TODO reduce this minimum for big objects (not too much, but perhaps so that
			// terrain looks acceptable...)
			if (p.contribution < minLightContribution) {
				p.contribution = 0;
			} else {
				count++;
			}
		}
		if (count < MAX_LIGHTS) {
			for (int i = 0; i < lights.size(); i++) {
				Light p = lights.get(i);
				if (p.contribution > 0) {
					dest.add(p);
				}
			}
		} else if (count > 0) {
			while (dest.size() < MAX_LIGHTS && (lights.size() - dest.size()) > MAX_LIGHTS) {
				Light nearest = null;
				float max_cont = 0;
				for (int i = 0; i < lights.size(); i++) {
					Light p = lights.get(i);
					if (p instanceof PointLight && !dest.contains(p)) {
						if (p.contribution > max_cont) {
							max_cont = p.contribution;
							nearest = p;
						}
					}
				}
				if (nearest == null) {
					break;
				}
				dest.add(nearest);
			}
		}
		return dest.size();
	}
	
	@Override
	public PointLight clone() {
		return new PointLight(this);
	}
	
}
