package org.merlin204.mef.mixin.epicfight;

import net.minecraft.world.entity.Entity;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = LivingEntityPatch.class,remap = false)
public class LivingEntityPatchMixin {

    @Shadow
    protected Entity lastTryHurtEntity;


    @Inject(at = @At(value = "TAIL"), method = "setLastAttackResult")
    protected void mef$setLastAttackResult(AttackResult attackResult, CallbackInfo ci) {
        LivingEntityPatch<?> livingEntityPatch =  (LivingEntityPatch<?>)(Object)this;
        if (livingEntityPatch.getOriginal() != null && MEFEntityAPI.getStaminaTypeByEntity(livingEntityPatch.getOriginal()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
            MEFEntity mefEntityHit = MEFCapabilities.getMEFEntity(lastTryHurtEntity);
            float damage = attackResult.damage;
            if (attackResult.resultType == AttackResult.ResultType.BLOCKED){
                mefEntity.getStaminaType().whenBeBlocked(mefEntity,damage);
                Entity hit = lastTryHurtEntity;
                if (hit != null && MEFEntityAPI.getStaminaTypeByEntity(hit) != null){
                    mefEntityHit.getStaminaType().whenBlock(mefEntityHit,damage);
                }
            }else if (attackResult.resultType == AttackResult.ResultType.MISSED){
                mefEntity.getStaminaType().whenBeDodged(mefEntity,damage);
                Entity hit = lastTryHurtEntity;
                if (hit != null && MEFEntityAPI.getStaminaTypeByEntity(hit) != null){
                    mefEntityHit.getStaminaType().whenDodge(mefEntityHit,damage);
                }
            }
        }

    }

}
