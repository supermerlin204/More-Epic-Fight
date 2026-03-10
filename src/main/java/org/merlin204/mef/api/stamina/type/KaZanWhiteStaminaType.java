package org.merlin204.mef.api.stamina.type;

import net.minecraft.world.damagesource.DamageSource;
import org.merlin204.mef.api.animation.property.MEFAnimationProperty;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class KaZanWhiteStaminaType extends StaminaType {
    public KaZanWhiteStaminaType(float defaultMax, float defaultRegen) {
        super(defaultMax, defaultRegen);
    }


    @Override
    public float beExecutedDamageModifier(MEFEntity mefEntity, DamageSource damageSource, float amount) {
        return mefEntity.getOriginal().getMaxHealth() * 0.15F + amount;
    }

    @Override
    public void whenHurt(MEFEntity mefEntity, DamageSource damageSource, float amount) {
        super.whenHurt(mefEntity, damageSource, amount);
        if (damageSource instanceof EpicFightDamageSource epicFightDamageSource){
            //处决动画的攻击不扣除耐力
            if (epicFightDamageSource.getAnimation().get().getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).isPresent() && epicFightDamageSource.getAnimation().get().getProperty(MEFAnimationProperty.IS_EXECUTE_ANIMATION).get()){
                return;
            }
            mefEntity.setStamina(mefEntity.getStamina() - epicFightDamageSource.getBaseImpact());
        }
        mefEntity.setStamina(mefEntity.getStamina() - amount*0.3F);
    }

    @Override
    public boolean canRecover(MEFEntity mefEntity) {
        return mefEntity.getOriginal().hurtTime == 0;
    }

    @Override
    public void whenKnockDownEnd(MEFEntity mefEntity) {
        super.whenKnockDownEnd(mefEntity);
        mefEntity.setStamina(mefEntity.getStaminaMax());
    }

    @Override
    public void whenBlock(MEFEntity mefEntity, float damage, DamageSource source) {
        super.whenBlock(mefEntity, damage, source);
        mefEntity.setStamina(mefEntity.getStamina() - damage*0.5F);
    }

    @Override
    public void whenBeBlocked(MEFEntity mefEntity, float damage, DamageSource damageSource) {
        super.whenBeBlocked(mefEntity, damage, damageSource);
        mefEntity.setStamina(mefEntity.getStamina() - damage*0.5F);
    }

    @Override
    public void whenZero(MEFEntity mefEntity) {
        super.whenZero(mefEntity);
        MEFEntityAPI.beKnockdown(mefEntity.getOriginal());
    }

    @Override
    public void whenBeParried(MEFEntity mefEntity) {
        super.whenBeParried(mefEntity);
        mefEntity.setStamina(mefEntity.getStamina() - mefEntity.getStaminaMax()*0.5F);
    }
}
