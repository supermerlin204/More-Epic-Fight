package org.merlin204.mef.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mef.main.MoreEpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mod.EventBusSubscriber(modid = MoreEpicFightMod.MOD_ID)
public class MEFEntityCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<MEFEntity> MEF_ENTITY = CapabilityManager.get(new CapabilityToken<>() {});

    private MEFEntity moreEpicFightEntity = null;
    private final LivingEntity entity;


    public MEFEntityCapabilityProvider(LivingEntity entity) {
        this.entity = entity;
    }

    private final LazyOptional<MEFEntity> optional = LazyOptional.of(this::createMEFEntity);

    private MEFEntity createMEFEntity() {
        if (moreEpicFightEntity == null) {
            moreEpicFightEntity = new MEFEntity();
            moreEpicFightEntity.onConstruct(entity); // 直接初始化
        }
        return moreEpicFightEntity;
    }


    public static MEFEntity get(LivingEntity entity){
        return entity.getCapability(MEF_ENTITY).orElse(MEFEntity.EMPTY_MEF_ENTITY);
    }

    public static MEFEntity get(LivingEntityPatch<?> patch){
        return get(patch.getOriginal());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == MEF_ENTITY){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createMEFEntity().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createMEFEntity().loadNBTData(tag);
    }

}
