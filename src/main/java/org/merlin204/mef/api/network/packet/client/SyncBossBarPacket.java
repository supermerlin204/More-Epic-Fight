package org.merlin204.mef.api.network.packet.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.merlin204.mef.api.network.packet.BasePacket;
import org.merlin204.mef.client.gui.MEFBossBarManager;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 同步客户端的uuid
 */
public record SyncBossBarPacket(UUID serverUuid, int id) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(serverUuid);
        buf.writeInt(id);
    }

    public static SyncBossBarPacket decode(FriendlyByteBuf buf) {
        return new SyncBossBarPacket(buf.readUUID(), buf.readInt());
    }

    @Override
    public void execute(@Nullable Player player) {
        MEFBossBarManager.BOSSES.put(serverUuid, id);
    }
}