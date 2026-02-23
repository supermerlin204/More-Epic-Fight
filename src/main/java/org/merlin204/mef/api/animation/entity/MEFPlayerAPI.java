package org.merlin204.mef.api.animation.entity;


import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.forgeevent.ParryAnimationRegistryEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

/**
 * MEF与玩家有关的逻辑集
 */
public class MEFPlayerAPI {

    private static final Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES = Maps.newHashMap();
    private static final Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> PARRY_ANIMATIONS_WITH_CLASS = Maps.newHashMap();

    /**
     * 逻辑集的初始化
     */
    public static void init(){
        Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> weaponCategory = Maps.newHashMap();
        Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> classMap = Maps.newHashMap();

        ParryAnimationRegistryEvent parryAnimationRegistryEvent = new ParryAnimationRegistryEvent(weaponCategory,classMap);
        ModLoader.get().postEvent(parryAnimationRegistryEvent);

        PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.putAll(weaponCategory);
        PARRY_ANIMATIONS_WITH_CLASS.putAll(classMap);
    }

    /**
     * 尝试播放弹反动画,返回是否成功播放
     * 优先级顺序为副手物品类-主手物品类-副手武器类型-主手武器类型
     */
    public static boolean tryPlayParryAnimation(PlayerPatch<?> patch){
        CapabilityItem main = patch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND);
        CapabilityItem off = patch.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND);
        Class<? extends Item> mainClass = patch.getAdvancedHoldingItemStack(InteractionHand.MAIN_HAND).getItem().getClass();
        Class<? extends Item> offClass = patch.getAdvancedHoldingItemStack(InteractionHand.OFF_HAND).getItem().getClass();
        AnimationManager.AnimationAccessor<?extends StaticAnimation> animationAccessor = null;
        if (PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(main.getWeaponCategory())){
            animationAccessor = PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(main.getWeaponCategory());
        }
        if (PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(off.getWeaponCategory())){
            animationAccessor = PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(off.getWeaponCategory());
        }
        if (PARRY_ANIMATIONS_WITH_CLASS.containsKey(mainClass)){
            animationAccessor = PARRY_ANIMATIONS_WITH_CLASS.get(mainClass);
        }
        if (PARRY_ANIMATIONS_WITH_CLASS.containsKey(offClass)){
            animationAccessor = PARRY_ANIMATIONS_WITH_CLASS.get(offClass);
        }

        if (animationAccessor != null){
            patch.playAnimationSynchronized(animationAccessor,0);
            return true;
        }
        return false;
    }

}
