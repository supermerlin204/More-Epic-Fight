package org.merlin204.mef.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    private static final Map<EntityType<?>, BossBarStyle> STYLE_REGISTRY = new HashMap<>();

    public static final BossBarStyle DEFAULT_STYLE = new BossBarStyle(
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/health_bar.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/health.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/stamina_bar.png"),
            ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/boss/stamina.png"),
            250, 17, 244, 17,
            3, 0,
            250, 17, 238, 17,
            0,
            6, 0,
            9
    );

    public static void registerStyle(EntityType<?> entityType, BossBarStyle style) {
        STYLE_REGISTRY.put(entityType, style);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderBossBar(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, int y) {
        if (BOSSES.isEmpty()) return false;

        Entity boss = null;
        if (BOSSES.containsKey(bossEvent.getId()) && Minecraft.getInstance().level != null) {
            boss = Minecraft.getInstance().level.getEntity(BOSSES.get(bossEvent.getId()));
        }

        if (boss == null) return false;

        StaminaType staminaType = MEFEntityAPI.getStaminaTypeByEntity(boss);
        if (staminaType == null) return false;

        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(boss);

        BossBarStyle style = STYLE_REGISTRY.getOrDefault(boss.getType(), DEFAULT_STYLE);

        Window window = Minecraft.getInstance().getWindow();
        float rio = mefEntity.getStamina() / mefEntity.getStaminaMax();

        int staminaBgY = y + style.staminaBgOffsetY;
        int staminaBgX = (window.getGuiScaledWidth() - style.staminaBgWidth) / 2;

        if (style.staminaBgTex != null) {
            guiGraphics.blit(style.staminaBgTex, staminaBgX, staminaBgY, style.staminaBgWidth, style.staminaBgHeight, 0, 0, style.staminaBgWidth, style.staminaBgHeight, style.staminaBgWidth, style.staminaBgHeight);
        }

        int currentStaminaWidth = Mth.lerpInt(rio, 0, style.staminaMaxWidth);
        guiGraphics.blit(style.staminaFgTex, staminaBgX + style.staminaOffsetX, staminaBgY + style.staminaOffsetY, currentStaminaWidth, style.staminaFgHeight, 0, 0, currentStaminaWidth, style.staminaFgHeight, style.staminaMaxWidth, style.staminaFgHeight);

        int healthBgX = (window.getGuiScaledWidth() - style.healthBgWidth) / 2;

        if (style.healthBgTex != null) {
            guiGraphics.blit(style.healthBgTex, healthBgX, y, style.healthBgWidth, style.healthBgHeight, 0, 0, style.healthBgWidth, style.healthBgHeight, style.healthBgWidth, style.healthBgHeight);
        }

        int currentHealthWidth = Mth.lerpInt(bossEvent.getProgress(), 0, style.healthMaxWidth);
        guiGraphics.blit(style.healthFgTex, healthBgX + style.healthOffsetX, y + style.healthOffsetY, currentHealthWidth, style.healthFgHeight, 0, 0, currentHealthWidth, style.healthFgHeight, style.healthMaxWidth, style.healthFgHeight);

        drawName(boss.getName(), guiGraphics, y, style.nameOffsetY);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawName(Component name, GuiGraphics guiGraphics, int y, int nameOffsetY) {
        Font font = Minecraft.getInstance().font;
        Window window = Minecraft.getInstance().getWindow();

        int nameWidth = font.width(name);
        int nameX = (window.getGuiScaledWidth() - nameWidth) / 2;
        int nameY = y - nameOffsetY;

        guiGraphics.drawString(font, name, nameX, nameY, 0xFFFFFFFF, true);
    }

    public static class BossBarStyle {
        public final ResourceLocation healthBgTex;
        public final ResourceLocation healthFgTex;
        public final ResourceLocation staminaBgTex;
        public final ResourceLocation staminaFgTex;

        public final int healthBgWidth;
        public final int healthBgHeight;
        public final int healthMaxWidth;
        public final int healthFgHeight;

        public final int healthOffsetX;
        public final int healthOffsetY;

        public final int staminaBgWidth;
        public final int staminaBgHeight;
        public final int staminaMaxWidth;
        public final int staminaFgHeight;

        public final int staminaBgOffsetY;
        public final int staminaOffsetX;
        public final int staminaOffsetY;

        public final int nameOffsetY;

        public BossBarStyle(ResourceLocation healthBgTex, ResourceLocation healthFgTex,
                            ResourceLocation staminaBgTex, ResourceLocation staminaFgTex,

                            int healthBgWidth, int healthBgHeight, int healthMaxWidth, int healthFgHeight,
                            int healthOffsetX, int healthOffsetY,

                            int staminaBgWidth, int staminaBgHeight, int staminaMaxWidth, int staminaFgHeight,
                            int staminaBgOffsetY, int staminaOffsetX, int staminaOffsetY,

                            int nameOffsetY) {

            this.healthBgTex = healthBgTex;
            this.healthFgTex = healthFgTex;
            this.staminaBgTex = staminaBgTex;
            this.staminaFgTex = staminaFgTex;

            this.healthBgWidth = healthBgWidth;
            this.healthBgHeight = healthBgHeight;
            this.healthMaxWidth = healthMaxWidth;
            this.healthFgHeight = healthFgHeight;

            this.healthOffsetX = healthOffsetX;
            this.healthOffsetY = healthOffsetY;

            this.staminaBgWidth = staminaBgWidth;
            this.staminaBgHeight = staminaBgHeight;
            this.staminaMaxWidth = staminaMaxWidth;
            this.staminaFgHeight = staminaFgHeight;

            this.staminaBgOffsetY = staminaBgOffsetY;
            this.staminaOffsetX = staminaOffsetX;
            this.staminaOffsetY = staminaOffsetY;

            this.nameOffsetY = nameOffsetY;
        }
    }
}