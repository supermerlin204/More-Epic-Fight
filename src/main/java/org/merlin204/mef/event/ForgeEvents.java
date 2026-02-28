package org.merlin204.mef.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.jar.EmbeddedJarCopier;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class ForgeEvents {


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void hurtEvent(LivingAttackEvent event) {




    }

}
