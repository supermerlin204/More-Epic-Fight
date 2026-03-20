package org.merlin204.mef.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.merlin204.mef.network.packet.CPReqExecute;

public class MEFNetworkManager {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.registerMessage(id(),
                CPReqExecute.class,
                CPReqExecute::toBytes,
                CPReqExecute::new,
                CPReqExecute::handle);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}