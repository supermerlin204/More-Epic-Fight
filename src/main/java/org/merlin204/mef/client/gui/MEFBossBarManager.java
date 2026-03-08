package org.merlin204.mef.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.main.MoreEpicFightMod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MEFBossBarManager {
    public static final Map<UUID, Integer> BOSSES = new HashMap<>();
    public static final Map<Integer, ServerBossEvent> BOSS_EVENT_MAP = new HashMap<>();

    public static final Map<EntityType<?>, BossBarRenderer> RENDERER_REGISTRY = new HashMap<>();

    public static final BossBarRenderer DEFAULT_RENDERER = new DefaultBossBarRenderer(
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/health_bar.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/health.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/stamina_bar.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/stamina.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/knockdown.png"),
            250, 17, 244, 17, 3, 0,
            250, 17, 238, 17, 0, 6, 0,
            9
    );

    @OnlyIn(Dist.CLIENT)
    public static boolean renderBossBar(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, int y) {
        if (BOSSES.isEmpty()) return false;

        Entity entity = null;
        if (BOSSES.containsKey(bossEvent.getId()) && Minecraft.getInstance().level != null) {
            entity = Minecraft.getInstance().level.getEntity(BOSSES.get(bossEvent.getId()));
        }

        if (!(entity instanceof LivingEntity boss)) return false;

        StaminaType staminaType = MEFEntityAPI.getStaminaTypeByEntity(boss);
        if (staminaType == null) return false;

        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(boss);
        boolean isKnockdown = MEFEntityAPI.canBeExecute(boss);

        BossBarRenderer renderer = RENDERER_REGISTRY.getOrDefault(boss.getType(), DEFAULT_RENDERER);

        renderer.render(guiGraphics, bossEvent, boss, mefEntity, isKnockdown, y);

        return true;
    }
}