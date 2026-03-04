package org.merlin204.mef.api.stamina.type;

import net.minecraft.world.damagesource.DamageSource;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class SekiroStaminaType extends StaminaType {
    public SekiroStaminaType(float defaultMax, float defaultRegen) {
        super(defaultMax, defaultRegen);
    }

    @Override
    public float beExecutedDamageModifier(MEFEntity mefEntity, DamageSource damageSource, float amount) {
        if (mefEntity.getOriginal().getHealth() > mefEntity.getOriginal().getMaxHealth()*0.5F){
            return mefEntity.getOriginal().getMaxHealth()*0.5F;
        }
        return mefEntity.getOriginal().getMaxHealth() * 0.4F;
    }


    @Override
    public boolean canRecover(MEFEntity mefEntity) {
        return false;
    }

    @Override
    public void whenKnockDownEnd(MEFEntity mefEntity) {
        super.whenKnockDownEnd(mefEntity);
        mefEntity.setStamina(getDefaultMax() * 0.5F);
    }

    @Override
    public void whenBlock(MEFEntity mefEntity, float damage) {
        super.whenBlock(mefEntity, damage);
        mefEntity.setStamina(mefEntity.getStamina() - 10);
    }

    @Override
    public void whenBeBlocked(MEFEntity mefEntity, float damage) {
        super.whenBeBlocked(mefEntity, damage);
        mefEntity.setStamina(mefEntity.getStamina() - 5);
    }

    @Override
    public void whenZero(MEFEntity mefEntity) {
        super.whenZero(mefEntity);
        MEFEntityAPI.beKnockdown(mefEntity.getOriginal());
    }

}
