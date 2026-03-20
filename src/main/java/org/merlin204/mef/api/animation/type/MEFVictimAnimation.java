package org.merlin204.mef.api.animation.type;

import net.minecraft.world.damagesource.DamageSource;
import org.merlin204.mef.api.animation.IMEFAnimation;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

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
                if (mefEntity.isDoomed()) {
                    DamageSource executionSource = mefEntity.getExecutionDamageSource();

                    if (executionSource == null) {
                        executionSource = livingEntityPatch.getOriginal().damageSources().generic();
                    }

                    livingEntityPatch.getOriginal().hurt(executionSource, Float.MAX_VALUE);

                    mefEntity.clearDoomed();
                }
            }
        }
    }
}