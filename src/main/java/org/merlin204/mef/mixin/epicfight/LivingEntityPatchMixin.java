package org.merlin204.mef.mixin.epicfight;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = LivingEntityPatch.class,remap = false)
public class LivingEntityPatchMixin {

    @Shadow
    protected Entity lastTryHurtEntity;



}
