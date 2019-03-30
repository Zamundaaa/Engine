package models.characters;

import org.joml.Vector3f;

import generic.Thing;
import tools.misc.Vects;
import tools.misc.interfaces.FloatInterface;

public class CharacterController<T extends Thing & GameCharacter> {

	protected FloatInterface forward, turn;
	protected T c;
//	protected float speed, turnspeed;

	public CharacterController(FloatInterface forward, FloatInterface turn) {
		super();
		this.forward = forward;
		this.turn = turn;
	}

	public CharacterController(FloatInterface walkingspeed, FloatInterface turn, T c) {
		this.forward = walkingspeed;
		this.turn = turn;
		this.c = c;
	}

	public void setGameCharacter(T c) {
		this.c = c;
	}

	public boolean update(float frameTimeSeconds) {
		boolean ret = false;
		if (c != null) {
			float f = forward.getFloat() * frameTimeSeconds, t = turn.getFloat() * frameTimeSeconds;
			if (f != 0) {
				Vector3f v = Vects.calcVect().set(0, 0, f);
				c.rotation().transform(v);
//				AppFolder.log.println(Meth.coordinatesToString(v));
				c.position().add(v);
				ret = true;
			}
			if (t != 0)
				c.rotation().rotateLocalY(t);
		}
		return ret;
	}

	public CharacterController<T> clone() {
		return new CharacterController<>(forward, turn);
	}

}
