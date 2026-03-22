package org.merlin204.mef.api.forgeevent;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.api.execution.ExecutionAnimSet;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

public class ExecuteAnimationRegistryEvent extends Event implements IModBusEvent {

    // 1. 通用处决
    private final Map<WeaponCategory, ExecutionAnimSet> categoryGenericMap;
    private final Map<Class<? extends Item>, ExecutionAnimSet> classGenericMap;

    // 2. 基于骨架的处决
    private final Map<WeaponCategory, Map<Armature, ExecutionAnimSet>> categoryArmatureMap;
    private final Map<Class<? extends Item>, Map<Armature, ExecutionAnimSet>> classArmatureMap;

    // 3. 基于具体实体的处决
    private final Map<WeaponCategory, Map<EntityType<?>, ExecutionAnimSet>> categoryEntityMap;
    private final Map<Class<? extends Item>, Map<EntityType<?>, ExecutionAnimSet>> classEntityMap;

    public ExecuteAnimationRegistryEvent(
            Map<WeaponCategory, ExecutionAnimSet> categoryGenericMap,
            Map<Class<? extends Item>, ExecutionAnimSet> classGenericMap,
            Map<WeaponCategory, Map<Armature, ExecutionAnimSet>> categoryArmatureMap,
            Map<Class<? extends Item>, Map<Armature, ExecutionAnimSet>> classArmatureMap,
            Map<WeaponCategory, Map<EntityType<?>, ExecutionAnimSet>> categoryEntityMap,
            Map<Class<? extends Item>, Map<EntityType<?>, ExecutionAnimSet>> classEntityMap) {
        this.categoryGenericMap = categoryGenericMap;
        this.classGenericMap = classGenericMap;
        this.categoryArmatureMap = categoryArmatureMap;
        this.classArmatureMap = classArmatureMap;
        this.categoryEntityMap = categoryEntityMap;
        this.classEntityMap = classEntityMap;
    }

    public void registerCategory(WeaponCategory category,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryGenericMap.put(category, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerCategory(WeaponCategory category, Armature victimArmature,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryArmatureMap.computeIfAbsent(category, k -> Maps.newHashMap()).put(victimArmature, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerCategory(WeaponCategory category, EntityType<?> victimType,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryEntityMap.computeIfAbsent(category, k -> Maps.newHashMap()).put(victimType, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classGenericMap.put(itemClass, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass, Armature victimArmature,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classArmatureMap.computeIfAbsent(itemClass, k -> Maps.newHashMap()).put(victimArmature, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass, EntityType<?> victimType,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classEntityMap.computeIfAbsent(itemClass, k -> Maps.newHashMap()).put(victimType, new ExecutionAnimSet(attackerAnim, victimAnim));
    }
}