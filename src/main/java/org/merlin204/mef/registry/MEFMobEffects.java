package org.merlin204.mef.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.merlin204.mef.effect.MEFKnockdownEffect;
import org.merlin204.mef.effect.MEFStunEffect;
import org.merlin204.mef.main.MoreEpicFightMod;

public class MEFMobEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MoreEpicFightMod.MOD_ID);

    // 眩晕效果
    public static final RegistryObject<MobEffect> STUN =
            EFFECTS.register("stun", MEFStunEffect::new);

    // 倒地效果
    public static final RegistryObject<MobEffect> KNOCKDOWN =
            EFFECTS.register("knockdown", MEFKnockdownEffect::new);


}
