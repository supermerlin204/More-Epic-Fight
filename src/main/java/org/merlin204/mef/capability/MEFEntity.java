package org.merlin204.mef.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.epicfight.IMEFPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class MEFEntity {

    public static final MEFEntity EMPTY_MEF_ENTITY = new MEFEntity();

    //耐力值
    protected static EntityDataAccessor<Float> STAMINA;
    //动画播放速度(只适用于EF实体)
    protected static EntityDataAccessor<Float> ANIMATION_SPEED;
    //对峙时移动速度
    protected static EntityDataAccessor<Float> WONDER_SPEED;
    //对峙时间
    protected static EntityDataAccessor<Integer> WONDER_TIME;
    //倒地时间(非玩家的MobEffect持续时间不会双端同步,因此需要单独记录)
    protected static EntityDataAccessor<Integer> KNOCKDOWN_TIME;
    //是否在被处决时死亡
    private boolean isDoomed = false;
    //被处决实体的伤害源记录
    private DamageSource executionDamageSource = null;
    //正在被处决
    private boolean isBeingExecuted = false;

    public static void initLivingEntityDataAccessor() {
        STAMINA = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
        ANIMATION_SPEED = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
        WONDER_SPEED = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
        WONDER_TIME = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
        KNOCKDOWN_TIME = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    }

    private LivingEntity original;
    private StaminaType staminaType;

    //对峙随机变向,为true的时候会随机改变对峙的移动方向
    protected boolean randomWonder;

    public static void createSyncedEntityData(LivingEntity livingentity) {
        livingentity.getEntityData().define(STAMINA, 0.0F);
        livingentity.getEntityData().define(ANIMATION_SPEED, 1F);
        livingentity.getEntityData().define(WONDER_SPEED, 0.5F);
        livingentity.getEntityData().define(WONDER_TIME, 0);
        livingentity.getEntityData().define(KNOCKDOWN_TIME, 0);
    }

    /**
     * 重置所有速度相关的属性 (动画调速、对峙移动速度、对峙持续时间)
     * 用于动画结束或被切换时调用
     */
    public void resetSpeedProperties() {
        if (original == null) return;
        this.setAnimationSpeed(1.0F);
        this.setWonderSpeed(0.5F);
        this.setWonderTime(0);
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
        staminaType = MEFEntityAPI.getStaminaTypeByEntity(entity);
        if (!entity.level().isClientSide && staminaIsPresent()){
            getOriginal().getEntityData().set(STAMINA,staminaType.getDefaultMax());
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

    public final boolean isWonder(){
        return getWonderTime() > 0;
    }

    public final boolean isRandomWonder(){
        return randomWonder;
    }

    public final void setRandomWonder(boolean b){
        randomWonder = b;
    }

    public final int getWonderTime(){
        if (original == null){
            return 0;
        }
        return getOriginal().getEntityData().get(WONDER_TIME);
    }

    public final void setWonderTime(int amount){
        if (original == null){
            return;
        }
        getOriginal().getEntityData().set(WONDER_TIME,amount);
    }

    public final int getKnockdownTime(){
        if (original == null){
            return 0;
        }
        return getOriginal().getEntityData().get(KNOCKDOWN_TIME);
    }

    public final void setKnockdownTime(int amount){
        if (original == null){
            return;
        }
        getOriginal().getEntityData().set(KNOCKDOWN_TIME,amount);
    }

    public final float getWonderSpeed(){
        if (original == null){
            return 1;
        }
        return getOriginal().getEntityData().get(WONDER_SPEED);
    }

    public final void setWonderSpeed(float amount){
        if (original == null){
            return;
        }
        getOriginal().getEntityData().set(WONDER_SPEED,amount);
    }

    public final float getAnimationSpeed(){
        if (original == null){
            return 1;
        }
        return getOriginal().getEntityData().get(ANIMATION_SPEED);
    }

    public final void setAnimationSpeed(float amount){
        if (original == null){
            return;
        }
        getOriginal().getEntityData().set(ANIMATION_SPEED,amount);
    }

    public final float getStamina(){
        if (original == null){
            return 0;
        }
        return getOriginal().getEntityData().get(STAMINA);
    }

    public final void setStamina(float amount){
        if (original == null){
            return;
        }
        if (!staminaIsPresent())return;
        float targetAmount =  Mth.clamp(amount,0,getStaminaMax());
        if (getStamina() > 0 && targetAmount == 0){
            getStaminaType().whenZero(this);
        }
        getOriginal().getEntityData().set(STAMINA,targetAmount);
    }

    public void setBeingExecuted(boolean beingExecuted) {
        this.isBeingExecuted = beingExecuted;
    }

    public boolean isBeingExecuted() {
        return this.isBeingExecuted;
    }

    public void markDoomed(DamageSource source) {
        this.isDoomed = true;
        this.executionDamageSource = source;
    }

    public boolean isDoomed() {
        return this.isDoomed;
    }

    public DamageSource getExecutionDamageSource() {
        return this.executionDamageSource;
    }

    public void clearDoomed() {
        this.isDoomed = false;
        this.executionDamageSource = null;
    }

    public void saveNBTData(CompoundTag tag) {
        if (!staminaIsPresent())return;
        tag.putFloat("mef_stamina",getStamina());
    }

    public final void loadNBTData(CompoundTag tag) {
        if (!staminaIsPresent())return;
        if (tag.contains("mef_stamina")){
            setStamina(tag.getFloat("mef_stamina"));
        }
    }

    public final void tick() {
        if (original == null)return;
        if (!original.level().isClientSide){
            if (getWonderTime() > 0){
                setWonderTime(getWonderTime() - 1);
            }
            if (staminaIsPresent() && getStaminaType().canRecover(this)){
                this.setStamina(getStamina() + getStaminaRegen());
            }
        }
    }
}