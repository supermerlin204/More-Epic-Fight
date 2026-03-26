package org.merlin204.mef.mixin.epicfight;

import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class, remap = false)
public class AnimationPlayerMixin {

    @ModifyVariable(
            method = "tick",
            at = @At(value = "FIELD", target = "Lyesman/epicfight/api/animation/AnimationPlayer;elapsedTime:F", opcode = Opcodes.GETFIELD, ordinal = 2),
            ordinal = 0
    )
    private float mef$applyAnimationSpeed(float playbackSpeed, LivingEntityPatch<?> entityPatch) {
        if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null) {
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
            float speedMultiplier = mefEntity.getAnimationSpeed();
            if (speedMultiplier != 1.0F && speedMultiplier != 0) {
                return playbackSpeed * speedMultiplier;
            }
        }
        return playbackSpeed;
    }
}