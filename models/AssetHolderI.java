package models;

import java.util.List;

public interface AssetHolderI {

	public void shown(boolean b);

	public boolean shown();
//
//	public void drawMode(int d);

	public List<Asset> assets();
}
