package generic;

import genericRendering.MasterRenderer;

public interface UpdatedThing {

	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr);

	public boolean updateServer(float frameTimeSeconds);

}
