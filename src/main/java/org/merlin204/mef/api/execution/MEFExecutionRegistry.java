package org.merlin204.mef.api.execution;

import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.forgeevent.ExecuteAnimationRegistryEvent;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.util.Map;

public class MEFExecutionRegistry {

    private static final Map<WeaponCategory, ExecutionAnimSet> CATEGORY_GENERIC = Maps.newHashMap();
    private static final Map<Class<? extends Item>, ExecutionAnimSet> CLASS_GENERIC = Maps.newHashMap();

    private static final Map<WeaponCategory, Map<Armature, ExecutionAnimSet>> CATEGORY_ARMATURE = Maps.newHashMap();
    private static final Map<Class<? extends Item>, Map<Armature, ExecutionAnimSet>> CLASS_ARMATURE = Maps.newHashMap();

    private static final Map<WeaponCategory, Map<EntityType<?>, ExecutionAnimSet>> CATEGORY_ENTITY = Maps.newHashMap();
    private static final Map<Class<? extends Item>, Map<EntityType<?>, ExecutionAnimSet>> CLASS_ENTITY = Maps.newHashMap();

    public static void fireRegistryEvent() {
        ExecuteAnimationRegistryEvent event = new ExecuteAnimationRegistryEvent(
                CATEGORY_GENERIC, CLASS_GENERIC,
                CATEGORY_ARMATURE, CLASS_ARMATURE,
                CATEGORY_ENTITY, CLASS_ENTITY
        );
        ModLoader.get().postEvent(event);
    }

    @Nullable
    public static ExecutionAnimSet getExecutionSet(LivingEntityPatch<?> attackerPatch, @Nullable LivingEntityPatch<?> victimPatch) {
        if (attackerPatch == null) return null;

        CapabilityItem main = attackerPatch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND);
        Class<? extends Item> mainClass = attackerPatch.getAdvancedHoldingItemStack(InteractionHand.MAIN_HAND).getItem().getClass();
        WeaponCategory category = main != null ? main.getWeaponCategory() : null;

        EntityType<?> victimType = victimPatch != null ? victimPatch.getOriginal().getType() : null;
        Armature victimArmature = victimPatch != null ? victimPatch.getArmature() : null;

        if (victimType != null) {
            if (CLASS_ENTITY.containsKey(mainClass) && CLASS_ENTITY.get(mainClass).containsKey(victimType)) {
                return CLASS_ENTITY.get(mainClass).get(victimType);
            }
            if (category != null && CATEGORY_ENTITY.containsKey(category) && CATEGORY_ENTITY.get(category).containsKey(victimType)) {
                return CATEGORY_ENTITY.get(category).get(victimType);
            }
        }

        if (victimArmature != null) {
            if (CLASS_ARMATURE.containsKey(mainClass) && CLASS_ARMATURE.get(mainClass).containsKey(victimArmature)) {
                return CLASS_ARMATURE.get(mainClass).get(victimArmature);
            }
            if (category != null && CATEGORY_ARMATURE.containsKey(category) && CATEGORY_ARMATURE.get(category).containsKey(victimArmature)) {
                return CATEGORY_ARMATURE.get(category).get(victimArmature);
            }
        }

        if (CLASS_GENERIC.containsKey(mainClass)) {
            return CLASS_GENERIC.get(mainClass);
        }
        if (category != null && CATEGORY_GENERIC.containsKey(category)) {
            return CATEGORY_GENERIC.get(category);
        }

        return null;
    }
}