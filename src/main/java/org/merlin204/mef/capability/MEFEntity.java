package org.merlin204.mef.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.epicfight.IMEFPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
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

        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(original,LivingEntityPatch.class);
        if (patch instanceof IMEFPatch imefPatch){
            return imefPatch.getStaminaType();
        }

        return staminaType;
    }

    public boolean staminaIsPresent(){
        return getStaminaType() != null;
    }

    public void onConstruct(LivingEntity entity){
        original = entity;
        staminaType = MEFEntityAPI.getStaminaTypeByEntityType(entity.getType());
        if (!entity.level().isClientSide){
            setStamina(9999999);
        }

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
        if (!staminaIsPresent())return;
        float targetAmount =  Mth.clamp(amount,0,getStaminaMax());
        if (getStamina() >= 0 && targetAmount ==0){
            getStaminaType().whenZero(this);
        }
        getOriginal().getEntityData().set(STAMINA,targetAmount);
    }

    public void saveNBTData(CompoundTag tag) {
        if (!staminaIsPresent())return;
        tag.putFloat("mef_stamina",getStamina());
    }

    public void loadNBTData(CompoundTag tag) {
        if (!staminaIsPresent())return;
        if (tag.contains("mef_stamina")){
            setStamina(tag.getFloat("mef_stamina"));
        }
    }

    public final void tick() {
        if (original == null)return;

        if (!original.level().isClientSide && staminaIsPresent()){
            if (getStaminaType().canRecover(this)){
                this.setStamina(getStamina() + getStaminaRegen());
            }

        }
    }

}