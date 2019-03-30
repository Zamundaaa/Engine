package graphics3D.proceduralSkybox;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import genericRendering.lights.DirectionalLight;
import genericRendering.lights.Light;
import tools.Meth;
import tools.misc.Vects;

public abstract class SunKeeper {

	public static DirectionalLight sun;
	private static final ArrayList<Light> list = new ArrayList<>();
	static {
		sun = new DirectionalLight(0, -1, 0, 0.5f, 0.5f, 0.5f);
		list.add(sun);
	}

	public static void updateClient() {
		timeUpdate();
	}

	public static void updateServer() {

	}

	public static float dayColor = 0.7f, nightColor = 0.075f;
	public static final Vector3f middle = new Vector3f();
	public static final float morningstart = 5, morning = 7, eveningstart = 17.75f, night = 19f;
//	private static float dayr = 0.4444f, dayg = 0.52f, dayb = 0.59f;
//	private static float nightr = 0.03f, nightg = 0.03f, nightb = 0.03f;

	private static void timeUpdate() {
		float time = SkyRenderer.getTime();
		if ((time >= night || time >= 0) && time < morningstart) {
			sun.setColor(nightColor, nightColor, nightColor);
		} else if (time >= morningstart && time < morning) {
			float fact = (time - morningstart) / (morning - morningstart);
			sun.setColor(((fact * dayColor) > nightColor) ? fact * dayColor : nightColor,
					(fact * dayColor > nightColor) ? fact * dayColor : nightColor,
					(fact * dayColor > nightColor) ? fact * dayColor : nightColor);
		} else if (time >= morning && time < eveningstart) {
			sun.setColor(dayColor, dayColor, dayColor);
		} else {
			float fact = (time - eveningstart) / (night - eveningstart);
			fact = 1 - fact;
			sun.setColor(((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor,
					((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor,
					((fact * dayColor) > nightColor) ? (fact * dayColor) : nightColor);
		}
		float cI = time / 24;
//		sun.position().set(-(float) Meth.sin(cI * 2 * Meth.PI) * SUNDIST + middle.x,
//				-Meth.cos(cI * 2 * Meth.PI) * SUNDIST + middle.y, zoffset * SUNDIST + middle.z);
		sun.direction().set(Meth.sin(cI * 2 * Meth.PI), Meth.cos(cI * 2 * Meth.PI), zoffset).normalize();

		getSunDirection(Vects.calcVect, SkyRenderer.getTime());
		float reddot = Vects.calcVect.dot(horizon);
		float redcap = 0.95f;
		if (reddot > redcap) {
			Vects.blend(sun.color(), red, sun.color(), Meth.clamp((1 - (100 * (1 - reddot))) * 0.2f, 0, 0.1f));
		} else {
			reddot = Vects.calcVect.dot(horizon2);
			if (reddot > redcap) {
				Vects.blend(sun.color(), red, sun.color(), Meth.clamp((1 - (100 * (1 - reddot))) * 0.2f, 0, 0.1f));
			}
		}

	}

	private static final float yredHorizonOffset = 0;
	private static final Vector3f red = new Vector3f(1, 0.2f, 0.2f),
			horizon = new Vector3f(1, yredHorizonOffset, 0).normalize(),
			horizon2 = new Vector3f(-1, yredHorizonOffset, 0);
	public static final float SUNDIST = 200000, zoffset = 0;

	public static Vector3f getSunDirection(Vector3f setVect, float time) {
		float cI = time / 24;
		setVect.set((float) -Meth.sin(cI * 2 * Meth.PI), (float) -Meth.cos(cI * 2 * Meth.PI), zoffset);
		setVect.normalize();
		if (setVect.y < -0.1f)
			setVect.negate();
		return setVect;
	}

	public static void cleanUp() {
		list.remove(sun);
		sun = null;

	}

	public static float getSunAngle(float time) {
		return -(time / 24) * 2 * Meth.PI;
	}

	public static List<Light> getLightsToRender() {
		return list;
	}

}
