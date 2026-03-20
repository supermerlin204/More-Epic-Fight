package org.merlin204.mef.mixin.epicfight;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.AttackResult;


/**
 * 去除倒地保护(这个的意义何在?)
 */
@Mixin(value = EntityState.class, remap = false)
public abstract class EntityStateMixin {


    @Shadow
    public abstract boolean knockDown();
    @Inject(method = "attackResult", at = @At("HEAD"), cancellable = true)
    public void attackResult(DamageSource damagesource, CallbackInfoReturnable<AttackResult.ResultType> cir){
        if(this.knockDown()){
            cir.setReturnValue(AttackResult.ResultType.SUCCESS);
        }
    }
}
