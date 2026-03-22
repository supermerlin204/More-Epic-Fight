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

    // 1. 通用处决 (基于武器，无视受击者)
    private final Map<WeaponCategory, ExecutionAnimSet> categoryGenericMap;
    private final Map<Class<? extends Item>, ExecutionAnimSet> classGenericMap;

    // 2. 基于骨架的处决 (武器 + 骨架)
    private final Map<WeaponCategory, Map<Armature, ExecutionAnimSet>> categoryArmatureMap;
    private final Map<Class<? extends Item>, Map<Armature, ExecutionAnimSet>> classArmatureMap;

    // 3. 基于具体实体的处决 (武器 + 实体)
    private final Map<WeaponCategory, Map<EntityType<?>, ExecutionAnimSet>> categoryEntityMap;
    private final Map<Class<? extends Item>, Map<EntityType<?>, ExecutionAnimSet>> classEntityMap;

    // 4. 全局受击者处决 (无视武器，只看受击者的骨架或实体类型)
    private final Map<Armature, ExecutionAnimSet> globalArmatureMap;
    private final Map<EntityType<?>, ExecutionAnimSet> globalEntityMap;

    public ExecuteAnimationRegistryEvent(
            Map<WeaponCategory, ExecutionAnimSet> categoryGenericMap,
            Map<Class<? extends Item>, ExecutionAnimSet> classGenericMap,
            Map<WeaponCategory, Map<Armature, ExecutionAnimSet>> categoryArmatureMap,
            Map<Class<? extends Item>, Map<Armature, ExecutionAnimSet>> classArmatureMap,
            Map<WeaponCategory, Map<EntityType<?>, ExecutionAnimSet>> categoryEntityMap,
            Map<Class<? extends Item>, Map<EntityType<?>, ExecutionAnimSet>> classEntityMap,
            Map<Armature, ExecutionAnimSet> globalArmatureMap,
            Map<EntityType<?>, ExecutionAnimSet> globalEntityMap) {
        this.categoryGenericMap = categoryGenericMap;
        this.classGenericMap = classGenericMap;
        this.categoryArmatureMap = categoryArmatureMap;
        this.classArmatureMap = classArmatureMap;
        this.categoryEntityMap = categoryEntityMap;
        this.classEntityMap = classEntityMap;
        this.globalArmatureMap = globalArmatureMap;
        this.globalEntityMap = globalEntityMap;
    }

    // --- 武器 + 任意受击者 ---
    public void registerCategory(WeaponCategory category,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryGenericMap.put(category, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classGenericMap.put(itemClass, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    // --- 武器 + 特定骨架 ---
    public void registerCategory(WeaponCategory category, Armature victimArmature,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryArmatureMap.computeIfAbsent(category, k -> Maps.newHashMap()).put(victimArmature, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass, Armature victimArmature,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classArmatureMap.computeIfAbsent(itemClass, k -> Maps.newHashMap()).put(victimArmature, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    // --- 武器 + 特定实体 ---
    public void registerCategory(WeaponCategory category, EntityType<?> victimType,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                 AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.categoryEntityMap.computeIfAbsent(category, k -> Maps.newHashMap()).put(victimType, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerItemClass(Class<? extends Item> itemClass, EntityType<?> victimType,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                                  AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.classEntityMap.computeIfAbsent(itemClass, k -> Maps.newHashMap()).put(victimType, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    // --- 任意武器 + 特定骨架/特定实体 ---
    public void registerGlobal(Armature victimArmature,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.globalArmatureMap.put(victimArmature, new ExecutionAnimSet(attackerAnim, victimAnim));
    }

    public void registerGlobal(EntityType<?> victimType,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> attackerAnim,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> victimAnim) {
        this.globalEntityMap.put(victimType, new ExecutionAnimSet(attackerAnim, victimAnim));
    }
}