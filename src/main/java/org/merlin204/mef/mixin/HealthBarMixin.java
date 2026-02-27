package org.merlin204.mef.mixin;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.gui.EntityUI;
import yesman.epicfight.client.gui.HealthBar;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import static yesman.epicfight.client.gui.EntityUI.drawUIAsLevelModel;


@Mixin(HealthBar.class)
public class HealthBarMixin {

    @Unique
    private static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/bar.png");
    @Unique
    private static final ResourceLocation HEALTH = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/health.png");
    @Unique
    private static final ResourceLocation STAMINA = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/stamina.png");

    @Inject(method = "shouldDraw", at = @At("HEAD"), remap = false, cancellable = true)
    public void mef$shouldDraw(LivingEntity entity, LivingEntityPatch<?> entityPatch, LocalPlayerPatch playerPatch, float partialTicks, CallbackInfoReturnable<Boolean> cir) {
        if (MEFEntityAPI.getStaminaTypeByEntityType(entity.getType()) != null){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
    public void mef$draw(LivingEntity entity, LivingEntityPatch<?> entityPatch, LocalPlayerPatch playerPatch, PoseStack poseStack, MultiBufferSource buffers, float partialTicks, CallbackInfo ci) {
        if (MEFEntityAPI.getStaminaTypeByEntityType(entity.getType()) != null){
            ci.cancel();

            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);

            Matrix4f matrix = ((EntityUI) (Object) this).getModelViewMatrixAlignedToCamera(
                    poseStack, entity, 0.0F, entity.getBbHeight() + 0.5F, 0.0F, true, partialTicks
            );

            // 生命比例
            float maxHealth = entity.getMaxHealth();
            float health = entity.getHealth();
            float healthRatio = Mth.clamp(health / maxHealth, 0.0F, 1.0F);

            float maxStamina = mefEntity.getStaminaMax();
            float stamina = mefEntity.getStamina();
            float staminaRatio = Mth.clamp(stamina / maxStamina, 0.0F, 1.0F);

            float scale = 2F * entity.getBbWidth();

            float bgWidth = 1.0f * scale;
            float bgHalf = bgWidth * 0.5f;
            float y1 = -0.048828125f * scale; // 下边界
            float y2 = 0.048828125f * scale;  // 上边界

            //计算居中位置
            float fgMaxWidth = (126f / 128f) * scale;
            float fgLeft = -bgHalf + (bgWidth - fgMaxWidth) * 0.5f; // 血条左边界
            float fgRightFull = fgLeft + fgMaxWidth;                // 满血右边界

            // 绘制背景
            drawUIAsLevelModel(matrix, BAR, buffers,
                    -bgHalf, y1, bgHalf, y2,
                    0.0F, 0.0F, 1.0F, 1.0F);

            // 绘制血量
            if (healthRatio > 0.0F) {
                float fgRight = fgLeft + fgMaxWidth * healthRatio;
                drawUIAsLevelModel(matrix, HEALTH, buffers,
                        fgLeft, y1, fgRight, y2,
                        0.0F, 0.0F, healthRatio, 1.0F);
            }
            fgMaxWidth = (120f / 128f) * scale;
            fgLeft = -bgHalf + (bgWidth - fgMaxWidth) * 0.5f;
            fgRightFull = fgLeft + fgMaxWidth;

            // 绘制耐力
            if (staminaRatio > 0.0F) {
                float fgRight = fgLeft + fgMaxWidth * staminaRatio;
                drawUIAsLevelModel(matrix, STAMINA, buffers,
                        fgLeft, y1, fgRight, y2,
                        0.0F, 0.0F, staminaRatio, 1.0F);
            }

        }
    }






}
