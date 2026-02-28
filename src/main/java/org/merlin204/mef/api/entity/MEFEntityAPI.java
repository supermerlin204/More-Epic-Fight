package org.merlin204.mef.api.entity;


import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.forgeevent.ExecuteAnimationRegistryEvent;
import org.merlin204.mef.api.forgeevent.MoreStunTypeRegistryEvent;
import org.merlin204.mef.api.forgeevent.ParryAnimationRegistryEvent;
import org.merlin204.mef.api.forgeevent.StaminaTypeRegistryEvent;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.registry.MEFMobEffects;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Map;

/**
 * MEF与实体有关的逻辑集
 */
public class MEFEntityAPI {
    //存储更多硬直动画的map
    private static final Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<?extends StaticAnimation>>> MORE_STUN_TYPE_MAP = Maps.newHashMap();
    //存储耐力类型动画的map
    private static final Map<EntityType<?>, StaminaType> STAMINA_TYPE_MAP = Maps.newHashMap();

    //存储弹反动画的map
    private static final Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES = Maps.newHashMap();
    private static final Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> PARRY_ANIMATIONS_WITH_CLASS = Maps.newHashMap();

    //存储处决动画的map
    private static final Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES = Maps.newHashMap();
    private static final Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> EXECUTE_ANIMATIONS_WITH_CLASS = Maps.newHashMap();

    /**
     * 初始化耐力类型表,提前初始化一次,确保属性正确添加
     */
    public static void initStaminaType(){
        Map<EntityType<?>, StaminaType> staminaTypeRegistry = Maps.newHashMap();

        StaminaTypeRegistryEvent staminaTypeRegistryEvent = new StaminaTypeRegistryEvent(staminaTypeRegistry);
        ModLoader.get().postEvent(staminaTypeRegistryEvent);

        STAMINA_TYPE_MAP.putAll(staminaTypeRegistry);
    }


    /**
     * 逻辑集的初始化
     */
    public static void init(){

        Map<EntityType<?>, StaminaType> staminaTypeRegistry = Maps.newHashMap();

        StaminaTypeRegistryEvent staminaTypeRegistryEvent = new StaminaTypeRegistryEvent(staminaTypeRegistry);
        ModLoader.get().postEvent(staminaTypeRegistryEvent);

        STAMINA_TYPE_MAP.putAll(staminaTypeRegistry);


        Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<?extends StaticAnimation>>> moreStunTypeRegistry = Maps.newHashMap();

        MoreStunTypeRegistryEvent moreStunTypeRegistryEvent = new MoreStunTypeRegistryEvent(moreStunTypeRegistry);
        ModLoader.get().postEvent(moreStunTypeRegistryEvent);

        MORE_STUN_TYPE_MAP.putAll(moreStunTypeRegistry);

        Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> parryWeaponCategory = Maps.newHashMap();
        Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> parryClassMap = Maps.newHashMap();

        ParryAnimationRegistryEvent parryAnimationRegistryEvent = new ParryAnimationRegistryEvent(parryWeaponCategory, parryClassMap);
        ModLoader.get().postEvent(parryAnimationRegistryEvent);

        PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.putAll(parryWeaponCategory);
        PARRY_ANIMATIONS_WITH_CLASS.putAll(parryClassMap);

        Map<WeaponCategory,AnimationManager.AnimationAccessor<?extends StaticAnimation>> executeWeaponCategory = Maps.newHashMap();
        Map<Class<? extends Item>,AnimationManager.AnimationAccessor<?extends StaticAnimation>> executeClassMap = Maps.newHashMap();

        ExecuteAnimationRegistryEvent executeAnimationRegistryEvent = new ExecuteAnimationRegistryEvent(executeWeaponCategory, executeClassMap);
        ModLoader.get().postEvent(executeAnimationRegistryEvent);

        EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES.putAll(executeWeaponCategory);
        EXECUTE_ANIMATIONS_WITH_CLASS.putAll(executeClassMap);


    }

    /**
     * 检查玩家是否能发动弹反
     */
    public static boolean canParried(PlayerPatch<?> playerPatch){
        //TODO 抛个事件
        return playerPatch.getEntityState().canUseSkill();
    }

    /**
     * 检查玩家是否能发动处决
     */
    public static boolean canExecute(PlayerPatch<?> playerPatch){
        //TODO 抛个事件
        if (playerPatch.getTarget() == null)return false;
        if (!playerPatch.getEntityState().canUseSkill())return false;
        LivingEntity target = playerPatch.getTarget();
        if (target.hasEffect(MEFMobEffects.KNOCKDOWN.get())){
            return true;
        }
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
        if (patch != null && patch.getHitAnimation(StunType.KNOCKDOWN) != null){
            if (patch.getAnimator().getPlayerFor(null).getRealAnimation().get() == patch.getHitAnimation(StunType.KNOCKDOWN).get()){
                return true;
            }
        }


        return playerPatch.getEntityState().canUseSkill();
    }

    /**
     * 放置耐力类型,一般情况下别用
     */
    public static void putStaminaTypeByEntityType(EntityType<?> entityType,StaminaType staminaType){
        STAMINA_TYPE_MAP.put(entityType,staminaType);
    }

    /**
     * 获取一个实体所绑定的耐力类型
     */
    public static StaminaType getStaminaTypeByEntityType(EntityType<?> entityType){
        return STAMINA_TYPE_MAP.get(entityType);
    }

