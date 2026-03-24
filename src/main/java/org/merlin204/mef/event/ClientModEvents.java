package org.merlin204.mef.event;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.merlin204.mef.client.ponder.MEFPonderPlugin;
import org.merlin204.mef.main.MoreEpicFightMod;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
       event.enqueueWork(() -> {
           PonderIndex.addPlugin(new MEFPonderPlugin());
       });
    }
}
