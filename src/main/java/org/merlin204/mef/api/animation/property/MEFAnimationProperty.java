package org.merlin204.mef.api.animation.property;

import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import yesman.epicfight.api.animation.property.AnimationProperty;

import java.util.List;

public class MEFAnimationProperty {


    public static final AnimationProperty.StaticAnimationProperty<List<DefenseTimePair>> DEFENSE_TIME = new AnimationProperty.StaticAnimationProperty<List<DefenseTimePair>>();

    /**
     * 攻击动画是否是处决动画
     */
    public static final AnimationProperty.AttackAnimationProperty<Boolean> IS_EXECUTE_ANIMATION = new AnimationProperty.AttackAnimationProperty<>();
    /**
     * 是否为单个的被处决动画（用于锁血）
     */
    public static final AnimationProperty.ActionAnimationProperty<Boolean> IS_VICTIM_ANIMATION = new AnimationProperty.ActionAnimationProperty<>();

}

