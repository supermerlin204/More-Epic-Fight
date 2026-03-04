package org.merlin204.mef.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.merlin204.mef.api.entity.MEFEntityAPI;
import org.merlin204.mef.capability.MEFCapabilities;
import org.merlin204.mef.capability.MEFEntity;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.EnumSet;


/**
 * EF实体使用的对峙Goal,如果是IMEFPatch的话会强制给一个,不用手动加
 */
public class PatchEntityWonderGoal extends Goal {
    private final LivingEntityPatch<?> entityPatch;

    public PatchEntityWonderGoal(LivingEntityPatch<?> entityPatch) {
        this.entityPatch = entityPatch;
    }


    @Override
    public @NotNull EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE, Flag.LOOK);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = entityPatch.getTarget();
        if (target == null || !target.isAlive())return false;
        if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
            return mefEntity.getWonderTime() > 0;
        }
        return false;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entityPatch.getTarget();
        if (target == null || !target.isAlive())return false;
        if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
            return mefEntity.getWonderTime() > 0;
        }
        return false;
    }

    public void tick() {
        if (entityPatch.getEntityState().inaction()) return;

        LivingEntity target = entityPatch.getTarget();
        LivingEntity entity = entityPatch.getOriginal();
        Vec3 targetPos = target.position();
        Vec3 entityPos = entity.position();

        Vec3 toTarget = targetPos.subtract(entityPos).normalize();
        double targetYaw = MathUtils.getYRotOfVector(toTarget);

        float moveSpeed = (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);

        float random = entityPatch.getOriginal().getRandom().nextFloat();
        boolean isRandom = false;

        if (MEFEntityAPI.getStaminaTypeByEntity(entityPatch.getOriginal()) != null){
            MEFEntity mefEntity = MEFCapabilities.getMEFEntity(entityPatch.getOriginal());
            isRandom = mefEntity.getRandomWonder();
            moveSpeed = moveSpeed * mefEntity.getWonderSpeed();
        }

        if (random > 0.99 && isRandom){
            moveSpeed = -moveSpeed;
        }

        float currentYaw = entity.getYRot();
        double deltaYaw = targetYaw - currentYaw;

        while (deltaYaw < -180) deltaYaw += 360;

        while (deltaYaw > 180) deltaYaw -= 360;


        float rotationStep = (float) (Math.signum(deltaYaw) * Math.min(3F, Math.abs(deltaYaw) * 0.3F));

        if (Math.abs(deltaYaw) > 45){
            rotationStep = (float) (Math.signum(deltaYaw) * Math.min(10F, Math.abs(deltaYaw) * 3F));
        }

        entity.setYRot(currentYaw + rotationStep);


        Vec3 tangent = new Vec3(-toTarget.z, 0, toTarget.x).normalize();


        Vec3 moveVec = tangent.scale(moveSpeed)
                .add(0, (target.getY() - entity.getY()) * 0.02, 0);

        entity.move(MoverType.SELF, moveVec);
    }
}
