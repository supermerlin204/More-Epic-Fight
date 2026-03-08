package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class EntityCanBeExecutedEvent extends Event implements IModBusEvent {
    private final LivingEntity target;
    private boolean canBeExecuted;

    public EntityCanBeExecutedEvent(LivingEntity target, boolean baseResult) {
        this.target = target;
        this.canBeExecuted = baseResult;
    }

    public LivingEntity getTarget() { return target; }
    public boolean canBeExecuted() { return canBeExecuted; }
    public void setCanBeExecuted(boolean canBeExecuted) { this.canBeExecuted = canBeExecuted; }
}