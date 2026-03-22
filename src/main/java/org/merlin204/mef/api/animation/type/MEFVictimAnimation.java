package org.merlin204.mef.api.animation.type;

import org.merlin204.mef.api.animation.IMEFAnimation;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * 一般的被处决动画类
 */
public class MEFVictimAnimation extends LongHitAnimation implements IMEFAnimation<MEFVictimAnimation> {

    public MEFVictimAnimation(float transitionTime, AnimationAccessor<? extends LongHitAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, accessor, armature);
        this.addProperty(MEFAnimationProperty.IS_VICTIM_ANIMATION, true);
    }

    public MEFVictimAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, path, armature);
        this.addProperty(MEFAnimationProperty.IS_VICTIM_ANIMATION, true);
    }

    @Override
    public void begin(LivingEntityPatch<?> livingEntityPatch) {
        super.begin(livingEntityPatch);

        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
        if (mefEntity != null) {
            mefEntity.setBeingExecuted(true);
        }
    }

    @Override
    public void tick(LivingEntityPatch<?> livingEntityPatch) {
        super.tick(livingEntityPatch);
        livingEntityPatch.setYRot(livingEntityPatch.getYRotO());
        livingEntityPatch.getOriginal().setYRot(livingEntityPatch.getYRotO());
        livingEntityPatch.getOriginal().setYBodyRot(livingEntityPatch.getYRotO());
    }

    @Override
    public void end(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
        super.end(livingEntityPatch, nextAnimation, isEnd);

        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
        if (mefEntity != null) {
            mefEntity.setBeingExecuted(false);

            if (!livingEntityPatch.isLogicalClient()) {
                mefEntity.clearDoomed();
            }
        }
    }
}