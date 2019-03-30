package generic.characters;

import generic.Thing;
import generic.UpdatedThing;
import genericRendering.MasterRenderer;
import models.animated.AnimatedModelData;
import models.animated.Animation;
import models.components.AnimationComponent;
import models.material.Material;
import openGlResources.buffers.VAO;
import physics.HeightGetter;

public class AnimatedCharacter extends Thing implements GameCharacter, UpdatedThing {

	// TODO you need to kind of switch to modules instead of this mess. A GameItem /
	// Model / Whatever (most likely: Model) would then just have a list of modules
	// that define its behaviour. There'd be a physics module, an animation module
	// (ka how I would do that exactly but would prob work). When checking for
	// rendering then not only the model itself but also its modules have to be
	// checked for priority. For example: StaticRenderer (or: everything that can't
	// animate...) - AnimationModule -> -100 prio. Collision modules would then be
	// thrown into a system of quadtrees or something.
	// also some modules would probably have to have a special place / own variable
	// for performance & ease of use. For example physics module would be good to
	// have easily re-configurable

	protected CharacterController<AnimatedCharacter> ctrl;
	protected HeightGetter<AnimatedCharacter> hg;

	public AnimatedCharacter(AnimatedModelData data, Material mat, CharacterController<AnimatedCharacter> ctrl) {
		this(data.getVAO(), mat, ctrl, new AnimationComponent(data));
	}

	public AnimatedCharacter(VAO vao, Material mat, CharacterController<AnimatedCharacter> ctrl,
			AnimationComponent anim) {
		super(vao, mat);
		this.animation = anim;
		this.ctrl = ctrl;
		ctrl.setGameCharacter(this);
	}

	public void setHeightGetter(HeightGetter<AnimatedCharacter> hg) {
		this.hg = hg;
	}

	@Override
	public boolean updateClient(float frameTimeSeconds, MasterRenderer mr) {
		return false;
	}

	@Override
	public boolean updateServer(float frameTimeSeconds) {
		if (hg != null)
			position.y = hg.height(this);
		if (ctrl != null) {
			if (ctrl.update(frameTimeSeconds)) {
				if (animation.currentAnimation() == null && animation.getAnimations().size() > 0) {
					animation.selectAnimation(animation.getAnimations().get(0), 1);
				}
			} else {
				animation.selectAnimation((Animation) null, 1);
			}
		}
		return false;
	}

	public void destruct() {

	}

	@Override
	public AnimatedCharacter clone() {
		return new AnimatedCharacter(this.vao, this.material, this.ctrl, new AnimationComponent(this.animation));
	}

}
