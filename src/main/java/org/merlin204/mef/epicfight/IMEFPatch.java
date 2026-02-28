package org.merlin204.mef.epicfight;


import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

/**
 * 接入该接口时,MEF获取耐力类型会改为从方法获取,而不是通过注册
 */
public interface IMEFPatch {

    StaminaType getStaminaType();

    default MEFEntity getMEFEntity(){
        if (this instanceof LivingEntityPatch<?> livingEntityPatch){
            return MEFCapabilities.getMEFEntity(livingEntityPatch.getOriginal());
        }
        return null;
    }

    default float getStaminaMax(){
        return getMEFEntity().getStaminaMax();
    }

    default float getStaminaRegen(){
        return getMEFEntity().getStaminaRegen();
    }

    default float getStamina(){
        return getMEFEntity().getStamina();
    }

    default void setStamina(float amount){
        getMEFEntity().setStamina(amount);
    }

}
