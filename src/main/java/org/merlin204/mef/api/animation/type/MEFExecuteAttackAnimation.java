package org.merlin204.mef.api.animation.type;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.lockon.CameraLockUtil;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;

/**
 * 处决攻击动画类,全程无敌，带有上下方向修正，可传入时间点临时开启/关闭锁定
 */
public class MEFExecuteAttackAnimation extends AttackAnimation {
    public MEFExecuteAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, float lockOnStart, float lockOnEnd, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true);
        this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY, HitEntityList.Priority.TARGET);
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1);
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnStart, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.startLockOn(50, 5);
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnEnd, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationProperty.AttackAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
    }

    public MEFExecuteAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, float lockOnStart, float lockOnEnd, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
        this.addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true);
        this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY, HitEntityList.Priority.TARGET);
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1);
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnStart, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.startLockOn(50, 5);
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnEnd, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationProperty.AttackAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
    }

    public MEFExecuteAttackAnimation(float transitionTime, float lockOnStart, float lockOnEnd, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true);
        this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY, HitEntityList.Priority.TARGET);
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1);
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnStart, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.startLockOn(50, 5);
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnEnd, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationProperty.AttackAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
    }

    public MEFExecuteAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, float lockOnStart, float lockOnEnd, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, AssetAccessor<? extends Armature> armature) {
        super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
        this.addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true);
        this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY, HitEntityList.Priority.TARGET);
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1);
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnStart, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.startLockOn(50, 5);
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnEnd, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationProperty.AttackAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
    }

    public MEFExecuteAttackAnimation(float convertTime, float lockOnStart, float lockOnEnd, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
        super(convertTime, path, armature, phases);
        this.addProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION,true);
        this.addProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY, HitEntityList.Priority.TARGET);
        this.addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, Animations.ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1);
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnStart, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.startLockOn(50, 5);
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationEvent.InTimeEvent.create(lockOnEnd, (livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
        this.addEvents(AnimationProperty.AttackAnimationProperty.ON_END_EVENTS, AnimationEvent.SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
            CameraLockUtil.endLockOn();
        }, AnimationEvent.Side.LOCAL_CLIENT));
    }

    @Override
    protected void bindPhaseState(Phase phase) {
        float preDelay = phase.preDelay;
        this.stateSpectrumBlueprint
                .newTimePair(phase.start, preDelay).addState(EntityState.PHASE_LEVEL, 1)
                .newTimePair(phase.start, phase.contact).addState(EntityState.CAN_SKILL_EXECUTION, false)
                .newTimePair(phase.start, phase.recovery).addState(EntityState.MOVEMENT_LOCKED, true).addState(EntityState.UPDATE_LIVING_MOTION, false).addState(EntityState.CAN_BASIC_ATTACK, false)
                .newTimePair(phase.start, phase.end).addState(EntityState.INACTION, true)
                //防乱转丢伤害,全程无敌
                .newTimePair(0, Float.MAX_VALUE).addState(EntityState.TURNING_LOCKED, true).addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource -> AttackResult.ResultType.MISSED))
                .newTimePair(preDelay, phase.contact).addState(EntityState.ATTACKING, true).addState(EntityState.PHASE_LEVEL, 2)
                .newTimePair(phase.contact, phase.end).addState(EntityState.PHASE_LEVEL, 3);
    }
}
