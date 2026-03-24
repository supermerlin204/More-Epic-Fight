package org.merlin204.mef.entity;

import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.damagesource.StunType;

public class DummyPlayerEntityPatch extends HumanoidMobPatch<DummyPlayerEntity> {

    public DummyPlayerEntityPatch() {
        super(Factions.NEUTRAL);
    }

    @Override
    public HumanoidArmature getArmature() {
        return Armatures.BIPED.get();
    }

    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return switch (stunType) {
            case LONG -> Animations.BIPED_HIT_LONG;
            case SHORT, HOLD -> Animations.BIPED_HIT_SHORT;
            case KNOCKDOWN -> Animations.BIPED_KNOCKDOWN;
            case NEUTRALIZE -> Animations.BIPED_COMMON_NEUTRALIZED;
            case FALL -> Animations.BIPED_LANDING;
            default -> null;
        };
    }

    @Override
    public void updateMotion(boolean b) {

    }
}
