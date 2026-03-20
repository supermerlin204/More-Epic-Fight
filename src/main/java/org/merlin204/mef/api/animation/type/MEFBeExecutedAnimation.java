package org.merlin204.mef.api.animation.type;

import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MEFBeExecutedAnimation extends LongHitAnimation {

    public MEFBeExecutedAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends LongHitAnimation> accessor, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, accessor, armature);
    }

    public MEFBeExecutedAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature) {
        super(transitionTime, path, armature);
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
        }
    }
}