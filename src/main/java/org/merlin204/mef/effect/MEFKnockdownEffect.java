package org.merlin204.mef.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;


/**
 * 倒地效果,用来控制非史诗战斗实体,算作增益避免被实体拦截,MEF开头防止重名,额外执行倒地结束对应的MEF方法
 */
public class MEFKnockdownEffect extends MobEffect {

    public MEFKnockdownEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x808080);
    }

    // 每个效果 tick 执行
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        //完全定住实体
        entity.setNoActionTime(20);
        entity.setDeltaMovement(0, 0, 0);
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setTarget(null);           // 清除目标
            mob.getNavigation().stop();     // 停止当前路径
        }

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    // 效果结束时
    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        if (entity.level().isClientSide) return;
        //恢复
        entity.setNoActionTime(0);
        if (entity instanceof Mob mob) {
            mob.setNoAi(false);
        }
        if (MEFEntityAPI.getStaminaTypeByEntityType(entity.getType()) != null) {
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entity);
            mefEntity.getStaminaType().whenKnockDownEnd(mefEntity);
        }
    }

}
