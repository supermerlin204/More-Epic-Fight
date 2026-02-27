package org.merlin204.mef.mixin;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.registry.MEFMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.events.EntityEvents;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

@Mixin(EntityEvents.class)
public class EntityEventsMixin {


    /**
     * 和EF使用同一个订阅,但在其之前先一步处理,主要处理动画防御逻辑
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
                        EpicFightCapabilities.getUnparameterizedEntityPatch(event.getSource().getEntity(), LivingEntityPatch.class).ifPresent(patch -> {
                            patch.setLastAttackResult(AttackResult.blocked(event.getAmount()));
                        });
                        ci.cancel();
                    }
                }

            }
        }
    }

    /**
     * 和EF使用同一个订阅,但在其之前先一步处理,主要处理动画处决逻辑
     */
    @Inject(method = "hurtEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hurtEvent(LivingHurtEvent event, CallbackInfo ci) {
        Entity causingEntity = event.getSource().getEntity();
        LivingEntity hitEntity = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (causingEntity != null) {
            LivingEntityPatch<?> attackerEntityPatch = EpicFightCapabilities.getEntityPatch(causingEntity, LivingEntityPatch.class);
            LivingEntityPatch<?> hitEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, LivingEntityPatch.class);
            if (attackerEntityPatch != null  && damageSource instanceof EpicFightDamageSource epicFightDamageSource) {
                if (epicFightDamageSource.getAnimation().get() instanceof AttackAnimation animation){
                    //如果伤害源的动画是处决动画
                    if (animation.getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).isPresent() && animation.getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).get()){
                        boolean successful = false;
                        //检查是否是史诗战斗实体
                        if (hitEntityPatch != null){
                            StaticAnimation targetAnimation = hitEntityPatch.getAnimator().getPlayerFor(null).getRealAnimation().get();
                            //判断是否已经进入处决状态,进入处决状态再受伤则播放处决结束动画,未触发则播放处决开始动画
                            if (MEFEntityAPI.getMoreStunAnimation(hitEntityPatch, MoreStunType.BE_EXECUTED_START) !=null && targetAnimation == MEFEntityAPI.getMoreStunAnimation(hitEntityPatch, MoreStunType.BE_EXECUTED_START).get()){
                                successful = MEFEntityAPI.playMoreStunAnimation(hitEntityPatch,MoreStunType.BE_EXECUTED_END);
                            }else {
                                successful = MEFEntityAPI.playMoreStunAnimation(hitEntityPatch,MoreStunType.BE_EXECUTED_START);
                            }
                            //若成功播放硬直则强制转向实体
                            if (successful){
                                float yRot = attackerEntityPatch.getYRot() + 180;
                                hitEntity.setYRot(yRot);
                                hitEntity.yBodyRot = yRot;
                                hitEntity.yBodyRotO = yRot;
                                hitEntity.yRotO = yRot;
                            }
                        }
                        //若未能成功播放动画或压根不是史诗战斗实体则尝试给实体添加眩晕buff
                        if (!successful){
                            if (hitEntity.hasEffect(MEFMobEffects.KNOCKDOWN.get())){
                                hitEntity.removeEffect(MEFMobEffects.KNOCKDOWN.get());
                                successful = hitEntity.addEffect(new MobEffectInstance(MEFMobEffects.STUN.get(),60,0,false,false,false));
                            }
                        }
                        //若成功播放或添加效果则取消史诗战斗的硬直
                        if (successful){
                            epicFightDamageSource.setStunType(StunType.NONE);
                        }
                    }
                }
            }
        }
    }
}