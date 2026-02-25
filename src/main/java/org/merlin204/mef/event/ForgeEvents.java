package org.merlin204.mef.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class ForgeEvents {


    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {

         for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
             StaminaType staminaType = MEFEntityAPI.getStaminaTypeByEntityType(entityType);
             if (staminaType != null){
                 if (!event.has(entityType, EpicFightAttributes.MAX_STAMINA.get())) {
                     event.add(entityType, EpicFightAttributes.MAX_STAMINA.get(), staminaType.getDefaultMax());
                 }
                 if (!event.has(entityType, EpicFightAttributes.STAMINA_REGEN.get())) {
                     event.add(entityType, EpicFightAttributes.STAMINA_REGEN.get(), staminaType.getDefaultRegen());
                 }
             }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void hurtEvent(LivingAttackEvent event) {




    }

}
