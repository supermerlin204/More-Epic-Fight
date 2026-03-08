package org.merlin204.mef.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mef.capability.MEFEntity;

@OnlyIn(Dist.CLIENT)
public abstract class BossBarRenderer {

    /**
     * 核心渲染方法，子类必须实现或继承默认实现
     * @param guiGraphics 渲染绘图对象
     * @param bossEvent 原版 BossBar事件数据（包含血量比例等）
     * @param boss Boss 实体本身
     * @param mefEntity MEF 的实体Cap数据（包含耐力值等）
     * @param isKnockdown 是否处于可处决状态
     * @param y 渲染的 Y 轴基础坐标
     */
    public abstract void render(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, LivingEntity boss, MEFEntity mefEntity, boolean isKnockdown, int y);

    /**
     * 供子类调用的通用名字居中绘制方法
     */
    protected void drawName(Component name, GuiGraphics guiGraphics, int y, int nameOffsetY) {
        Font font = Minecraft.getInstance().font;
        Window window = Minecraft.getInstance().getWindow();

        int nameWidth = font.width(name);
        int nameX = (window.getGuiScaledWidth() - nameWidth) / 2;
        int nameY = y - nameOffsetY;

        guiGraphics.drawString(font, name, nameX, nameY, 0xFFFFFFFF, true);
    }
}