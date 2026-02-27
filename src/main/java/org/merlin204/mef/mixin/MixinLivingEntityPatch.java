package org.merlin204.mef.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = LivingEntityPatch.class,remap = false)
public class MixinLivingEntityPatch {

    @Shadow
    protected Entity lastTryHurtEntity;

//    @Inject(at = @At(value = "RETURN"), method = "tryHurt")
//    protected void mef$tryHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<AttackResult> cir) {
//        LivingEntityPatch<?> livingEntityPatch =  (LivingEntityPatch<?>)(Object)this;
//        if (MEFEntityAPI.getStaminaTypeByEntityType(livingEntityPatch.getOriginal().getType()) != null){
//            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
//            if (cir.getReturnValue().resultType == AttackResult.ResultType.BLOCKED){
//                mefEntity.getStaminaType().whenBlock(mefEntity);
//            }
//        }
//    }
//
//
//    @Inject(at = @At(value = "TAIL"), method = "onDodgeSuccess")
//    protected void mef$onDodgeSuccess(DamageSource damageSource, Vec3 location, CallbackInfo ci) {
//        LivingEntityPatch<?> livingEntityPatch =  (LivingEntityPatch<?>)(Object)this;
//        if (MEFEntityAPI.getStaminaTypeByEntityType(livingEntityPatch.getOriginal().getType()) != null){
//            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
//            mefEntity.getStaminaType().whenDodge(mefEntity);
//        }
//    }

    @Inject(at = @At(value = "TAIL"), method = "onJoinWorld(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/event/entity/EntityJoinLevelEvent;)V")
    protected void mef$setLastAttackResult(Entity entity, EntityJoinLevelEvent par2, CallbackInfo ci) {
        LivingEntityPatch<?> livingEntityPatch =  (LivingEntityPatch<?>)(Object)this;
        if (MEFEntityAPI.getStaminaTypeByEntityType(livingEntityPatch.getOriginal().getType()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
            mefEntity.setStamina(99999999);
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "setLastAttackResult")
    protected void mef$setLastAttackResult(AttackResult attackResult, CallbackInfo ci) {
        LivingEntityPatch<?> livingEntityPatch =  (LivingEntityPatch<?>)(Object)this;
        if (MEFEntityAPI.getStaminaTypeByEntityType(livingEntityPatch.getOriginal().getType()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
            MEFEntity mefEntityHit = MEFCapabilities.getMEFEntity(lastTryHurtEntity);
            float damage = attackResult.damage;
            if (attackResult.resultType == AttackResult.ResultType.BLOCKED){
                mefEntity.getStaminaType().whenBeBlocked(mefEntity,damage);
                Entity hit = lastTryHurtEntity;
                if (MEFEntityAPI.getStaminaTypeByEntityType(hit.getType()) != null){
                    mefEntityHit.getStaminaType().whenBlock(mefEntityHit,damage);
                }
            }else if (attackResult.resultType == AttackResult.ResultType.MISSED){
                mefEntity.getStaminaType().whenBeDodged(mefEntity,damage);
                Entity hit = lastTryHurtEntity;
                if (MEFEntityAPI.getStaminaTypeByEntityType(hit.getType()) != null){
                    mefEntityHit.getStaminaType().whenDodge(mefEntityHit,damage);
                }
            }
        }

    }

}
