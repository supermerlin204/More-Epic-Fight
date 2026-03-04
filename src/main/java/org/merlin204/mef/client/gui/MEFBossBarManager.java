package org.merlin204.mef.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MEFBossBarManager {
    public static final Map<UUID, Integer> BOSSES = new HashMap<>();
    public static final Map<Integer, ServerBossEvent> BOSS_EVENT_MAP = new HashMap<>();
    private static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/boss/bar.png");
    private static final ResourceLocation HEALTH = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/boss/health.png");
    private static final ResourceLocation STAMINA = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/boss/stamina.png");

    @OnlyIn(Dist.CLIENT)
    public static boolean renderBossBar(GuiGraphics guiGraphics, LerpingBossEvent bossEvent,int y){
        Entity boss = null;
        if (BOSSES.isEmpty()) return false;

        if(BOSSES.containsKey(bossEvent.getId()) && Minecraft.getInstance().level != null){
            boss = Minecraft.getInstance().level.getEntity(BOSSES.get(bossEvent.getId()));
        }
        StaminaType staminaType = MEFEntityAPI.getStaminaTypeByEntity(boss);
        if (staminaType == null)return false;
        MEFEntity mefEntity = MEFCapabilities.getMEFEntity(boss);
        if (mefEntity == null)return false;
        Window window = Minecraft.getInstance().getWindow();
        int x = window.getGuiScaledWidth() / 2 - 128;
        float rio = mefEntity.getStamina()/mefEntity.getStaminaMax();

        guiGraphics.blit(BAR, x, y, 250, 17, 0, 0, 250, 17, 250, 17);

        guiGraphics.blit(HEALTH, x+3, y, Mth.lerpInt(bossEvent.getProgress(), 0, 244), 17, 0, 0, Mth.lerpInt(bossEvent.getProgress(),0, 244), 17, 244, 17);
        guiGraphics.blit(STAMINA, x+6, y, Mth.lerpInt(rio, 0, 238), 17, 0, 0, Mth.lerpInt(rio,0, 238), 17, 238, 17);
        drawName(boss.getName(),guiGraphics,y);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawName(Component name,GuiGraphics guiGraphics, int y) {
        Font font = Minecraft.getInstance().font;
        Window window = Minecraft.getInstance().getWindow();


        int nameWidth = font.width(name);

        int nameX =  (window.getGuiScaledWidth()-nameWidth) / 2;
        int nameY = y-9;

        guiGraphics.drawString(font, name, nameX, nameY, 0xFFFFFFFF, true);
    }





}
