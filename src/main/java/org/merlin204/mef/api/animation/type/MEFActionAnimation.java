package org.merlin204.mef.api.animation.type;

import org.merlin204.mef.api.animation.IMEFAnimation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;


/**
 * 示例:接入了MEF动画接口的ActionAnimation
 */
public class MEFActionAnimation extends ActionAnimation implements IMEFAnimation<MEFActionAnimation> {

    public MEFActionAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, accessor, armature);
    }

    public MEFActionAnimation(float transitionTime, float postDelay, AnimationManager.AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, postDelay, accessor, armature);
    }

    public MEFActionAnimation(float transitionTime, float postDelay, String path, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, postDelay, path, armature);
    }


}
