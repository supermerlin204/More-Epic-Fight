package org.merlin204.mef.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.function.Supplier;

public class CPReqExecute {
    private final int victimId;

    public CPReqExecute(int victimId) {
        this.victimId = victimId;
    }

    public CPReqExecute(FriendlyByteBuf buffer) {
        this.victimId = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.victimId);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Entity victimEntity = player.level().getEntity(this.victimId);
            if (victimEntity instanceof LivingEntity victim) {

                ServerPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
                LivingEntityPatch<?> victimPatch = EpicFightCapabilities.getEntityPatch(victim, LivingEntityPatch.class);

                if (playerPatch != null && victimPatch != null) {
                    if (MEFEntityAPI.canExecute(playerPatch, victim)) {

                        MEFEntityAPI.tryPlayExecuteAnimation(playerPatch, victimPatch);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}