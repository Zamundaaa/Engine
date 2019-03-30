package gui.basics.button;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import generic.Thing;
import models.material.Material;
import openGlResources.buffers.VAO;

public abstract class Button extends Thing {

	protected List<ButtonListener> bs = new ArrayList<>();
	protected boolean isSwitch = false;
	protected boolean state = false;

	public Button(VAO vao, Material material, Vector3f position, float scale) {
		super(vao, material, position, scale);
	}

	public Button(VAO vao, Material material, Vector3f position) {
		super(vao, material, position, 1);
	}

	public Button(VAO vao, Material material, Vector3f position, float scale, ButtonListener b) {
		super(vao, material, position, scale);
		this.bs.add(b);
	}

	/**
	 * if this button should stay pressed if let go. default: false
	 */
	public void setSwitch(boolean s) {
		this.isSwitch = s;
	}

	/**
	 * do mind that this does NOT fire events!
	 */
	public void setState(boolean b) {
		this.state = b;
	}

	public void eventOcurred(ButtonEvent e) {
		if (e == ButtonEvent.press || e == ButtonEvent.alternatepress)
			state = true;
		else if (!isSwitch && (e == ButtonEvent.release || e == ButtonEvent.alternateRelease))
			state = false;
		for (int i = 0; i < bs.size(); i++)
			bs.get(i).eventOccurred(e);
	}

	public void addListener(ButtonListener b) {
		this.bs.add(b);
	}

	public void removeListener(ButtonListener b) {
		this.bs.remove(b);
	}

}
