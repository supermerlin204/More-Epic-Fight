package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.api.execution.ExecutionAnimSet;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

public class ExecuteAnimationRegistryEvent extends Event implements IModBusEvent {

    private final Map<WeaponCategory, ExecutionAnimSet> weaponCategoryMap;
    private final Map<Class<? extends Item>, ExecutionAnimSet> classMap;

    public ExecuteAnimationRegistryEvent(
            Map<WeaponCategory, ExecutionAnimSet> weaponCategoryMap,
            Map<Class<? extends Item>, ExecutionAnimSet> classMap) {
        this.weaponCategoryMap = weaponCategoryMap;
        this.classMap = classMap;
    }

    public void registerCategory(WeaponCategory category,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.weaponCategoryMap.put(category, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classMap.put(itemClass, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public Map<WeaponCategory, ExecutionAnimSet> getWeaponCategoryMap() {
        return weaponCategoryMap;
    }

    public Map<Class<? extends Item>, ExecutionAnimSet> getClassMap() {
        return classMap;
    }
}