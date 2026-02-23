package org.merlin204.mef.client.keymapping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.animation.entity.MEFPlayerAPI;
import org.merlin204.mef.epicfight.MEFAnimations;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, value = {Dist.CLIENT})
public class ClientInputManager {

    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (event.phase == TickEvent.Phase.END) {

            LocalPlayerPatch patch = EpicFightCapabilities.getLocalPlayerPatch(player);
            if (patch == null)return;


            while (MEFKeyMappings.PARRY.consumeClick()) {
                MEFPlayerAPI.tryPlayParryAnimation(patch);
            }


        }
    }

}
