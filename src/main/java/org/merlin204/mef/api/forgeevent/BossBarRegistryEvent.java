package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.merlin204.mef.client.gui.BossBarRenderer;

import java.util.Map;

public class BossBarRegistryEvent extends Event implements IModBusEvent {
    private final Map<EntityType<?>, BossBarRenderer> registryMap;

    public BossBarRegistryEvent(Map<EntityType<?>, BossBarRenderer> registryMap) {
        this.registryMap = registryMap;
    }

    public void register(EntityType<?> entityType, BossBarRenderer renderer) {
        this.registryMap.put(entityType, renderer);
    }
}