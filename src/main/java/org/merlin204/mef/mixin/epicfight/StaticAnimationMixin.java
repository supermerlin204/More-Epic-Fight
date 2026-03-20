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


/**
 * 倒地动画结束的时候尝试过一下耐力类型的倒地结束
 */
@Mixin(value = StaticAnimation.class, remap = false)
public abstract class StaticAnimationMixin {


    @Inject(method = "end", at = @At("HEAD"))
    public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd, CallbackInfo ci){
        if (entityPatch.getEntityState().knockDown() && !entityPatch.getOriginal().level().isClientSide){
            if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null){
                MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
                mefEntity.getStaminaType().whenKnockDownEnd(mefEntity);
            }
        }
    }
}
