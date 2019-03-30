package models;

import generic.Thing;
import generic.UpdatedThing;

public interface Asset extends AssetHolderI, UpdatedThing {

	public void drawMode(int d);

	public void shown(boolean b);

	public void setParent(Thing t);

	public void add(Asset a);

	public void remove(Asset a);

	public Asset clone();

}
