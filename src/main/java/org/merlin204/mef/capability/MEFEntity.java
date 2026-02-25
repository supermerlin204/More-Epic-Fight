package org.merlin204.mef.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.stamina.StaminaType;

public class MEFEntity {

    public static final MEFEntity EMPTY_MEF_ENTITY = new MEFEntity();

    private LivingEntity original;
    private StaminaType staminaType;

    public boolean staminaIsPresent(){
        return staminaType != null;
    }

    public void onConstruct(LivingEntity entity){
        original = entity;
    }

    public void saveNBTData(CompoundTag tag) {
    }

    public void loadNBTData(CompoundTag tag) {
    }

    public void tick() {

    }

}