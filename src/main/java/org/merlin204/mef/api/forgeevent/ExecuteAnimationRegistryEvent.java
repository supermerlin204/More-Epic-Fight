package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;


/**
 * 添加处决动画
 */
public class ExecuteAnimationRegistryEvent extends Event implements IModBusEvent {
    private final Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> weaponCategoryMap;
    private final Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> classMap;

    public ExecuteAnimationRegistryEvent(Map<WeaponCategory, AnimationManager.AnimationAccessor<? extends StaticAnimation>> weaponCategoryMap, Map<Class<? extends Item>, AnimationManager.AnimationAccessor<? extends StaticAnimation>> classMap) {
        this.weaponCategoryMap = weaponCategoryMap;
        this.classMap = classMap;
    }


    public Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> getWeaponCategoryMap() {
        return weaponCategoryMap;
    }

    public Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> getClassMap() {
        return classMap;
    }
}
