package org.merlin204.mef.api.animation;

import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;


/**
 * MEF动画的接口,接入后可便捷添加MEF的Property（不接也能加）
 */
public interface IMEFAnimation<T extends StaticAnimation> {

    default T addDefenseTimePair(DefenseTimePair... defenseTimePair){
        ((T)this).addProperty(MEFAnimationProperty.DEFENSE_TIME, List.of(defenseTimePair));
        return (T)this;
    }


}
