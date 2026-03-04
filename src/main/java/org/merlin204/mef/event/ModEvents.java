package org.merlin204.mef.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.entity.MoreLivingMotions;
import org.merlin204.mef.api.entity.MoreStunType;
import org.merlin204.mef.api.forgeevent.ExecuteAnimationRegistryEvent;
import org.merlin204.mef.api.forgeevent.MoreStunTypeRegistryEvent;

import org.merlin204.mef.api.forgeevent.ParryAnimationRegistryEvent;
import org.merlin204.mef.api.forgeevent.StaminaTypeRegistryEvent;
import org.merlin204.mef.api.network.PacketHandler;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.api.stamina.type.DarkSoulStaminaType;
import org.merlin204.mef.api.stamina.type.SekiroStaminaType;
import org.merlin204.mef.epicfight.MEFAnimations;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.InitAnimatorEvent;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.HashMap;
import java.util.Map;

import static org.merlin204.mef.api.entity.MoreStunType.*;
import static org.merlin204.mef.epicfight.MEFAnimations.*;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {



    /**
     * 添加默认的耐力类型
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void staminaTypeRegistry(StaminaTypeRegistryEvent event) {

        event.getMap().put(EntityType.IRON_GOLEM,new SekiroStaminaType(50,0F).setBarRenderType(StaminaType.BarRenderType.BOSS));

        event.getMap().put(EntityType.POLAR_BEAR,new SekiroStaminaType(20,0F).setBarRenderType(StaminaType.BarRenderType.BOSS));

        event.getMap().put(EntityType.HUSK,new DarkSoulStaminaType(5,0F));
        event.getMap().put(EntityType.ZOMBIE,new DarkSoulStaminaType(5,0F));
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
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.NOT_WEAPON,FIST_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.FIST,FIST_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.LONGSWORD,ONE_HAND_EXECUTE);

        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.DAGGER,ONE_HAND_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SWORD,ONE_HAND_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.TACHI,ONE_HAND_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.TRIDENT,ONE_HAND_EXECUTE);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.UCHIGATANA,ONE_HAND_EXECUTE);

        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SHOVEL,ONE_HAND_EXECUTE_HARD);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.AXE,ONE_HAND_EXECUTE_HARD);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.HOE,ONE_HAND_EXECUTE_HARD);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.PICKAXE,ONE_HAND_EXECUTE_HARD);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.SPEAR,ONE_HAND_EXECUTE_HARD);
        event.getWeaponCategoryMap().put(CapabilityItem.WeaponCategories.GREATSWORD,ONE_HAND_EXECUTE_HARD);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

}
