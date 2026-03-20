package org.merlin204.mef.api.execution;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.forgeevent.ExecuteAnimationRegistryEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.util.Map;

public class MEFExecutionRegistry {

    private static final Map<WeaponCategory, ExecutionAnimSet> CATEGORY_EXECUTIONS = Maps.newHashMap();
    private static final Map<Class<? extends Item>, ExecutionAnimSet> CLASS_EXECUTIONS = Maps.newHashMap();

    public static void fireRegistryEvent() {
        ExecuteAnimationRegistryEvent event = new ExecuteAnimationRegistryEvent(CATEGORY_EXECUTIONS, CLASS_EXECUTIONS);
        ModLoader.get().postEvent(event);
    }

    @Nullable
    public static ExecutionAnimSet getExecutionSet(LivingEntity player) {
        Item mainHandItem = player.getMainHandItem().getItem();

        for (Map.Entry<Class<? extends Item>, ExecutionAnimSet> entry : CLASS_EXECUTIONS.entrySet()) {
            if (entry.getKey().isInstance(mainHandItem)) {
                return entry.getValue();
            }
        }

        var cap = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
        if (cap != null && CATEGORY_EXECUTIONS.containsKey(cap.getWeaponCategory())) {
            return CATEGORY_EXECUTIONS.get(cap.getWeaponCategory());
        }

        return null;
    }
}