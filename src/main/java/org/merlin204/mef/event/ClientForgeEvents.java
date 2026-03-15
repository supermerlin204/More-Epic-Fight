package org.merlin204.mef.event;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.client.gui.ExecuteIconRenderCommand;
import org.merlin204.mef.client.gui.ExecuteIconRenderer;
import org.merlin204.mef.client.gui.MEFBossBarManager;
import org.merlin204.mef.client.render.MEFRenderTypes;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;


@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if(MEFBossBarManager.renderBossBar(event.getGuiGraphics(), event.getBossEvent(), event.getY())){
            event.setCanceled(true);
        }
    }

    private static final ResourceLocation EXECUTE = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "textures/gui/execute.png");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        // 获取全局的 BufferSource
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        // 遍历所有渲染指令
        for (ExecuteIconRenderCommand cmd : ExecuteIconRenderer.getCommands()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(MEFRenderTypes.alwaysOnTop(cmd.texture));

            // 绘制四边形（注意顶点顺序与原来一致）
            vertexConsumer.vertex(cmd.matrix, cmd.minX, cmd.minY, 0.0F).uv(cmd.minU, cmd.maxV).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
            vertexConsumer.vertex(cmd.matrix, cmd.maxX, cmd.minY, 0.0F).uv(cmd.maxU, cmd.maxV).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
            vertexConsumer.vertex(cmd.matrix, cmd.maxX, cmd.maxY, 0.0F).uv(cmd.maxU, cmd.minV).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
            vertexConsumer.vertex(cmd.matrix, cmd.minX, cmd.maxY, 0.0F).uv(cmd.minU, cmd.minV).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
        }

        // 提交自定义渲染类型的批次
        bufferSource.endBatch(MEFRenderTypes.alwaysOnTop(EXECUTE)); // 注意：你需要引用 EXECUTE 纹理，或者从 cmd 中获取，但纹理是固定的，这里直接使用常量

        // 清空指令列表，准备下一帧
        ExecuteIconRenderer.clear();
    }

}
