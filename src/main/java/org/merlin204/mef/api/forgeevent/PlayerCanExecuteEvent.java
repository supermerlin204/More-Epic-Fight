package org.merlin204.mef.api.forgeevent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class PlayerCanExecuteEvent extends Event implements IModBusEvent {
    private final PlayerPatch<?> playerPatch;
    @Nullable
    private final LivingEntity target;
    private boolean canExecute;

    public PlayerCanExecuteEvent(PlayerPatch<?> playerPatch, @Nullable LivingEntity target, boolean baseResult) {
        this.playerPatch = playerPatch;
        this.target = target;
        this.canExecute = baseResult;
    }

    public PlayerPatch<?> getPlayerPatch() { return playerPatch; }
    @Nullable
    public LivingEntity getTarget() { return target; }
    public boolean canExecute() { return canExecute; }
    public void setCanExecute(boolean canExecute) { this.canExecute = canExecute; }
}