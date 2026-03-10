package org.merlin204.mef.api.stamina.type;

import net.minecraft.world.damagesource.DamageSource;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFEntity;

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
    public void whenKnockDownEnd(MEFEntity mefEntity) {
        super.whenKnockDownEnd(mefEntity);
        mefEntity.setStamina(getDefaultMax() * 0.5F);
    }

    @Override
    public void whenBlock(MEFEntity mefEntity, float damage, DamageSource source) {
        super.whenBlock(mefEntity, damage, source);
        mefEntity.setStamina(mefEntity.getStamina());
    }

    @Override
    public void whenBeBlocked(MEFEntity mefEntity, float damage, DamageSource damageSource) {
        super.whenBeBlocked(mefEntity, damage, damageSource);
        mefEntity.setStamina(mefEntity.getStamina() - 5);
    }

    @Override
    public void whenZero(MEFEntity mefEntity) {
        super.whenZero(mefEntity);
        MEFEntityAPI.beKnockdown(mefEntity.getOriginal());
    }

}
