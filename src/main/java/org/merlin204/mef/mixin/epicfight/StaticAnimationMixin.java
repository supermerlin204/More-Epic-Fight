package org.merlin204.mef.mixin.epicfight;

import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = StaticAnimation.class, remap = false)
public abstract class StaticAnimationMixin {

    @Inject(method = "end", at = @At("HEAD"))
    public void mef$onAnimationEnd(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd, CallbackInfo ci){
        if (entityPatch.getEntityState().knockDown() && !entityPatch.getOriginal().level().isClientSide){
            if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null){
                MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
                mefEntity.getStaminaType().whenKnockDownEnd(mefEntity);
            }
        }

        StaticAnimation self = (StaticAnimation)(Object)this;

        if (!self.isLinkAnimation()) {
            if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null) {
                MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
                if (mefEntity.getAnimationSpeed() != 1.0F) {
                    mefEntity.resetSpeedProperties();
                }
            }
        }
    }
}