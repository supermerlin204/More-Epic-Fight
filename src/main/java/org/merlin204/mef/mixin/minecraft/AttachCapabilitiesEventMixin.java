package org.merlin204.mef.mixin.minecraft;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFEntityCapabilityProvider;
import org.merlin204.mef.epicfight.IMEFPatch;
import org.merlin204.mef.main.MoreEpicFightMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;

@Mixin(AttachCapabilitiesEvent.class)
public abstract class AttachCapabilitiesEventMixin<T> {


    @Shadow public abstract T getObject();

    @Shadow public abstract void addCapability(ResourceLocation key, ICapabilityProvider cap);

    /**
     * 在绑定Patch时检查一下是不是IMEFPatch,是的话添加一下MEFEntity
     */
    @Inject(method = "addCapability", at = @At("HEAD"), remap = false)
    private void mef$addCapability(ResourceLocation key, ICapabilityProvider cap, CallbackInfo ci) {
        if (this.getObject() instanceof LivingEntity livingEntity){
            if (cap instanceof EntityPatchProvider entityPatchProvider && entityPatchProvider.get() instanceof IMEFPatch imefPatch){
                this.addCapability(ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "more_epic_fight_entity"), new MEFEntityCapabilityProvider(livingEntity));
                MEFEntityAPI.putStaminaTypeByEntityType(livingEntity.getType(),imefPatch.getStaminaType());
            }
        }

    }


}