package org.merlin204.mef.api.animation.property;

import com.mojang.serialization.Codec;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import yesman.epicfight.api.animation.property.AnimationProperty;

import java.util.List;

public class MEFAnimationProperty {


    public static final AnimationProperty.StaticAnimationProperty<List<DefenseTimePair>> DEFENSE_TIME = new AnimationProperty.StaticAnimationProperty<List<DefenseTimePair>>();

    public static final AnimationProperty.AttackAnimationProperty<Boolean> IS_EXECUTE_ANIMATION = new AnimationProperty.AttackAnimationProperty<>();

}

