package org.merlin204.mef.mixin.epicfight.client;

import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.lockon.IEpicFightCameraAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;

@Mixin(value = EpicFightCameraAPI.class, remap = false)
public abstract class MixinEpicFightCameraAPI implements IEpicFightCameraAPI {

    @Shadow private LivingEntity focusingEntity;

    @Shadow private void sendTargeting(LivingEntity target) {}

    public void mef$forceSetFocusingEntity(LivingEntity target) {
        this.focusingEntity = target;
        this.sendTargeting(target);
    }
}