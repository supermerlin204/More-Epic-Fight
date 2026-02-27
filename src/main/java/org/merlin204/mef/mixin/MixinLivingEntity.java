package org.merlin204.mef.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = LivingEntity.class)
public class MixinLivingEntity {


    @Inject(at = @At(value = "TAIL"), method = "<clinit>")
    private static void mef$staticInitialize(CallbackInfo callbackInfo) {
        MEFEntity.initLivingEntityDataAccessor();
    }

    @Inject(at = @At(value = "TAIL"), method = "defineSynchedData()V")
    protected void mef$defineSynchedData(CallbackInfo info) {
        if(MEFEntityAPI.getStaminaTypeByEntityType(((LivingEntity)(Object)this).getType()) != null){
            MEFEntity.createSyncedEntityData((LivingEntity)(Object)this);
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "tick")
    protected void mef$tick(CallbackInfo info) {
        if(MEFEntityAPI.getStaminaTypeByEntityType(((LivingEntity)(Object)this).getType()) != null){
            MEFCapabilities.getMEFEntity((LivingEntity)(Object)this).tick();
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "hurt")
    protected void mef$hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(MEFEntityAPI.getStaminaTypeByEntityType(((LivingEntity)(Object)this).getType()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity((LivingEntity)(Object)this);
            mefEntity.getStaminaType().whenHurt(mefEntity,damageSource,amount);
        }
    }

}
