package org.merlin204.mef.mixin.epicfight;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.client.render.MEFRenderTypes;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.merlin204.mef.registry.MEFMobEffects;
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
import yesman.epicfight.world.damagesource.StunType;

import static yesman.epicfight.client.gui.EntityUI.drawUIAsLevelModel;


@Mixin(HealthBar.class)
public class HealthBarMixin {

    @Unique
    private static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/bar.png");
    @Unique
    private static final ResourceLocation HEALTH = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/health.png");
    @Unique
    private static final ResourceLocation STAMINA = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/stamina.png");
    @Unique
    private static final ResourceLocation EXECUTE = ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID,"textures/gui/execute.png");

    @Inject(method = "shouldDraw", at = @At("HEAD"), remap = false, cancellable = true)
    public void mef$shouldDraw(LivingEntity entity, LivingEntityPatch<?> entityPatch, LocalPlayerPatch playerPatch, float partialTicks, CallbackInfoReturnable<Boolean> cir) {
        if (MEFEntityAPI.getStaminaTypeByEntity(entity) != null && MEFEntityAPI.getStaminaTypeByEntity(entity).getBarRenderType() == StaminaType.BarRenderType.SMALL){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
    public void mef$draw(LivingEntity entity, LivingEntityPatch<?> entityPatch, LocalPlayerPatch playerPatch, PoseStack poseStack, MultiBufferSource buffers, float partialTicks, CallbackInfo ci) {
        if (MEFEntityAPI.getStaminaTypeByEntity(entity) != null){
            ci.cancel();

            float scale = 2F * entity.getBbWidth();

            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);

            //绘制处决图标
            if (entity == playerPatch.getTarget() && MEFEntityAPI.canExecute(playerPatch) && entity.isAlive()){
                float rio = mefEntity.getKnockdownTime()/100F;

                if (entityPatch != null && entityPatch.getHitAnimation(StunType.KNOCKDOWN) != null && entityPatch.getAnimator().getPlayerFor(null).getRealAnimation() == entityPatch.getHitAnimation(StunType.KNOCKDOWN)){
                    rio = entityPatch.getAnimator().getPlayerFor(null).getElapsedTime()/ entityPatch.getHitAnimation(StunType.KNOCKDOWN).get().getTotalTime();
                }
                rio = Mth.clamp(rio,0F,1F);
                Matrix4f matrix4f = ((EntityUI) (Object) this).getModelViewMatrixAlignedToCamera(
                        poseStack, entity, 0.0F, entity.getBbHeight()/2, 0.0F, true, partialTicks
                );
                int step = (int) (59F*rio);

                float size = 0.2F * scale;

                mef$draw(matrix4f, EXECUTE, buffers,
                        -size, -size, size, size,
                        step/60F, 0.0F, (step+1)/60F, 1.0F);


            }

            if (mefEntity.getStaminaType().getBarRenderType() != StaminaType.BarRenderType.SMALL)return;

            Matrix4f matrix = ((EntityUI) (Object) this).getModelViewMatrixAlignedToCamera(
                    poseStack, entity, 0.0F, entity.getBbHeight() + 0.5F, 0.0F, true, partialTicks
            );


            float maxHealth = entity.getMaxHealth();
            float health = entity.getHealth();
            float healthRatio = Mth.clamp(health / maxHealth, 0.0F, 1.0F);

            float maxStamina = mefEntity.getStaminaMax();
            float stamina = mefEntity.getStamina();
            float staminaRatio = Mth.clamp(stamina / maxStamina, 0.0F, 1.0F);



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


    @Unique
    private static void mef$draw(Matrix4f matrix, ResourceLocation textureLocation, MultiBufferSource buffer, float minX, float minY, float maxX, float maxY, float minU, float minV, float maxU, float maxV) {
        VertexConsumer vertexConsumer = buffer.getBuffer(MEFRenderTypes.alwaysOnTop(textureLocation));
        vertexConsumer.vertex(matrix, minX, minY, 0.0F).uv(minU, maxV).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, 0.0F).uv(maxU, maxV).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, 0.0F).uv(maxU, minV).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, 0.0F).uv(minU, minV).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
    }



}
