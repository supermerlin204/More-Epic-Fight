package org.merlin204.mef.capability;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.main.MoreEpicFightMod;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class MEFCapabilities {



    /**
     * 尝试获取MEFEntity
     */
    public static MEFEntity getMEFEntity(Entity entity){
        if (entity == null){
            return null;
        }
        return entity.getCapability(MEFEntityCapabilityProvider.MEF_ENTITY).orElse(MEFEntity.EMPTY_MEF_ENTITY);
    }



    /**
     * 为绑定了耐力类型的实体绑定MEFEntity
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof LivingEntity livingEntity && MEFEntityAPI.getStaminaTypeByEntity(livingEntity) != null) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MoreEpicFightMod.MOD_ID, "more_epic_fight_entity"), new MEFEntityCapabilityProvider(livingEntity));
        }

    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(MEFEntity.class);
    }


}
