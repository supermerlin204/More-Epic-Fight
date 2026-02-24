package org.merlin204.mef.main;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.merlin204.mef.registry.MEFMobEffects;


@Mod(MoreEpicFightMod.MOD_ID)
public class MoreEpicFightMod {


    public static final String MOD_ID = "more_epic_fight";


    public MoreEpicFightMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        MEFMobEffects.EFFECTS.register(bus);

    }


}