    /**
     * 根据实体手中物品尝试播放处决动画,返回是否成功播放
     * 动画选取优先级顺序为主手物品类-主手武器类型-副手物品类-副手武器类型-
     */
    public static boolean tryPlayExecuteAnimation(LivingEntityPatch<?> patch){
        CapabilityItem main = patch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND);
        CapabilityItem off = patch.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND);
        Class<? extends Item> mainClass = patch.getAdvancedHoldingItemStack(InteractionHand.MAIN_HAND).getItem().getClass();
        Class<? extends Item> offClass = patch.getAdvancedHoldingItemStack(InteractionHand.OFF_HAND).getItem().getClass();
        AnimationManager.AnimationAccessor<?extends StaticAnimation> animationAccessor = null;

        if (EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(off.getWeaponCategory())){
            animationAccessor = EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(off.getWeaponCategory());
        }
        if (EXECUTE_ANIMATIONS_WITH_CLASS.containsKey(offClass)){
            animationAccessor = EXECUTE_ANIMATIONS_WITH_CLASS.get(offClass);
        }
        if (EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(main.getWeaponCategory())){
            animationAccessor = EXECUTE_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(main.getWeaponCategory());
        }
        if (EXECUTE_ANIMATIONS_WITH_CLASS.containsKey(mainClass)){
            animationAccessor = EXECUTE_ANIMATIONS_WITH_CLASS.get(mainClass);
        }

        if (animationAccessor != null){
            patch.playAnimationSynchronized(animationAccessor,0);
            return true;
        }
        return false;
    }

    /**
     * 根据实体手中物品尝试播放弹反动画,返回是否成功播放
     * 动画选取优先级顺序为副手物品类-副手武器类型-主手物品类-主手武器类型
     */
    public static boolean tryPlayParryAnimation(LivingEntityPatch<?> patch){
        CapabilityItem main = patch.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND);
        CapabilityItem off = patch.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND);
        Class<? extends Item> mainClass = patch.getAdvancedHoldingItemStack(InteractionHand.MAIN_HAND).getItem().getClass();
        Class<? extends Item> offClass = patch.getAdvancedHoldingItemStack(InteractionHand.OFF_HAND).getItem().getClass();
        AnimationManager.AnimationAccessor<?extends StaticAnimation> animationAccessor = null;
        if (PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(main.getWeaponCategory())){
            animationAccessor = PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(main.getWeaponCategory());
        }
        if (PARRY_ANIMATIONS_WITH_CLASS.containsKey(mainClass)){
            animationAccessor = PARRY_ANIMATIONS_WITH_CLASS.get(mainClass);
        }
        if (PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.containsKey(off.getWeaponCategory())){
            animationAccessor = PARRY_ANIMATIONS_WITH_WEAPON_CATEGORIES.get(off.getWeaponCategory());
        }
        if (PARRY_ANIMATIONS_WITH_CLASS.containsKey(offClass)){
            animationAccessor = PARRY_ANIMATIONS_WITH_CLASS.get(offClass);
        }

        if (animationAccessor != null){
            patch.playAnimationSynchronized(animationAccessor,0);
            return true;
        }
        return false;
    }


    /**
     * 获取实体的更多硬直动画
     */
    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> getMoreStunAnimation(LivingEntityPatch<?> entityPatch, MoreStunType moreStunType){
        if (MORE_STUN_TYPE_MAP.containsKey(entityPatch.getOriginal().getType())){
            return MORE_STUN_TYPE_MAP.get(entityPatch.getOriginal().getType()).get(moreStunType);
        }
        return null;
    }
    /**
     * 使一个实体播放更多硬直动画,返回是否成功播放硬直
     */
    public static boolean playMoreStunAnimation(LivingEntityPatch<?> entityPatch, MoreStunType moreStunType){
        if (getMoreStunAnimation(entityPatch,moreStunType) != null){
            entityPatch.playAnimationSynchronized(getMoreStunAnimation(entityPatch,moreStunType),0);
            return true;
        }
        return false;
    }

    /**
     * 使一个实体被弹反的方法,返回是否成功被弹反
     */
    public static boolean beParried(LivingEntity livingEntity){
        //TODO Arc来补个判断攻击方向
        MoreStunType moreStunType = MoreStunType.BE_PARRIED_L;
        LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
        if (livingEntityPatch != null && getMoreStunAnimation(livingEntityPatch,moreStunType) != null){
            livingEntityPatch.playAnimationSynchronized(getMoreStunAnimation(livingEntityPatch,moreStunType),0);
            return true;
        }
        return livingEntity.addEffect(new MobEffectInstance(MEFMobEffects.STUN.get(),80,0));
    }

    /**
     * 使一个实体倒地的方法,返回是否成功倒地
     */
    public static boolean beKnockdown(LivingEntity livingEntity){
        LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
        if (livingEntityPatch != null && livingEntityPatch.getHitAnimation(StunType.KNOCKDOWN)!= null){
            livingEntityPatch.playAnimationSynchronized(livingEntityPatch.getHitAnimation(StunType.KNOCKDOWN),0);
            return true;
        }
        return livingEntity.addEffect(new MobEffectInstance(MEFMobEffects.KNOCKDOWN.get(),100,0));
    }

}
