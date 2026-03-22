package org.merlin204.mef.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.api.forgeevent.*;
import org.merlin204.mef.api.network.PacketHandler;
import org.merlin204.mef.api.stamina.type.DarkSoulStaminaType;
import org.merlin204.mef.api.stamina.type.SekiroStaminaType;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.HashMap;
import java.util.Map;

import static org.merlin204.mef.api.entity.MoreStunType.*;
import static org.merlin204.mef.epicfight.MEFAnimations.*;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    /**
     * 防御与处决无敌帧的处理
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attackResultEvent(AttackResultEvent event) {
        Entity attacker = event.getSource().getEntity();
        LivingEntity target = event.getBeAttacked();
        DamageSource damageSource = event.getSource();

        if (attacker == null || target == null) return;

        LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(attacker, LivingEntityPatch.class);
        LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);

        // 1. 攻击方是否正在处决（处决者状态）
        boolean isAttackerPerformingExecution = false;
        if (attackerPatch != null) {
            var attackerAnimPlayer = attackerPatch.getAnimator().getPlayerFor(null);
            if (attackerAnimPlayer != null) {
                var attackerCurrentAnim = attackerAnimPlayer.getRealAnimation().get();
                if (attackerCurrentAnim != null) {
                    isAttackerPerformingExecution = attackerCurrentAnim.getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).orElse(false);
                }
            }
        }

        // 2. 受击方是否正在“处决别人”？（无敌帧）
        boolean isTargetPerformingExecution = false;

        // 3. 受击方是否正在“被处决”？（锁血，防止被抢怪）
        boolean isTargetBeingExecuted = MEFEntityAPI.canBeExecute(target);

        if (targetPatch != null) {
            var targetAnimPlayer = targetPatch.getAnimator().getPlayerFor(null);
            if (targetAnimPlayer != null) {
                var targetCurrentAnim = targetAnimPlayer.getRealAnimation().get();
                if (targetCurrentAnim != null) {
                    // 受击方正在播放处决动画（正在处决别人）
                    isTargetPerformingExecution = targetCurrentAnim.getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).orElse(false);

                    // 检查受击方是否正在播放“被处决”动画
                    var startAnimAccessor = MEFEntityAPI.getMoreStunAnimation(targetPatch, MoreStunType.BE_EXECUTED_START);
                    boolean isPlayingBeingExecutedAnim = targetCurrentAnim.getProperty(MEFAnimationProperty.IS_VICTIM_ANIMATION).orElse(false) ||
                            (startAnimAccessor != null && targetCurrentAnim.equals(startAnimAccessor.get()));

                    isTargetBeingExecuted = isTargetBeingExecuted || isPlayingBeingExecutedAnim;
                }
            }
        }

        if (isTargetPerformingExecution) {
            event.setAttackResult(AttackResult.blocked(0.0F));
            return;
        }

        if (isTargetBeingExecuted) {
            if (!isAttackerPerformingExecution) {
                event.setAttackResult(AttackResult.blocked(0.0F));
                return;
            }
        }

        if (isAttackerPerformingExecution) {
            if (!isTargetBeingExecuted) {
                event.setAttackResult(AttackResult.blocked(0.0F));
                return;
            }
        }

        if (targetPatch != null) {
            var targetAnimPlayer = targetPatch.getAnimator().getPlayerFor(null);
            if (targetAnimPlayer != null) {
                var targetCurrentAnim = targetAnimPlayer.getRealAnimation().get();
                float time = targetAnimPlayer.getElapsedTime();

                if (targetCurrentAnim != null && targetCurrentAnim.getProperty(MEFAnimationProperty.DEFENSE_TIME).isPresent()){
                    boolean successful = false;

                    for (DefenseTimePair defenseTimePair : targetCurrentAnim.getProperty(MEFAnimationProperty.DEFENSE_TIME).get()){
                        if (defenseTimePair.isTimeIn(time) && defenseTimePair.canDefense(targetPatch, attacker, damageSource)){
                            defenseTimePair.defenseSuccess(targetPatch, attacker, damageSource);
                            successful = true;
                        }
                    }

                    if (successful){
                        event.setAttackResult(AttackResult.blocked(event.getDamage()));
                        if (attackerPatch != null) {
                            attackerPatch.setLastAttackResult(AttackResult.blocked(event.getDamage()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加默认的耐力类型
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void staminaTypeRegistry(StaminaTypeRegistryEvent event) {
        event.getMap().put(EntityType.IRON_GOLEM,new SekiroStaminaType(50,0F));
        event.getMap().put(EntityType.POLAR_BEAR,new SekiroStaminaType(20,0F));
        event.getMap().put(EntityType.WARDEN,new DarkSoulStaminaType(20,0F));

        event.getMap().put(EntityType.WOLF,new DarkSoulStaminaType(5,0F));

        event.getMap().put(EntityType.HUSK,new DarkSoulStaminaType(5,0F));
        event.getMap().put(EntityType.ZOMBIE,new DarkSoulStaminaType(5,0F));
        event.getMap().put(EntityType.SHULKER,new DarkSoulStaminaType(5,0F));
        event.getMap().put(EntityType.WITHER_SKELETON,new DarkSoulStaminaType(5,0F));
    }

    /**
     * 为所有的EF人型实体添加更多硬直动画
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void moreStunTypeRegistry(MoreStunTypeRegistryEvent event) {
        Map<MoreStunType, AnimationManager.AnimationAccessor<? extends StaticAnimation>> biped = new HashMap<>(Map.of(
                BE_PARRIED_L, BIPED_BE_PARRIED_L, BE_PARRIED_R, BIPED_BE_PARRIED_R, BE_PARRIED_M, BIPED_BE_PARRIED_M,
                BE_EXECUTED_START,BIPED_BE_EXECUTED_START,BE_EXECUTED_END,BIPED_BE_EXECUTED_END
                ));
        event.getMap().put(EntityType.PLAYER,biped);
        event.getMap().put(EntityType.ZOMBIE,biped);
        event.getMap().put(EntityType.HUSK, biped);
        event.getMap().put(EntityType.SHULKER, biped);
        event.getMap().put(EntityType.WITHER_SKELETON, biped);
        event.getMap().put(EntityType.ZOMBIE_VILLAGER, biped);
        event.getMap().put(EntityType.VINDICATOR, biped);
        event.getMap().put(EntityType.PILLAGER, biped);
        event.getMap().put(EntityType.PIGLIN, biped);
        event.getMap().put(EntityType.PIGLIN_BRUTE, biped);
        event.getMap().put(EntityType.ZOMBIFIED_PIGLIN, biped);
    }

    /**
     * 添加默认的弹反动画
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void ParryAnimationRegistryEvent(ParryAnimationRegistryEvent event) {
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SHIELD,SHIELD_PARRY);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.LONGSWORD,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.AXE,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.DAGGER,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SWORD,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.TACHI,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.TRIDENT,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.UCHIGATANA,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.HOE,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.PICKAXE,PARRY_ONE_HAND);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SHOVEL,PARRY_ONE_HAND);
    }


    /**
     * 添加默认的处决动画
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void ExecuteAnimationRegistryEvent(ExecuteAnimationRegistryEvent event) {
        event.registerCategory(CapabilityItem.WeaponCategories.NOT_WEAPON, FIST_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.FIST, FIST_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.LONGSWORD, ONE_HAND_EXECUTE, null);

        event.registerCategory(CapabilityItem.WeaponCategories.DAGGER, ONE_HAND_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.SWORD, ONE_HAND_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.TACHI, ONE_HAND_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.TRIDENT, ONE_HAND_EXECUTE, null);
        event.registerCategory(CapabilityItem.WeaponCategories.UCHIGATANA, ONE_HAND_EXECUTE, null);

        event.registerCategory(CapabilityItem.WeaponCategories.SHOVEL, ONE_HAND_EXECUTE_HARD, null);
        event.registerCategory(CapabilityItem.WeaponCategories.AXE, ONE_HAND_EXECUTE_HARD, null);
        event.registerCategory(CapabilityItem.WeaponCategories.HOE, ONE_HAND_EXECUTE_HARD, null);
        event.registerCategory(CapabilityItem.WeaponCategories.PICKAXE, ONE_HAND_EXECUTE_HARD, null);
        event.registerCategory(CapabilityItem.WeaponCategories.SPEAR, ONE_HAND_EXECUTE_HARD, null);
        event.registerCategory(CapabilityItem.WeaponCategories.GREATSWORD, ONE_HAND_EXECUTE_HARD, null);


        //特定实体
        event.registerCategory(CapabilityItem.WeaponCategories.TACHI, EntityType.WITHER_SKELETON, ARES_BIPED_COMMON_EXECUTE, ARES_BIPED_BE_EXECUTED);
        //特定骨架
        event.registerCategory(CapabilityItem.WeaponCategories.TACHI, Armatures.CREEPER.get(), ARES_BIPED_COMMON_EXECUTE, ARES_BIPED_BE_EXECUTED);
        //通用
        event.registerCategory(CapabilityItem.WeaponCategories.TACHI, ARES_BIPED_COMMON_EXECUTE, ARES_BIPED_BE_EXECUTED);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

}
