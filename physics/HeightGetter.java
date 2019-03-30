package physics;

import generic.Thing;

public interface HeightGetter<T extends Thing> {
	
	public float height(T t);
	
}
