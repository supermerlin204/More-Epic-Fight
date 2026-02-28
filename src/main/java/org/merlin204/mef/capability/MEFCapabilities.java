package org.merlin204.mef.capability;


import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.main.MoreEpicFightMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import javax.annotation.Nullable;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class MEFCapabilities {


    public static MEFEntity getMEFEntity(Entity entity){
        return entity.getCapability(MEFEntityCapabilityProvider.MEF_ENTITY).orElse(MEFEntity.EMPTY_MEF_ENTITY);
    }



    /**
     * 为绑定了耐力类型的实体绑定MEFEntity
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof LivingEntity livingEntity && MEFEntityAPI.getStaminaTypeByEntityType(livingEntity.getType()) != null) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "more_epic_fight_entity"), new MEFEntityCapabilityProvider(livingEntity));
        }

    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(MEFEntity.class);
    }


}
