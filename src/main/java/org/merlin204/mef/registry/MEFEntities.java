package org.merlin204.mef.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.merlin204.mef.entity.DummyPlayerEntity;
import org.merlin204.mef.main.MoreEpicFightMod;

public class MEFEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MoreEpicFightMod.MOD_ID);

    public static final RegistryObject<EntityType<DummyPlayerEntity>> DUMMY_PLAYER = ENTITIES.register("dummy_player",
            () -> EntityType.Builder.of(DummyPlayerEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("dummy_player"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}