package org.merlin204.mef.mixin.minecraft;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.world.entity.ai.attribute.MEFAttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributeSupplier;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin {


    @Inject(at = @At(value = "TAIL"), method = "<clinit>")
    private static void mef$staticInitialize(CallbackInfo callbackInfo) {
        MEFEntity.initLivingEntityDataAccessor();
    }

    @Inject(at = @At(value = "TAIL"), method = "defineSynchedData()V")
    protected void mef$defineSynchedData(CallbackInfo info) {
        MEFEntity.createSyncedEntityData((LivingEntity)(Object)this);
    }

    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    private void mef$constructor(EntityType<?> entityType, Level level, CallbackInfo info) {
        LivingEntity self = (LivingEntity)((Object)this);
        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(self);
        if (MEFEntityAPI.getStaminaTypeByEntity(self) != null){
            self.getAttributes().supplier = new MEFAttributeSupplier(self.getAttributes().supplier);

            AttributeInstance maxStamina = self.getAttribute(EpicFightAttributes.MAX_STAMINA.get());
            if (maxStamina != null) maxStamina.setBaseValue(mefEntity.getStaminaType().getDefaultMax());
            AttributeInstance staminaRegen = self.getAttribute(EpicFightAttributes.STAMINA_REGEN.get());
            if (staminaRegen != null) staminaRegen.setBaseValue(mefEntity.getStaminaType().getDefaultRegen());
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "tick")
    protected void mef$tick(CallbackInfo info) {
        if(MEFEntityAPI.getStaminaTypeByEntity(((LivingEntity)(Object)this)) != null){
            MEFCapabilities.getMEFEntity((LivingEntity)(Object)this).tick();
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "hurt")
    protected void mef$hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(MEFEntityAPI.getStaminaTypeByEntity(((LivingEntity)(Object)this)) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity((LivingEntity)(Object)this);
            mefEntity.getStaminaType().whenHurt(mefEntity,damageSource,amount);
        }
    }

}
