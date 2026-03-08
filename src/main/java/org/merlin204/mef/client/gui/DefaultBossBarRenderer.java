package org.merlin204.mef.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mef.capability.MEFEntity;

@OnlyIn(Dist.CLIENT)
public class DefaultBossBarRenderer extends BossBarRenderer {

    public final ResourceLocation healthBgTex;
    public final ResourceLocation healthFgTex;
    public final ResourceLocation staminaBgTex;
    public final ResourceLocation staminaFgTex;
    public final ResourceLocation knockdownTex;

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

    public DefaultBossBarRenderer(ResourceLocation healthBgTex, ResourceLocation healthFgTex, ResourceLocation staminaBgTex, ResourceLocation staminaFgTex,
                                  int healthBgWidth, int healthBgHeight, int healthMaxWidth, int healthFgHeight, int healthOffsetX, int healthOffsetY,
                                  int staminaBgWidth, int staminaBgHeight, int staminaMaxWidth, int staminaFgHeight, int staminaBgOffsetY, int staminaOffsetX, int staminaOffsetY,
                                  int nameOffsetY) {
        this(healthBgTex, healthFgTex, staminaBgTex, staminaFgTex, null, healthBgWidth, healthBgHeight, healthMaxWidth, healthFgHeight, healthOffsetX, healthOffsetY, staminaBgWidth, staminaBgHeight, staminaMaxWidth, staminaFgHeight, staminaBgOffsetY, staminaOffsetX, staminaOffsetY, nameOffsetY);
    }

    public DefaultBossBarRenderer(ResourceLocation healthBgTex, ResourceLocation healthFgTex, ResourceLocation staminaBgTex, ResourceLocation staminaFgTex, ResourceLocation knockdownTex,
                                  int healthBgWidth, int healthBgHeight, int healthMaxWidth, int healthFgHeight, int healthOffsetX, int healthOffsetY,
                                  int staminaBgWidth, int staminaBgHeight, int staminaMaxWidth, int staminaFgHeight, int staminaBgOffsetY, int staminaOffsetX, int staminaOffsetY,
                                  int nameOffsetY) {
        this.healthBgTex = healthBgTex; this.healthFgTex = healthFgTex; this.staminaBgTex = staminaBgTex; this.staminaFgTex = staminaFgTex; this.knockdownTex = knockdownTex;
        this.healthBgWidth = healthBgWidth; this.healthBgHeight = healthBgHeight; this.healthMaxWidth = healthMaxWidth; this.healthFgHeight = healthFgHeight;
        this.healthOffsetX = healthOffsetX; this.healthOffsetY = healthOffsetY;
        this.staminaBgWidth = staminaBgWidth; this.staminaBgHeight = staminaBgHeight; this.staminaMaxWidth = staminaMaxWidth; this.staminaFgHeight = staminaFgHeight;
        this.staminaBgOffsetY = staminaBgOffsetY; this.staminaOffsetX = staminaOffsetX; this.staminaOffsetY = staminaOffsetY;
        this.nameOffsetY = nameOffsetY;
    }

    @Override
    public void render(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, LivingEntity boss, MEFEntity mefEntity, boolean isKnockdown, int y) {
        Window window = Minecraft.getInstance().getWindow();
        float rio = mefEntity.getStamina() / mefEntity.getStaminaMax();

        int staminaBgY = y + this.staminaBgOffsetY;
        int staminaBgX = (window.getGuiScaledWidth() - this.staminaBgWidth) / 2;

        if (this.staminaBgTex != null) {
            guiGraphics.blit(this.staminaBgTex, staminaBgX, staminaBgY, this.staminaBgWidth, this.staminaBgHeight, 0, 0, this.staminaBgWidth, this.staminaBgHeight, this.staminaBgWidth, this.staminaBgHeight);
        }

        if (isKnockdown && this.knockdownTex != null) {
            guiGraphics.blit(this.knockdownTex, staminaBgX + this.staminaOffsetX, staminaBgY + this.staminaOffsetY, this.staminaMaxWidth, this.staminaFgHeight, 0, 0, this.staminaMaxWidth, this.staminaFgHeight, this.staminaMaxWidth, this.staminaFgHeight);
        } else if (this.staminaFgTex != null) {
            int currentStaminaWidth = Mth.lerpInt(rio, 0, this.staminaMaxWidth);
            guiGraphics.blit(this.staminaFgTex, staminaBgX + this.staminaOffsetX, staminaBgY + this.staminaOffsetY, currentStaminaWidth, this.staminaFgHeight, 0, 0, currentStaminaWidth, this.staminaFgHeight, this.staminaMaxWidth, this.staminaFgHeight);
        }

        int healthBgX = (window.getGuiScaledWidth() - this.healthBgWidth) / 2;

        if (this.healthBgTex != null) {
            guiGraphics.blit(this.healthBgTex, healthBgX, y, this.healthBgWidth, this.healthBgHeight, 0, 0, this.healthBgWidth, this.healthBgHeight, this.healthBgWidth, this.healthBgHeight);
        }

        int currentHealthWidth = Mth.lerpInt(bossEvent.getProgress(), 0, this.healthMaxWidth);
        if (this.healthFgTex != null) {
            guiGraphics.blit(this.healthFgTex, healthBgX + this.healthOffsetX, y + this.healthOffsetY, currentHealthWidth, this.healthFgHeight, 0, 0, currentHealthWidth, this.healthFgHeight, this.healthMaxWidth, this.healthFgHeight);
        }

        drawName(boss.getName(), guiGraphics, y, this.nameOffsetY);
    }
}