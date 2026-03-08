package org.merlin204.mef.api.forgeevent;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class PlayerCanParryEvent extends Event implements IModBusEvent {
    private final PlayerPatch<?> playerPatch;
    private boolean canParry;

    public PlayerCanParryEvent(PlayerPatch<?> playerPatch, boolean baseResult) {
        this.playerPatch = playerPatch;
        this.canParry = baseResult;
    }

    public PlayerPatch<?> getPlayerPatch() { return playerPatch; }
    public boolean canParry() { return canParry; }
    public void setCanParry(boolean canParry) { this.canParry = canParry; }
}