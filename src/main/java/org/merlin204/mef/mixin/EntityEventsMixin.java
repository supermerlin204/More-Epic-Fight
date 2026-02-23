package org.merlin204.mef.mixin;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.events.EntityEvents;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(EntityEvents.class)
public class EntityEventsMixin {


    /**
     * 和EF使用同一个订阅,但在其之前先一步处理
     */
    @Inject(method = "attackEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private static void attackEvent(LivingAttackEvent event, CallbackInfo ci) {
        Entity causingEntity = event.getSource().getEntity();
        LivingEntity hitEntity = event.getEntity();
        DamageSource damageSource = event.getSource();



        if (causingEntity != null) {
            LivingEntityPatch<?> attackerEntityPatch = EpicFightCapabilities.getEntityPatch(causingEntity, LivingEntityPatch.class);
            if (attackerEntityPatch != null) {

            }

            LivingEntityPatch<?> hitEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, LivingEntityPatch.class);
            if (hitEntityPatch != null){
                StaticAnimation animation = hitEntityPatch.getAnimator().getPlayerFor(null).getRealAnimation().get();
                float time = hitEntityPatch.getAnimator().getPlayerFor(null).getElapsedTime();
                if (animation.getProperty(MEFAnimationProperty.DEFENSE_TIME).isPresent()){
                    boolean successful = false;
                    for (DefenseTimePair defenseTimePair:animation.getProperty(MEFAnimationProperty.DEFENSE_TIME).get()){
                        if (defenseTimePair.isTimeIn(time) && defenseTimePair.canDefense(hitEntityPatch,causingEntity,damageSource)){
                            defenseTimePair.defenseSuccess(hitEntityPatch,causingEntity,damageSource);
                            successful = true;
                        }
                    }
                    if (successful){
                        event.setCanceled(true);
                        EpicFightCapabilities.getUnparameterizedEntityPatch(event.getSource().getEntity(), LivingEntityPatch.class).ifPresent(attackerentitypatch -> {
                            attackerentitypatch.setLastAttackResult(AttackResult.blocked(event.getAmount()));
                        });
                        ci.cancel();
                    }
                }

            }
        }
    }
}