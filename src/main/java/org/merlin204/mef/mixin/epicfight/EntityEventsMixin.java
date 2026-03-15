package org.merlin204.mef.mixin.epicfight;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.api.forgeevent.AttackResultEvent;
import org.merlin204.mef.api.forgeevent.MoreStunTypeRegistryEvent;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.registry.MEFMobEffects;
import org.merlin204.mef.world.entity.ai.attribute.MEFAttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.events.EntityEvents;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mixin(value = EntityEvents.class, remap = false)
public class EntityEventsMixin {

    /**
     * 捕获攻击结果,从而进行防御与被防御的判定
     */
    @Redirect(
            method = "attackEvent(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;tryHurt(Lnet/minecraft/world/damagesource/DamageSource;F)Lyesman/epicfight/api/utils/AttackResult;"
            )
    )
    private static AttackResult redirectTryHurt(LivingEntityPatch<?> entityPatch, DamageSource source, float damage,LivingAttackEvent event) {
        AttackResult attackResult = entityPatch.tryHurt(source, damage);
        Entity atk = source.getEntity();
        LivingEntity hurt = event.getEntity();
        MEFEntity atkMefEntity = MEFCapabilities.getMEFEntity(atk);
        MEFEntity hurtMefEntity = MEFCapabilities.getMEFEntity(hurt);

        //抛出修改攻击结果的事件
        AttackResultEvent attackResultEvent = new AttackResultEvent(attackResult,source,hurt,damage);
        ModLoader.get().postEvent(attackResultEvent);
        attackResult = attackResultEvent.getAttackResult();

        if (MEFEntityAPI.getStaminaTypeByEntity(atk) != null && atkMefEntity != null){
            if (attackResult.resultType == AttackResult.ResultType.BLOCKED){
                atkMefEntity.getStaminaType().whenBeBlocked(atkMefEntity,damage, source);
            }else if (attackResult.resultType == AttackResult.ResultType.MISSED){
                atkMefEntity.getStaminaType().whenBeDodged(atkMefEntity,damage, source);
            }
        }

        if (MEFEntityAPI.getStaminaTypeByEntity(hurt) != null && hurtMefEntity != null){
            if (attackResult.resultType == AttackResult.ResultType.BLOCKED){
                hurtMefEntity.getStaminaType().whenBlock(atkMefEntity,damage, source);
            }else if (attackResult.resultType == AttackResult.ResultType.MISSED){
                hurtMefEntity.getStaminaType().whenDodge(atkMefEntity,damage, source);
            }
        }
        return attackResult;
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
                //有耐力条的不执行EF的破防倒地
                if (MEFEntityAPI.getStaminaTypeByEntity(hitEntity) != null){
                    if (epicFightDamageSource.getStunType() == StunType.KNOCKDOWN || epicFightDamageSource.getStunType() == StunType.NEUTRALIZE ){
                        epicFightDamageSource.setStunType(StunType.LONG);
                    }
                }


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
                            }else if (hitEntity.hasEffect(MEFMobEffects.STUN.get())){
                                successful = hitEntity.addEffect(new MobEffectInstance(MEFMobEffects.STUN.get(),60,0,false,false,false));
                            }
                        }
                        //若成功播放或添加效果则取消史诗战斗的硬直,并进行伤害加成
                        if (successful){
                            if (MEFEntityAPI.getStaminaTypeByEntity(hitEntity) != null){
                                MEFEntity hit = MEFCapabilities.getMEFEntity(hitEntity);
                                event.setAmount(hit.getStaminaType().beExecutedDamageModifier(hit,damageSource,event.getAmount()));
                            }
                            epicFightDamageSource.setStunType(StunType.NONE);
                        }
                    }
                }
            }
        }
    }
}