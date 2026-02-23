package org.merlin204.mef.event;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.merlin204.mef.api.animation.entity.MoreStunType;
import org.merlin204.mef.api.forgeevent.MoreStunTypeRegistryEvent;

import org.merlin204.mef.api.forgeevent.ParryAnimationRegistryEvent;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.HashMap;
import java.util.Map;

import static org.merlin204.mef.api.animation.entity.MoreStunType.*;
import static org.merlin204.mef.epicfight.MEFAnimations.*;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {



    /**
     * 为所有的EF人型实体添加更多硬直动画
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void moreStunTypeRegistry(MoreStunTypeRegistryEvent event) {
        Map<MoreStunType, AnimationManager.AnimationAccessor<? extends StaticAnimation>> biped = new HashMap<>(Map.of(
                BE_PARRIED_L, BIPED_BE_PARRIED_L, BE_PARRIED_R, BIPED_BE_PARRIED_R, BE_PARRIED_M, BIPED_BE_PARRIED_M));
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




}
