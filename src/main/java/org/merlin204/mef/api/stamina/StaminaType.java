package org.merlin204.mef.api.stamina;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.capability.MEFEntity;

public abstract class StaminaType {
    private final float defaultMax;
    private final float defaultRegen;


    public StaminaType(float defaultMax, float defaultRegen) {
        this.defaultMax = defaultMax;
        this.defaultRegen = defaultRegen;
    }

    /**
     * 耐力值减小到0的时候
     */
    public void whenZero(MEFEntity mefEntity){

    }


    /**
     * 倒地结束的时候
     */
    public void whenKnockDownEnd(MEFEntity mefEntity){

    }

    /**
     * 受伤的时候
     */
    public void whenHurt(MEFEntity mefEntity, DamageSource damageSource, float amount){

    }

    /**
     * 格挡攻击的时候
     */
    public void whenBlock(MEFEntity mefEntity, float damage){

    }

    /**
     * 闪避攻击的时候
     */
    public void whenDodge(MEFEntity mefEntity, float damage){

    }

    /**
     * 被弹反的时候
     */
    public void whenBeParried(MEFEntity mefEntity){

    }

    /**
     * 攻击被格挡的时候
     */
    public void whenBeBlocked(MEFEntity mefEntity, float damage){

    }

    /**
     * 攻击被闪避的时候
     */
    public void whenBeDodged(MEFEntity mefEntity, float damage){

    }

    /**
     * 是否自动回复耐力
     */
    public boolean canRecover(MEFEntity mefEntity){
        return true;
    }

    /**
     * 默认的上限
     */
    public final float getDefaultMax() {
        return defaultMax;
    }


    /**
     * 默认的回复速度
     */
    public final float getDefaultRegen() {
        return defaultRegen;
    }


}
