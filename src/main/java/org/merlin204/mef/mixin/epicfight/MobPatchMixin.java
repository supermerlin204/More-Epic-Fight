package org.merlin204.mef.mixin.epicfight;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import org.merlin204.mef.epicfight.IMEFPatch;
import org.merlin204.mef.world.entity.ai.goal.PatchEntityWonderGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@Mixin(value = MobPatch.class,remap = false)
public class MobPatchMixin {

    @Inject(at = @At(value = "TAIL"), method = "onJoinWorld(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraftforge/event/entity/EntityJoinLevelEvent;)V")
    protected void mef$onJoinWorld(LivingEntity par1, EntityJoinLevelEvent par2, CallbackInfo ci) {
        MobPatch<?> mobPatch = (MobPatch<?>) (Object)this;
        if (this instanceof IMEFPatch){
            mobPatch.getOriginal().goalSelector.addGoal(0,new PatchEntityWonderGoal(mobPatch));
        }

    }

}
