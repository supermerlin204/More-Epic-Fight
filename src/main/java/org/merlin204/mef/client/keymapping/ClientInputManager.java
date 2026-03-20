package org.merlin204.mef.client.keymapping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.merlin204.mef.network.MEFNetworkManager;
import org.merlin204.mef.network.packet.CPReqExecute;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, value = {Dist.CLIENT})
public class ClientInputManager {

    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
        if (patch == null) return;

        while (MEFKeyMappings.PARRY.consumeClick()) {
            if (MEFEntityAPI.canParried(patch)) {
                MEFEntityAPI.tryPlayParryAnimation(patch);
            }
        }

        while (MEFKeyMappings.EXECUTE.consumeClick()) {
            LivingEntity victim = MEFEntityAPI.getNearbyExecutableEntity(patch);

            if (victim != null && MEFEntityAPI.canExecute(patch, victim)) {

                MEFNetworkManager.sendToServer(new CPReqExecute(victim.getId()));

            }
        }
    }
}