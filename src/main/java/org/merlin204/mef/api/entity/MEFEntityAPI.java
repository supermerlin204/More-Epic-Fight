package org.merlin204.mef.api.entity;

import com.google.common.collect.Maps;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import org.merlin204.mef.api.animation.MEFAttackAnalyzer;
import org.merlin204.mef.api.execution.ExecutionAnimSet;
import org.merlin204.mef.api.execution.MEFExecutionRegistry;
import org.merlin204.mef.api.forgeevent.*;
import org.merlin204.mef.api.stamina.StaminaType;
import org.merlin204.mef.client.gui.BossBarRenderer;
import org.merlin204.mef.client.gui.MEFBossBarManager;
import org.merlin204.mef.epicfight.IMEFPatch;
import org.merlin204.mef.registry.MEFMobEffects;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.damagesource.StunType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        MEFExecutionRegistry.fireRegistryEvent();
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        Map<EntityType<?>, BossBarRenderer> bossBarRegistry = Maps.newHashMap();
        BossBarRegistryEvent bossBarRegistryEvent = new BossBarRegistryEvent(bossBarRegistry);
        ModLoader.get().postEvent(bossBarRegistryEvent);

        MEFBossBarManager.RENDERER_REGISTRY.putAll(bossBarRegistry);
    }

    /**
     * 检查玩家是否能发动弹反
     */
    public static boolean canParried(PlayerPatch<?> playerPatch){
        boolean baseResult = playerPatch.getEntityState().canUseSkill();

        PlayerCanParryEvent event = new PlayerCanParryEvent(playerPatch, baseResult);
        MinecraftForge.EVENT_BUS.post(event);

        return event.canParry();
    }

    /**
     * 检查玩家是否能发动处决
     */
    @Nullable
    public static LivingEntity getNearbyExecutableEntity(PlayerPatch<?> playerPatch) {
        LivingEntity player = playerPatch.getOriginal();

        LivingEntity currentTarget = playerPatch.getTarget();
        if (currentTarget != null && currentTarget.isAlive()) {
            if (player.distanceTo(currentTarget) <= currentTarget.getBbWidth() + 2.5F) {
                if (canBeExecute(currentTarget)) {
                    return currentTarget;
                }
            }
        }

        AABB searchBox = player.getBoundingBox().inflate(3.0D, 2.0D, 3.0D);
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive() && canBeExecute(entity));

        LivingEntity bestTarget = null;
        double minScore = Double.MAX_VALUE;

        Vec3 lookVec = player.getLookAngle();
        for (LivingEntity entity : nearbyEntities) {
            Vec3 dirToEntity = entity.position().subtract(player.position()).normalize();
            double dotProduct = lookVec.dot(dirToEntity);

            if (dotProduct > 0) {
                double distanceSq = player.distanceToSqr(entity);
                double anglePenalty = (1.0 - dotProduct) * 10.0;
                double score = distanceSq + anglePenalty;

                if (score < minScore) {
                    minScore = score;
                    bestTarget = entity;
                }
            }
        }

        return bestTarget;
    }

    public static boolean canExecute(PlayerPatch<?> playerPatch, @Nullable LivingEntity target) {
        boolean baseResult = false;

        if (target != null && playerPatch.getEntityState().canUseSkill()) {
            if (target.distanceTo(playerPatch.getOriginal()) <= target.getBbWidth() + 2.5F) {
                baseResult = canBeExecute(target);
            }
        }

        PlayerCanExecuteEvent event = new PlayerCanExecuteEvent(playerPatch, target, baseResult);
        MinecraftForge.EVENT_BUS.post(event);

        return event.canExecute();
    }

    /**
     * 检查当前实体是否能被处决
     */
    public static boolean canBeExecute(LivingEntity livingEntity){
        boolean baseResult = false;
        if (livingEntity != null) {
            LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
            if (targetPatch != null && targetPatch.getHitAnimation(StunType.KNOCKDOWN) != null) {
                baseResult = Objects.requireNonNull(targetPatch.getAnimator().getPlayerFor(null)).getRealAnimation().get() == targetPatch.getHitAnimation(StunType.KNOCKDOWN).get();
            } else {
                baseResult = livingEntity.hasEffect(MEFMobEffects.KNOCKDOWN.get());
            }
        }

        EntityCanBeExecutedEvent event = new EntityCanBeExecutedEvent(livingEntity, baseResult);
        MinecraftForge.EVENT_BUS.post(event);

        return event.canBeExecuted();
    }

    /**
     * 放置耐力类型,一般情况下别用
     */
    public static void putStaminaTypeByEntityType(EntityType<?> entityType,StaminaType staminaType){
        STAMINA_TYPE_MAP.put(entityType,staminaType);
    }

    /**
     * 获取一个实体所绑定的耐力类型
     * 加 @Nullable 注解和 null 检查，防止处理非实体伤害（如/kill，跌落，虚空）时发生崩溃
     */
    @Nullable
    public static StaminaType getStaminaTypeByEntity(@Nullable Entity entity){
        if (entity == null) {
            return null; // 遇到无来源伤害，安全返回空，让外层自行判定
        }
        if (EpicFightCapabilities.getEntityPatch(entity,LivingEntityPatch.class) instanceof IMEFPatch imefPatch){
            return imefPatch.getStaminaType();
        }
        return STAMINA_TYPE_MAP.get(entity.getType());
    }

    public static boolean tryPlayExecuteAnimation(LivingEntityPatch<?> patch){
        return tryPlayExecuteAnimation(patch, null);
    }

    /**
     * 根据实体手中物品尝试播放处决动画,返回是否成功播放
     * 动画选取优先级顺序为主手物品类-主手武器类型-副手物品类-副手武器类型
     */
    public static boolean tryPlayExecuteAnimation(LivingEntityPatch<?> attacker, @Nullable LivingEntityPatch<?> victim) {
        ExecutionAnimSet animSet = getExecutionAnimSet(attacker, victim);

        if (animSet != null) {
            if (victim != null) {
                correctExecutionPosition(attacker.getOriginal(), victim.getOriginal());

                if (animSet.victimAnim() != null) {
                    victim.playAnimationSynchronized(animSet.victimAnim(), 0);
                }
            }

            if (animSet.attackerAnim() != null) {
                attacker.playAnimationSynchronized(animSet.attackerAnim(), 0);
            }

            return true;
        }

        return false;
    }

    /**
     * 处决位置朝向修正
     */
    public static void correctExecutionPosition(LivingEntity aEnt, LivingEntity vEnt) {
        Vec3 dir = vEnt.position().subtract(aEnt.position()).multiply(1, 0, 1);
        if (dir.lengthSqr() > 0.0001) {
            dir = dir.normalize();
        } else {
            dir = Vec3.directionFromRotation(0, aEnt.getYRot()).multiply(1, 0, 1).normalize();
        }

        double optimalDist = (aEnt.getBbWidth() + vEnt.getBbWidth()) / 2.0 + 0.5;
        double minDist = (aEnt.getBbWidth() + vEnt.getBbWidth()) / 2.0;

        boolean foundSafePos = false;
        Vec3 safePos = aEnt.position();

        for (double d = optimalDist; d >= minDist; d -= 0.2) {
            Vec3 testPos = vEnt.position().subtract(dir.scale(d));
            testPos = new Vec3(testPos.x, aEnt.getY(), testPos.z);

            AABB testBox = aEnt.getBoundingBox().move(testPos.x - aEnt.getX(), 0, testPos.z - aEnt.getZ());

            if (aEnt.level().noCollision(aEnt, testBox)) {
                safePos = testPos;
                foundSafePos = true;
                break;
            }
        }

        if (foundSafePos) {
            aEnt.teleportTo(safePos.x, safePos.y, safePos.z);
        }

        float targetYaw = (float) (Mth.atan2(dir.z, dir.x) * (180D / Math.PI)) - 90.0F;

        aEnt.setYRot(targetYaw);
        aEnt.yBodyRot = targetYaw;
        aEnt.yHeadRot = targetYaw;
        aEnt.yRotO = targetYaw;

        float vRot = targetYaw + 180.0F;
        vEnt.setYRot(vRot);
        vEnt.yBodyRot = vRot;
        vEnt.yHeadRot = vRot;
        vEnt.yRotO = vRot;
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
     * 使一个实体被弹反的方法，根据攻击方向播放对应的弹反硬直
     * @param attacker 被弹反的实体（攻击者）
     * @param defender 发动弹反的玩家（防御者）。如果传入null，则默认播放中段硬直。
     * @return 是否成功播放动画
     */
    public static boolean beParried(LivingEntity attacker, @Nullable LivingEntityPatch<?> defender) {
        MoreStunType moreStunType = MoreStunType.BE_PARRIED_M;

        if (defender != null) {
            MEFAttackAnalyzer.AttackDirection direction = MEFAttackAnalyzer.analyzeAttackDirection(defender, attacker);

            moreStunType = switch (direction) {
                case LEFT_ATTACK, LEFT_SLIGHT_ATTACK, LEFT_SIDE -> MoreStunType.BE_PARRIED_R;
                case RIGHT_ATTACK, RIGHT_SLIGHT_ATTACK, RIGHT_SIDE -> MoreStunType.BE_PARRIED_L;
                default -> MoreStunType.BE_PARRIED_M;
            };
        }

        LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(attacker, LivingEntityPatch.class);

        if (attackerPatch != null && getMoreStunAnimation(attackerPatch, moreStunType) != null) {
            attackerPatch.playAnimationSynchronized(getMoreStunAnimation(attackerPatch, moreStunType), 0);
            return true;
        }

        if (attackerPatch != null && moreStunType != MoreStunType.BE_PARRIED_M && getMoreStunAnimation(attackerPatch, MoreStunType.BE_PARRIED_M) != null) {
            attackerPatch.playAnimationSynchronized(getMoreStunAnimation(attackerPatch, MoreStunType.BE_PARRIED_M), 0);
            return true;
        }

        return attacker.addEffect(new MobEffectInstance(MEFMobEffects.STUN.get(), 80, 0));
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

    /**
     * 获取实体的处决动作表
     */
    @Nullable
    public static ExecutionAnimSet getExecutionAnimSet(LivingEntityPatch<?> patch) {
        return getExecutionAnimSet(patch, null);
    }

    /**
     * 获取实体的处决动作表 (全新匹配逻辑)
     */
    @Nullable
    public static ExecutionAnimSet getExecutionAnimSet(LivingEntityPatch<?> attacker, @Nullable LivingEntityPatch<?> victim) {
        return MEFExecutionRegistry.getExecutionSet(attacker, victim);
    }

}