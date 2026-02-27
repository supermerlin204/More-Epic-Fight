package org.merlin204.mef.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class MEFEntity {

    public static final MEFEntity EMPTY_MEF_ENTITY = new MEFEntity();


    protected static EntityDataAccessor<Float> STAMINA;
    protected static EntityDataAccessor<Boolean> IS_WONDER;
    protected static EntityDataAccessor<Integer> WONDER_TIME;



    public static void initLivingEntityDataAccessor() {
        STAMINA = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
        IS_WONDER = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
        WONDER_TIME = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    }

    private LivingEntity original;
    private StaminaType staminaType;

    public static void createSyncedEntityData(LivingEntity livingentity) {
        livingentity.getEntityData().define(STAMINA, 0.0F);
        livingentity.getEntityData().define(IS_WONDER, false);
        livingentity.getEntityData().define(WONDER_TIME, 0);
    }

    public StaminaType getStaminaType() {
        return staminaType;
    }

    public boolean staminaIsPresent(){
        return staminaType != null;
    }

    public void onConstruct(LivingEntity entity){
        original = entity;
        staminaType = MEFEntityAPI.getStaminaTypeByEntityType(entity.getType());
    }

    public LivingEntity getOriginal() {
        return original;
    }

    public final float getStaminaMax(){
        return (float) getOriginal().getAttributeValue(EpicFightAttributes.MAX_STAMINA.get());
    }

    public final float getStaminaRegen(){
        return (float) getOriginal().getAttributeValue(EpicFightAttributes.STAMINA_REGEN.get());
    }

    public final float getStamina(){
        return getOriginal().getEntityData().get(STAMINA);
    }

    public final void setStamina(float amount){
        float targetAmount =  Mth.clamp(amount,0,getStaminaMax());
        if (getStamina() >= 0 && targetAmount ==0){
            getStaminaType().whenZero(this);
        }
        getOriginal().getEntityData().set(STAMINA,targetAmount);
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putFloat("mef_stamina",getStamina());
    }

    public void loadNBTData(CompoundTag tag) {
        if (tag.contains("mef_stamina")){
            setStamina(tag.getFloat("mef_stamina"));
        }
    }

    public final void tick() {
        if (original == null)return;
        if (!original.level().isClientSide && staminaIsPresent()){
            //莫要诧异,这是为了确保耐力一直有在同步
            this.setStamina(getStamina());
            if (staminaType.canRecover(this)){
                this.setStamina(getStamina() + getStaminaRegen());
            }

        }
    }

}