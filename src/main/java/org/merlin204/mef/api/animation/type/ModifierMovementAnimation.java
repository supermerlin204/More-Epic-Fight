package org.merlin204.mef.api.animation.type;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ModifierMovementAnimation extends MovementAnimation {

    private final float speed;


    public ModifierMovementAnimation(boolean isRepeat, AnimationManager.AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature, float speed) {
        super(isRepeat, accessor, armature);
        this.speed = speed;
    }

    public ModifierMovementAnimation(float transitionTime, boolean isRepeat, AnimationManager.AnimationAccessor<? extends MovementAnimation> accessor, AssetAccessor<? extends Armature> armature, float speed) {
        super(transitionTime, isRepeat, accessor, armature);
        this.speed = speed;
    }

    public ModifierMovementAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature, float speed) {
        super(transitionTime, isRepeat, path, armature);
        this.speed = speed;
    }

    @Override
    public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        if (animation.isLinkAnimation()) {
            return 1.0F;
        } else {
            float movementSpeed = 1.0F;
            if (Math.abs((entitypatch.getOriginal()).walkAnimation.speed() - (entitypatch.getOriginal()).walkAnimation.speed(1.0F)) < 0.007F) {
                movementSpeed *= (entitypatch.getOriginal()).walkAnimation.speed() * this.speed;
            }

            return movementSpeed;
        }
    }




}
