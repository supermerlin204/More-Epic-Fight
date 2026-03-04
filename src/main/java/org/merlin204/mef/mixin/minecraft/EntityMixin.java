package org.merlin204.mef.mixin.minecraft;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.client.gui.MEFBossBarManager;
import org.merlin204.mef.world.entity.ai.attribute.MEFAttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mixin(value = Entity.class)
public class EntityMixin {

    @Inject(at = @At(value = "TAIL"), method = "discard")
    protected void mef$discard(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if(MEFEntityAPI.getStaminaTypeByEntity(entity) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);
            if (mefEntity.staminaIsPresent() && mefEntity.getStaminaType().getBarRenderType() == StaminaType.BarRenderType.BOSS){
                ServerBossEvent bossInfo = MEFBossBarManager.BOSS_EVENT_MAP.get(entity.getId());
                if (bossInfo != null){
                    MEFBossBarManager.BOSS_EVENT_MAP.remove(entity.getId());
                }
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "tick")
    protected void mef$tick(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if(MEFEntityAPI.getStaminaTypeByEntity(entity) != null && entity instanceof LivingEntity livingEntity){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);
            if (mefEntity.staminaIsPresent() && mefEntity.getStaminaType().getBarRenderType() == StaminaType.BarRenderType.BOSS){
                ServerBossEvent bossInfo = MEFBossBarManager.BOSS_EVENT_MAP.get(entity.getId());
                if (bossInfo != null){
                    bossInfo.setProgress(livingEntity.getHealth() / livingEntity.getMaxHealth());
                }
            }
        }
    }


    @Inject(at = @At(value = "TAIL"), method = "startSeenByPlayer")
    protected void mef$startSeenByPlayer(ServerPlayer player, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if(MEFEntityAPI.getStaminaTypeByEntity(entity) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);
            if (mefEntity.staminaIsPresent() && mefEntity.getStaminaType().getBarRenderType() == StaminaType.BarRenderType.BOSS){
                ServerBossEvent bossInfo = MEFBossBarManager.BOSS_EVENT_MAP.get(entity.getId());
                if (bossInfo != null){
                    bossInfo.addPlayer(player);
                }
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "stopSeenByPlayer")
    protected void mef$stopSeenByPlayer(ServerPlayer player, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        if(MEFEntityAPI.getStaminaTypeByEntity(entity) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);
            if (mefEntity.getStaminaType().getBarRenderType() == StaminaType.BarRenderType.BOSS){
                ServerBossEvent bossInfo = MEFBossBarManager.BOSS_EVENT_MAP.get(entity.getId());
                if (bossInfo != null){
                    bossInfo.removePlayer(player);
                }
            }
        }
    }

}
