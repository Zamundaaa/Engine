package tools.raytracing;

import static tools.Meth.*;

import org.joml.Vector3f;

import tools.AppFolder;
import tools.Meth;

/**
 * you can instantiate this class if you want to have a convenience method with
 * an internal Vector3f for return values - but you don't have to. The actual
 * raytracing is just the static method
 * 
 * @author xaver
 *
 */
public class GenericRaytracer {

	public static final int XP = 0, XM = 1, YP = 2, YM = 3, ZP = 4, ZM = 5;

	// TODO copy all of this over to "IntegerRaytracer" that can only operate on an
	// integer grid & change this so it can operate with arbitrary step size (not
	// using integers at all). sxyz would still remain the same, just multiplied
	// with stepsize when adding. XYZ would have to be calculated differently in the
	// beginning but it would basically stay the same.

	public static interface GoalDescriptorF {
		public boolean fitsDescription(float x, float y, float z);
	}

	protected Vector3f retVect = new Vector3f();

	/**
	 * tries to find the first coordinates from start on, along ray, that fit the
	 * description of goal.
	 * 
	 * @return a vector containing the result, or null if no result has been found
	 *         <p>
	 *         will always return the same vector object from this raytracer
	 *         instance. So this is just a shortcut for small calculations. Don't
	 *         use this for anything else!
	 */
	public Vector3f find(Vector3f start, Vector3f ray, float stepsize, float distance, GoalDescriptorF goal) {
		int dir = find(start, ray, stepsize, distance, goal, retVect);
		if (dir == -1)
			return null;
		else
			return retVect;
	}

	/**
	 * tries to find the first coordinates from start on, along ray, that fit the
	 * description of goal
	 * 
	 * @param dest If there's a result found then it will be put into this vector.
	 *             If there is none it will not be touched.
	 * @param ray  has to be normalized (length 1)
	 * @return -1 if nothing has been found or the side the ray entered the
	 *         destination as defined by {@link GenericRaytracer#XP},
	 *         {@link GenericRaytracer#XM} etc
	 */
	public static int find(Vector3f start, Vector3f ray, float stepsize, float distance, GoalDescriptorF goal,
			Vector3f dest) {
		return find(start.x, start.y, start.z, ray.x, ray.y, ray.z, stepsize, distance, goal, dest);
	}

	/**
	 * tries to find the first coordinates from start on, along ray, that fit the
	 * description of goal
	 * 
	 * @param dest If there's a result found then it will be put into this vector.
	 *             If there is none it will not be touched.
	 * @return -1 if nothing has been found or the side the ray entered the
	 *         destination as defined by {@link GenericRaytracer#XP},
	 *         {@link GenericRaytracer#XM} etc
	 */
	public static int find(float startx, float starty, float startz, float rx, float ry, float rz, float stepsize,
			float distance, GoalDescriptorF goal, Vector3f dest) {
		if (abs(rx) < 0.00001f)
			rx = 0;
		if (abs(ry) < 0.00001f)
			ry = 0;
		if (abs(rz) < 0.00001f)
			ry = 0;

		if (rx == ry && ry == rz && rz == 0) {
			return -1;
		}

//		if(goal.fitsDescription(startx, starty, startz)) {
//			// TODO ?
//		}
		float l = Meth.sqrt(rx * rx + ry * ry + rz * rz);

		float x = startx, y = starty, z = startz;
//		rx /= l;
//		ry /= l;
//		rz /= l;
		float dx = Float.POSITIVE_INFINITY, dy = Float.POSITIVE_INFINITY, dz = Float.POSITIVE_INFINITY;
		int xdir = sign(rx) == 1 ? XP : XM, ydir = sign(ry) == 1 ? YP : YM, zdir = sign(rz) == 1 ? ZP : ZM;
		float d = 0, step = 0;
		int lastDir = -1;
		do {
			// all guaranteed to be >= 0 !
			dx = calcD(x, stepsize, rx);
			dy = calcD(y, stepsize, ry);
			dz = calcD(z, stepsize, rz);
			if (dx < dy) {
				if (dx < dz) {
					lastDir = xdir;
					step = dx;
				} else {
					lastDir = zdir;
					step = dz;
				}
			} else if (dz < dy) {
				lastDir = zdir;
				step = dz;
			} else {
				lastDir = ydir;
				step = dy;
			}
			x += step * rx;
			y += step * ry;
			z += step * rz;
			if (goal.fitsDescription(x, y, z)) {
				if (dest != null)
					dest.set(x, y, z);
				return lastDir;
			}
			d += l * step;
			if (step == 0) {
				AppFolder.log.println("step is " + step + "?!? " + lastDir);
			}
		} while (d < distance);
		return -1;
	}

	/**
	 * guaranteed positive output. POSITIVE_INFINITY if ray == 0
	 */
	private static float calcD(float coordinate, float stepsize, float ray) {
		if (ray == 0)
			return Float.POSITIVE_INFINITY;
		float ret;
		if (ray < 0)
			ret = ceil(coordinate / stepsize) - 1;
		else
			ret = floor(coordinate / stepsize) + 1;
		ret = (ret * stepsize - coordinate) / ray;
		if(abs(ret) < 0.000001f)
			ret = Float.POSITIVE_INFINITY;
		return ret;
	}

}
