package org.merlin204.mef.mixin.epicfight;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreLivingMotions;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.epicfight.IMEFPatch;
import org.merlin204.mef.world.entity.ai.goal.PatchEntityWonderGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@Mixin(value = MobPatch.class,remap = false)
public class MobPatchMixin {

    @Inject(at = @At(value = "TAIL"), method = "onJoinWorld(Lnet/minecraft/world/entity/Mob;Lnet/minecraftforge/event/entity/EntityJoinLevelEvent;)V")
    protected void mef$onJoinWorld(Mob par1, EntityJoinLevelEvent par2, CallbackInfo ci) {

        MobPatch<?> mobPatch = (MobPatch<?>) (Object)this;
        if (this instanceof IMEFPatch){
            mobPatch.getOriginal().goalSelector.addGoal(0, new PatchEntityWonderGoal(mobPatch));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "commonMobUpdateMotion", cancellable = true)
    protected void mef$commonMobUpdateMotion(boolean considerInaction, CallbackInfo ci) {
        ci.cancel();
        MobPatch<?> mobPatch = (MobPatch<?>) (Object)this;
        if (mobPatch.getOriginal().getHealth() <= 0.0F) {
            mobPatch.currentLivingMotion = LivingMotions.DEATH;
        } else if (mobPatch.getEntityState().inaction() && considerInaction) {
            mobPatch.currentLivingMotion = LivingMotions.INACTION;
        } else {
            if (mobPatch.getOriginal().getVehicle() != null)
                mobPatch.currentLivingMotion = LivingMotions.MOUNT;
            else
            if (mobPatch.getOriginal().getDeltaMovement().y < -0.55F || mobPatch.isAirborneState())
                mobPatch.currentLivingMotion = LivingMotions.FALL;
            else if (mobPatch.getOriginal().walkAnimation.speed() > 0.01F){
                mobPatch.currentLivingMotion = LivingMotions.WALK;
                if (MEFEntityAPI.getStaminaTypeByEntity(mobPatch.getOriginal()) != null){
                    MEFEntity mefEntity = MEFCapabilities.getMEFEntity(mobPatch.getOriginal());
                    if (mefEntity.isWonder()){
                        if (mefEntity.getWonderSpeed() > 0){
                            mobPatch.currentLivingMotion = MoreLivingMotions.WONDER_R;
                        }else {
                            mobPatch.currentLivingMotion = MoreLivingMotions.WONDER_L;
                        }
                    }
                }
            }
            else
                mobPatch.currentLivingMotion = LivingMotions.IDLE;
        }
        mobPatch.currentCompositeMotion = mobPatch.currentLivingMotion;

    }

    @Inject(at = @At(value = "HEAD"), method = "commonAggressiveMobUpdateMotion", cancellable = true)
    protected void mef$commonAggressiveMobUpdateMotion(boolean considerInaction, CallbackInfo ci) {
        ci.cancel();
        MobPatch<?> mobPatch = (MobPatch<?>) (Object)this;
        if (mobPatch.getOriginal().getHealth() <= 0.0F) {
            mobPatch.currentLivingMotion = LivingMotions.DEATH;
        } else if (mobPatch.getEntityState().inaction() && considerInaction) {
            mobPatch.currentLivingMotion = LivingMotions.INACTION;
        } else {
            if (mobPatch.getOriginal().getVehicle() != null)
                mobPatch.currentLivingMotion = LivingMotions.MOUNT;
            else
            if (mobPatch.getOriginal().getDeltaMovement().y < -0.55F || mobPatch.isAirborneState())
                mobPatch.currentLivingMotion = LivingMotions.FALL;
            else if (mobPatch.getOriginal().walkAnimation.speed() > 0.01F){
                if (mobPatch.getOriginal().isAggressive())
                    mobPatch.currentLivingMotion = LivingMotions.CHASE;
                else {
                    mobPatch.currentLivingMotion = LivingMotions.WALK;
                    if (MEFEntityAPI.getStaminaTypeByEntity(mobPatch.getOriginal()) != null){
                        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(mobPatch.getOriginal());
                        if (mefEntity.isWonder()){
                            if (mefEntity.getWonderSpeed() > 0){
                                mobPatch.currentLivingMotion = MoreLivingMotions.WONDER_R;
                            }else {
                                mobPatch.currentLivingMotion = MoreLivingMotions.WONDER_L;
                            }
                        }
                    }
                }
            }
            else
                mobPatch.currentLivingMotion = LivingMotions.IDLE;
        }
        mobPatch.currentCompositeMotion = mobPatch.currentLivingMotion;
    }

}
