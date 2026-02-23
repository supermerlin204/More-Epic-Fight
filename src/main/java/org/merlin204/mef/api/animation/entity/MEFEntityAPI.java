package org.merlin204.mef.api.animation.entity;


import com.google.common.collect.Maps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.forgeevent.MoreStunTypeRegistryEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;

/**
 * MEF与实体有关的逻辑集
 */
public class MEFEntityAPI {
    private static final Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<?extends StaticAnimation>>> MORE_STUN_TYPE_MAP = Maps.newHashMap();

    /**
     * 逻辑集的初始化
     */
    public static void init(){
        Map<EntityType<?>, Map<MoreStunType, AnimationManager.AnimationAccessor<?extends StaticAnimation>>> registry = Maps.newHashMap();

        MoreStunTypeRegistryEvent moreStunTypeRegistryEvent = new MoreStunTypeRegistryEvent(registry);
        ModLoader.get().postEvent(moreStunTypeRegistryEvent);

        MORE_STUN_TYPE_MAP.putAll(registry);
    }

    /**
     * 获取实体的更多硬直动画
     */
    public static AnimationManager.AnimationAccessor<?extends StaticAnimation> getMoreStunAnimation(LivingEntityPatch<?> entityPatch, MoreStunType moreStunType){
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
     * 使一个实体被弹反的方法,返回是否成功播放弹反动画
     */
    public static boolean beParried(LivingEntityPatch<?> entityPatch){
        //TODO Arc来补个判断攻击方向
        MoreStunType moreStunType = MoreStunType.BE_PARRIED_L;
        if (getMoreStunAnimation(entityPatch,moreStunType) != null){
            entityPatch.playAnimationSynchronized(getMoreStunAnimation(entityPatch,moreStunType),0);
            return true;
        }
        return false;
    }

}
