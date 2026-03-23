package org.merlin204.mef.api.lockon;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.main.MoreEpicFightMod;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CameraFovUtil {

    private static boolean isActive = false;
    private static int currentTick = 0;

    private static int fadeInTicks = 0;
    private static int sustainTicks = 0;
    private static int fadeOutTicks = 0;
    private static float targetFovScale = 1.0F;

    /**
     * 触发带有三段式时间轴的电影级镜头推拉
     * @param fadeIn 镜头拉近所需的时间(Tick)
     * @param sustain 镜头保持拉近状态的时间(Tick)
     * @param fadeOut 镜头恢复正常所需的时间(Tick)
     * @param targetFovScale 目标FOV缩放倍率 (例如: 0.7F 为拉近， 1.3F 为拉远)
     */
    public static void triggerZoom(int fadeIn, int sustain, int fadeOut, float targetFovScale) {
        fadeInTicks = Math.max(0, fadeIn);
        sustainTicks = Math.max(0, sustain);
        fadeOutTicks = Math.max(0, fadeOut);
        CameraFovUtil.targetFovScale = targetFovScale;

        currentTick = 0;
        isActive = true;
    }

    public static void stopZoom() {
        isActive = false;
        currentTick = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && isActive) {
            currentTick++;

            if (currentTick >= fadeInTicks + sustainTicks + fadeOutTicks) {
                isActive = false;
            }
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        if (isActive) {
            float fovModifier;

            if (currentTick <= fadeInTicks) {
                float progress = fadeInTicks > 0 ? (float) currentTick / fadeInTicks : 1.0F;

                float ease = 0.5F - 0.5F * Mth.cos(progress * (float) Math.PI);
                fovModifier = Mth.lerp(ease, 1.0F, targetFovScale);

            } else if (currentTick <= fadeInTicks + sustainTicks) {
                fovModifier = targetFovScale;

            } else {
                int fadeOutStart = fadeInTicks + sustainTicks;
                float progress = fadeOutTicks > 0 ? (float) (currentTick - fadeOutStart) / fadeOutTicks : 1.0F;

                float ease = 0.5F - 0.5F * Mth.cos(progress * (float) Math.PI);
                fovModifier = Mth.lerp(ease, targetFovScale, 1.0F);
            }

            event.setNewFovModifier(event.getFovModifier() * fovModifier);
        }
    }
}