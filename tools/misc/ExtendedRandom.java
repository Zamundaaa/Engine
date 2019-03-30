package tools.misc;

import java.util.Random;

import tools.Meth;

public class ExtendedRandom {
	
	protected Random r;
	
	public ExtendedRandom() {
		r = new Random();
	}
	
	public float get(long seed, float min, float max) {
		setSeed(seed);
		return Meth.randomFloat(r, min, max, seed);
	}
	
	public float get(float min, float max) {
		return Meth.randomFloat(r, min, max);
	}
	
	public void setSeed(long s) {
		r.setSeed(s);
	}
	
}
