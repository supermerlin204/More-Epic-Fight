package org.merlin204.mef.mixin;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.merlin204.mef.api.animation.defense.DefenseTimePair;
import org.merlin204.mef.api.animation.entity.MEFEntityAPI;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

@Mixin(EntityPatchProvider.class)
public class EntityPatchProviderMixin {


    /**
     * 在EF实体注册完Patch后进行MEF逻辑集的初始化
     */
    @Inject(method = "registerEntityPatches", at = @At("TAIL"),  remap = false)
    private static void registerEntityPatches(CallbackInfo ci) {
        MEFEntityAPI.init();
    }

    @Inject(method = "registerEntityPatchesClient", at = @At("TAIL"),  remap = false)
    private static void registerEntityPatchesClient(CallbackInfo ci) {
        MEFEntityAPI.init();
    }
}