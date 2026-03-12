package org.merlin204.mef.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.main.MoreEpicFightMod;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OriginalSkillEvents {

    private static final ResourceLocation CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "original_skill_memory");

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(OriginalSkillCapability.INSTANCE).isPresent()) {
                event.addCapability(CAPABILITY_ID, new OriginalSkillCapability.Provider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        oldPlayer.reviveCaps();

        oldPlayer.getCapability(OriginalSkillCapability.INSTANCE).ifPresent(oldCap -> {
            CompoundTag oldData = oldCap.serializeNBT();

            if (!oldData.isEmpty()) {
                newPlayer.getCapability(OriginalSkillCapability.INSTANCE).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldData);
                });
            }
        });

        oldPlayer.invalidateCaps();
    }

    @Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(OriginalSkillCapability.IOriginalSkillMemory.class);
        }
    }
}