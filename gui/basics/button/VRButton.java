package gui.basics.button;

import org.joml.Vector3f;

import genericRendering.MasterRenderer;
import models.material.Material;
import openGlResources.buffers.VAO;

/**
 * mind you: not even close to working
 * 
 * @author xaver
 *
 */
public class VRButton extends Button {

	public VRButton(VAO vao, Material material, Vector3f position, float scale) {
		super(vao, material, position, scale);
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
		return false;
	}

	@Override
	public boolean updateServer(float frameTimeSeconds) {
//		if (VRHandler.player != null) {
//			VRController c = VRHandler.player.left();
//		}
		return false;
	}

}
